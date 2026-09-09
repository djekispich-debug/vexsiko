package rtx.vexsiko.api.modules.impl.Visuals.killeffect;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import rtx.vexsiko.api.events.impl.render.WorldRenderEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 3D Kill Effect renderer with support for multiple simultaneous effects.
 * Renders expanding rings, rotating sphere, and energy spikes in world space.
 * Properly handles depth testing and depth buffer interaction.
 */
public final class KillEffectScanRenderer {
    private static final long EFFECT_DURATION_MS = 3500L;
    private static boolean disabled = false;
    private static final List<ActiveEffect> activeEffects = new CopyOnWriteArrayList<>();

    private KillEffectScanRenderer() {}

    /**
     * Trigger a kill effect at the specified world position.
     * Multiple effects can be active simultaneously.
     * @param pos World position where effect appears
     * @param speed Speed multiplier (0.25-2.0)
     */
    public static void ping(Vec3d pos, float speed) {
        if (pos == null || MinecraftClient.getInstance().world == null) {
            return;
        }
        long duration = (long)(EFFECT_DURATION_MS / Math.clamp(speed, 0.25f, 2.0f));
        activeEffects.add(new ActiveEffect(pos, duration, System.currentTimeMillis()));
    }

    /**
     * Main render call from KillEffect.onWorldRender().
     * Renders all active 3D kill effects.
     */
    public static void render(WorldRenderEvent event, float speedMult, int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8) {
        if (disabled || activeEffects.isEmpty()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || event.getCamera() == null) {
            return;
        }

        try {
            Vec3d cameraPos = event.getCamera().getCameraPos();

            // Create immediate vertex consumer provider
            VertexConsumerProvider.Immediate vertexConsumers = VertexConsumerProvider.immediate(new java.util.HashMap<>());

            // Process and render active effects
            Iterator<ActiveEffect> it = activeEffects.iterator();
            while (it.hasNext()) {
                ActiveEffect effect = it.next();
                long elapsed = System.currentTimeMillis() - effect.startTime;
                
                // Remove expired effects
                if (elapsed >= effect.durationMs) {
                    it.remove();
                    continue;
                }

                float progress = (float)elapsed / effect.durationMs;
                float animationTime = (float)elapsed / 1000f;
                
                // Render the 3D geometry for this effect
                renderEffectGeometry(
                    effect.position, cameraPos,
                    progress, animationTime,
                    c1, c2, c3, c4, c5, c6, c7, c8,
                    vertexConsumers
                );
            }

            // Draw all accumulated vertices
            vertexConsumers.draw();
        } catch (Exception e) {
            System.err.println("[KillEffectScanRenderer] Render failed: " + e.getMessage());
            e.printStackTrace();
            disabled = true;
        }
    }

    /**
     * Render complete 3D effect with multiple geometry layers.
     * Uses transparent/additive blending for energy effect appearance.
     */
    private static void renderEffectGeometry(
        Vec3d worldPos, Vec3d cameraPos, float progress, float time,
        int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8,
        VertexConsumerProvider vertexConsumers
    ) {
        // Get render layer with proper depth testing and additive blending
        VertexConsumer vc = vertexConsumers.getBuffer(
            RenderLayer.getBeaconBeam(Identifier.of("minecraft", "textures/white.png"), true)
        );

        Vec3d relPos = worldPos.subtract(cameraPos);
        float x = (float)relPos.x;
        float y = (float)relPos.y;
        float z = (float)relPos.z;

        // === Phase 1: Spawn pulse (0-0.15) ===
        float spawnAlpha = spawnEnvelope(progress);
        if (spawnAlpha > 0) {
            float sphereRadius = 0.3f + progress * 0.5f;
            drawSphere(vc, x, y, z, sphereRadius, c1, spawnAlpha * 0.8f, 12);
        }

        // === Phase 2: Expanding ring wave (0-0.4) ===
        float ringAlpha = ringEnvelope(progress);
        if (ringAlpha > 0) {
            float ringRadius = 0.2f + progress * 2.0f;
            float ringThickness = 0.15f * (1.0f - progress * 0.8f);
            drawRing(vc, x, y, z, ringRadius, ringThickness, c2, ringAlpha, 32);
        }

        // === Phase 3: Rotating energy core (0.15-0.7) ===
        float coreAlpha = coreEnvelope(progress);
        if (coreAlpha > 0) {
            float coreRadius = 0.4f + (float)Math.sin(progress * Math.PI) * 0.3f;
            float rotation = time * 180f; // rotation speed
            drawRotatingTorus(vc, x, y, z, coreRadius, 0.12f, c3, coreAlpha, 24, rotation);
            drawRotatingSpikes(vc, x, y, z, coreRadius * 1.2f, c4, coreAlpha * 0.6f, 6, rotation);
        }

        // === Phase 4: Secondary energy shells (0.3-0.65) ===
        float shellAlpha = shellEnvelope(progress);
        if (shellAlpha > 0) {
            float shell1Radius = 0.5f + progress * 1.5f;
            drawRing(vc, x, y + 0.3f, z, shell1Radius, 0.1f, c5, shellAlpha * 0.5f, 28);
            
            float shell2Radius = 0.35f + progress * 1.2f;
            drawRing(vc, x, y - 0.2f, z, shell2Radius, 0.08f, c6, shellAlpha * 0.4f, 24);
        }

        // === Phase 5: Fade-out rays (0.5-1.0) ===
        float fadeAlpha = fadeEnvelope(progress);
        if (fadeAlpha > 0) {
            drawRadialRays(vc, x, y, z, 0.6f + progress * 0.5f, c7, fadeAlpha * 0.4f, 8);
        }
    }

    // === Animation envelope functions for smooth phase transitions ===

    private static float spawnEnvelope(float t) {
        if (t > 0.15f) return 0f;
        return (float)Math.sin(t / 0.15f * Math.PI) * 0.8f;
    }

    private static float ringEnvelope(float t) {
        if (t < 0.05f || t > 0.45f) return 0f;
        float local = (t - 0.05f) / 0.4f;
        return (float)Math.sin(local * Math.PI);
    }

    private static float coreEnvelope(float t) {
        if (t < 0.1f) return 0f;
        if (t > 0.75f) return 0f;
        return easeOutCubic(Math.min((t - 0.1f) / 0.65f, 1.0f)) * 0.9f;
    }

    private static float shellEnvelope(float t) {
        if (t < 0.25f || t > 0.7f) return 0f;
        float local = (t - 0.25f) / 0.45f;
        return (float)Math.sin(local * Math.PI);
    }

    private static float fadeEnvelope(float t) {
        if (t < 0.45f) return 0f;
        return (float)Math.pow(1.0f - t, 2.5f);
    }

    private static float easeOutCubic(float t) {
        return 1.0f - (float)Math.pow(1.0f - t, 3.0f);
    }

    // === Geometry rendering functions ===

    /**
     * Draw a sphere made of quads in spherical coordinates.
     */
    private static void drawSphere(VertexConsumer vc, float cx, float cy, float cz, 
                                   float radius, int color, float alpha, int segments) {
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = Math.clamp(alpha, 0.0f, 1.0f);
        int packed = packColor(r, g, b, a);

        int rings = segments / 2;
        for (int i = 0; i < rings; i++) {
            float lat0 = (float)Math.PI * i / rings;
            float lat1 = (float)Math.PI * (i + 1) / rings;
            
            for (int j = 0; j < segments; j++) {
                float lon0 = 2.0f * (float)Math.PI * j / segments;
                float lon1 = 2.0f * (float)Math.PI * (j + 1) / segments;
                
                Vector3f p0 = spherePoint(radius, lat0, lon0);
                Vector3f p1 = spherePoint(radius, lat0, lon1);
                Vector3f p2 = spherePoint(radius, lat1, lon1);
                Vector3f p3 = spherePoint(radius, lat1, lon0);
                
                addQuad(vc, cx, cy, cz, p0, p1, p2, p3, packed);
            }
        }
    }

    /**
     * Draw a horizontal ring (torus cross-section).
     */
    private static void drawRing(VertexConsumer vc, float cx, float cy, float cz, 
                                float radius, float thickness, int color, float alpha, int segments) {
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = Math.clamp(alpha, 0.0f, 1.0f);
        int packed = packColor(r, g, b, a);

        for (int i = 0; i < segments; i++) {
            float angle0 = 2.0f * (float)Math.PI * i / segments;
            float angle1 = 2.0f * (float)Math.PI * (i + 1) / segments;
            
            float x0 = (float)Math.cos(angle0) * radius;
            float z0 = (float)Math.sin(angle0) * radius;
            float x1 = (float)Math.cos(angle1) * radius;
            float z1 = (float)Math.sin(angle1) * radius;
            
            Vector3f p0 = new Vector3f(x0, thickness, z0);
            Vector3f p1 = new Vector3f(x1, thickness, z1);
            Vector3f p2 = new Vector3f(x1, -thickness, z1);
            Vector3f p3 = new Vector3f(x0, -thickness, z0);
            
            addQuad(vc, cx, cy, cz, p0, p1, p2, p3, packed);
        }
    }

    /**
     * Draw a rotating torus (donut shape).
     */
    private static void drawRotatingTorus(VertexConsumer vc, float cx, float cy, float cz, 
                                         float majorRadius, float minorRadius, int color, 
                                         float alpha, int segments, float rotation) {
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = Math.clamp(alpha, 0.0f, 0.7f);
        int packed = packColor(r, g, b, a);

        float rotRad = (float)Math.toRadians(rotation);
        
        for (int i = 0; i < segments; i++) {
            float angle0 = 2.0f * (float)Math.PI * i / segments;
            float angle1 = 2.0f * (float)Math.PI * (i + 1) / segments;
            
            float x0 = (float)Math.cos(angle0) * majorRadius;
            float z0 = (float)Math.sin(angle0) * majorRadius;
            float x1 = (float)Math.cos(angle1) * majorRadius;
            float z1 = (float)Math.sin(angle1) * majorRadius;
            
            // Apply Y-axis rotation
            float rx0 = x0 * (float)Math.cos(rotRad) - z0 * (float)Math.sin(rotRad);
            float rz0 = x0 * (float)Math.sin(rotRad) + z0 * (float)Math.cos(rotRad);
            float rx1 = x1 * (float)Math.cos(rotRad) - z1 * (float)Math.sin(rotRad);
            float rz1 = x1 * (float)Math.sin(rotRad) + z1 * (float)Math.cos(rotRad);
            
            Vector3f p0 = new Vector3f(rx0 + minorRadius * (float)Math.cos(angle0), minorRadius, rz0 + minorRadius * (float)Math.sin(angle0));
            Vector3f p1 = new Vector3f(rx1 + minorRadius * (float)Math.cos(angle1), minorRadius, rz1 + minorRadius * (float)Math.sin(angle1));
            Vector3f p2 = new Vector3f(rx1 - minorRadius * (float)Math.cos(angle1), -minorRadius, rz1 - minorRadius * (float)Math.sin(angle1));
            Vector3f p3 = new Vector3f(rx0 - minorRadius * (float)Math.cos(angle0), -minorRadius, rz0 - minorRadius * (float)Math.sin(angle0));
            
            addQuad(vc, cx, cy, cz, p0, p1, p2, p3, packed);
        }
    }

    /**
     * Draw rotating energy spikes radiating from center.
     */
    private static void drawRotatingSpikes(VertexConsumer vc, float cx, float cy, float cz, 
                                          float length, int color, float alpha, int spikeCount, float rotation) {
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = Math.clamp(alpha, 0.0f, 1.0f);
        int packed = packColor(r, g, b, a);

        float rotRad = (float)Math.toRadians(rotation);
        
        for (int i = 0; i < spikeCount; i++) {
            float angle = 2.0f * (float)Math.PI * i / spikeCount;
            float nextAngle = 2.0f * (float)Math.PI * (i + 1) / spikeCount;
            
            float rotatedAngle = angle + rotRad;
            float rotatedNextAngle = nextAngle + rotRad;
            
            float tipX = (float)Math.cos(rotatedAngle) * length;
            float tipZ = (float)Math.sin(rotatedAngle) * length;
            float nextTipX = (float)Math.cos(rotatedNextAngle) * length;
            float nextTipZ = (float)Math.sin(rotatedNextAngle) * length;
            
            float baseW = 0.12f;
            float baseBX = (float)Math.cos(rotatedAngle) * baseW;
            float baseBZ = (float)Math.sin(rotatedAngle) * baseW;
            float baseNX = (float)Math.cos(rotatedNextAngle) * baseW;
            float baseNZ = (float)Math.sin(rotatedNextAngle) * baseW;
            
            Vector3f p0 = new Vector3f(baseBX, 0.05f, baseBZ);
            Vector3f p1 = new Vector3f(baseNX, 0.05f, baseNZ);
            Vector3f p2 = new Vector3f(nextTipX, 0.4f, nextTipZ);
            Vector3f p3 = new Vector3f(tipX, 0.4f, tipZ);
            
            addQuad(vc, cx, cy, cz, p0, p1, p2, p3, packed);
        }
    }

    /**
     * Draw radial rays emanating from center point.
     */
    private static void drawRadialRays(VertexConsumer vc, float cx, float cy, float cz, 
                                      float length, int color, float alpha, int rayCount) {
        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = Math.clamp(alpha, 0.0f, 1.0f);
        int packed = packColor(r, g, b, a);

        for (int i = 0; i < rayCount; i++) {
            float angle = 2.0f * (float)Math.PI * i / rayCount;
            float nextAngle = 2.0f * (float)Math.PI * (i + 1) / rayCount;
            
            float tipX = (float)Math.cos(angle) * length;
            float tipZ = (float)Math.sin(angle) * length;
            float nextTipX = (float)Math.cos(nextAngle) * length;
            float nextTipZ = (float)Math.sin(nextAngle) * length;
            
            Vector3f p0 = new Vector3f(0, 0, 0);
            Vector3f p1 = new Vector3f(0, 0.05f, 0);
            Vector3f p2 = new Vector3f(nextTipX, 0.3f, nextTipZ);
            Vector3f p3 = new Vector3f(tipX, 0.3f, tipZ);
            
            addQuad(vc, cx, cy, cz, p0, p1, p2, p3, packed);
        }
    }

    // === Utility functions ===

    private static Vector3f spherePoint(float radius, float latitude, float longitude) {
        float sinLat = (float)Math.sin(latitude);
        float cosLat = (float)Math.cos(latitude);
        float sinLon = (float)Math.sin(longitude);
        float cosLon = (float)Math.cos(longitude);
        
        float x = radius * sinLat * cosLon;
        float y = radius * cosLat;
        float z = radius * sinLat * sinLon;
        return new Vector3f(x, y, z);
    }

    private static void addQuad(VertexConsumer vc, float cx, float cy, float cz, 
                               Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, int color) {
        // First triangle
        vc.vertex(cx + p0.x, cy + p0.y, cz + p0.z).color(color).next();
        vc.vertex(cx + p1.x, cy + p1.y, cz + p1.z).color(color).next();
        vc.vertex(cx + p2.x, cy + p2.y, cz + p2.z).color(color).next();
        
        // Second triangle
        vc.vertex(cx + p0.x, cy + p0.y, cz + p0.z).color(color).next();
        vc.vertex(cx + p2.x, cy + p2.y, cz + p2.z).color(color).next();
        vc.vertex(cx + p3.x, cy + p3.y, cz + p3.z).color(color).next();
    }

    private static int packColor(float r, float g, float b, float a) {
        int ir = Math.round(Math.clamp(r, 0, 1) * 255);
        int ig = Math.round(Math.clamp(g, 0, 1) * 255);
        int ib = Math.round(Math.clamp(b, 0, 1) * 255);
        int ia = Math.round(Math.clamp(a, 0, 1) * 255);
        return (ia << 24) | (ir << 16) | (ig << 8) | ib;
    }

    public static void clear() {
        activeEffects.clear();
    }

    public static boolean isDisabledAfterError() {
        return disabled;
    }

    // === Inner class for tracking active effects ===

    private static class ActiveEffect {
        final Vec3d position;
        final long durationMs;
        final long startTime;

        ActiveEffect(Vec3d pos, long duration, long start) {
            this.position = pos;
            this.durationMs = duration;
            this.startTime = start;
        }
    }
}
