package rtx.vexsiko.utils.render.post.shaderkill;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * ShaderKill pipeline.
 *
 * The original tornado/portal renderers are kept intact for compatibility.
 * The new 3D tornado uses generated geometry in the vertex shader, so it is
 * a real world-space mesh instead of a camera-facing quad.
 */
public final class ShaderKillPipeline {
    private static final Identifier TORNADO_PIPELINE_ID =
            Identifier.of("vexsiko", "pipeline/shaderkill_tornado");
    private static final Identifier TORNADO_SHADER_ID =
            Identifier.of("vexsiko", "core/shaderkill/tornado");

    private static final Identifier TORNADO_3D_PIPELINE_ID =
            Identifier.of("vexsiko", "pipeline/shaderkill_tornado_3d");
    private static final Identifier TORNADO_3D_SHADER_ID =
            Identifier.of("vexsiko", "core/shaderkill/tornado3d");

    private static final Identifier PORTAL_PIPELINE_ID =
            Identifier.of("vexsiko", "pipeline/shaderkill_portal");
    private static final Identifier PORTAL_SHADER_ID =
            Identifier.of("vexsiko", "core/shaderkill/portal");

    // 24 segments, 10 height rings:
    // 24 * 9 quads * 6 vertices = 1296
    // core: 12 * 5 * 6 = 360
    // torus: 24 * 4 * 6 = 576
    // orb: 12 * 5 * 6 = 360
    // total = 2592 generated vertices.
    private static final int TORNADO_3D_VERTICES = 2592;

    private static final RenderPipeline TORNADO_PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withLocation(TORNADO_PIPELINE_ID)
            .withVertexShader(TORNADO_SHADER_ID)
            .withFragmentShader(TORNADO_SHADER_ID)
            .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
            .withUniform("ShaderKillData", UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build()
    );

    private static final RenderPipeline TORNADO_3D_PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withLocation(TORNADO_3D_PIPELINE_ID)
            .withVertexShader(TORNADO_3D_SHADER_ID)
            .withFragmentShader(TORNADO_3D_SHADER_ID)
            .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
            .withUniform("ShaderKillData", UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build()
    );

    private static final RenderPipeline PORTAL_PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withLocation(PORTAL_PIPELINE_ID)
            .withVertexShader(PORTAL_SHADER_ID)
            .withFragmentShader(PORTAL_SHADER_ID)
            .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
            .withUniform("ShaderKillData", UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build()
    );

    private static final ShaderKillPipeline INSTANCE = new ShaderKillPipeline();

    public static ShaderKillPipeline getInstance() {
        return INSTANCE;
    }

    private GpuBuffer uniformBuffer;
    private GpuBuffer dummyVertexBuffer;
    private ByteBuffer dataBuffer;
    private boolean initialized;
    private boolean errorLogged;

    private void ensureInitialized() {
        if (initialized) return;

        try {
            dataBuffer = MemoryUtil.memAlloc(96);

            ByteBuffer dummy = MemoryUtil.memAlloc(4);
            dummy.putInt(0);
            dummy.flip();

            dummyVertexBuffer = RenderSystem.getDevice().createBuffer(
                () -> "vexsiko:shaderkill_dummy",
                GpuBuffer.USAGE_VERTEX,
                dummy
            );

            MemoryUtil.memFree(dummy);
            initialized = true;
        } catch (Exception e) {
            if (!errorLogged) {
                System.err.println("[ShaderKillPipeline] init failed: " + e.getMessage());
                errorLogged = true;
            }
        }
    }

    public void drawTornado(float x, float y, float w, float h,
                            float time, float progress, int color, float intensity) {
        draw(TORNADO_PIPELINE, x, y, w, h, time, progress, color,
                intensity, 1.0f, null, 0f, 0f, 6);
    }

    public void drawPortal(float x, float y, float w, float h,
                           float time, float progress, int color, float intensity) {
        draw(PORTAL_PIPELINE, x, y, w, h, time, progress, color,
                intensity, 1.0f, null, 0f, 0f, 6);
    }

    public void drawTornado(float x, float y, float w, float h,
                            float time, float progress, int color,
                            float intensity, float uSize) {
        draw(TORNADO_PIPELINE, x, y, w, h, time, progress, color,
                intensity, uSize, null, 0, 0, 6);
    }

    public void drawPortal(float x, float y, float w, float h,
                           float time, float progress, int color,
                           float intensity, float uSize) {
        draw(PORTAL_PIPELINE, x, y, w, h, time, progress, color,
                intensity, uSize, null, 0, 0, 6);
    }

    /**
     * Legacy world-space tornado billboard (camera-facing quad).
     * Kept for the old "Торнадо" mode. Sizes are pixels, the vertex shader
     * converts them to world units (widthPx / 90).
     */
    public void drawTornadoBillboardWorld(Vec3d pos, float worldW, float worldH,
                                          float time, float progress, int color,
                                          float intensity, float uSize) {
        draw(
            TORNADO_PIPELINE,
            0f, 0f,
            worldW, worldH,
            time, progress, color, intensity, uSize,
            pos, worldW, worldH,
            6
        );
    }

    /**
     * Legacy alias — pre-3D behavior (billboard, pixel sizes).
     */
    public void drawTornadoWorld(Vec3d pos, float worldW, float worldH,
                                 float time, float progress, int color,
                                 float intensity, float uSize) {
        drawTornadoBillboardWorld(pos, worldW, worldH, time, progress,
                color, intensity, uSize);
    }

    /**
     * New real 3D energy pillar.
     *
     * Sizes are already Minecraft blocks here (NOT pixels), so the effect
     * has the same physical size regardless of resolution, GUI scale,
     * window size, FOV or camera distance.
     */
    public void drawTornado3DWorld(Vec3d pos, float widthBlocks, float heightBlocks,
                                   float time, float progress, int color,
                                   float intensity, float uSize) {
        float w = Math.max(0.35f, widthBlocks);
        float h = Math.max(1.0f, heightBlocks);

        draw(
            TORNADO_3D_PIPELINE,
            0f, 0f,
            w, h,
            time, progress, color, intensity, uSize,
            pos, w, h,
            TORNADO_3D_VERTICES
        );
    }

    public void drawPortalWorld(Vec3d pos, float worldW, float worldH,
                                float time, float progress, int color,
                                float intensity, float uSize) {
        draw(PORTAL_PIPELINE, 0, 0, worldW, worldH, time, progress,
                color, intensity, uSize, pos, worldW, worldH, 6);
    }

    // Legacy single-size portal method.
    public void drawPortalWorld(Vec3d pos, float worldSize, float time,
                                float progress, int color, float intensity,
                                float uSize) {
        drawPortalWorld(pos, worldSize, worldSize, time, progress,
                color, intensity, uSize);
    }

    private void draw(RenderPipeline pipeline,
                      float x, float y, float w, float h,
                      float time, float progress, int color,
                      float intensity, float uSize,
                      Vec3d worldPos, float worldW, float worldH,
                      int vertexCount) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getFramebuffer() == null || w <= 0 || h <= 0) return;

        ensureInitialized();
        if (!initialized || dataBuffer == null || dummyVertexBuffer == null) return;

        float screenW = mc.getWindow().getScaledWidth();
        float screenH = mc.getWindow().getScaledHeight();
        if (screenW <= 0 || screenH <= 0) return;

        float fbW = mc.getFramebuffer().textureWidth;
        float fbH = mc.getFramebuffer().textureHeight;

        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;
        float a = ((color >>> 24) & 255) / 255f;
        if (a <= 0f) a = 1f;

        dataBuffer.clear();

        // rect
        dataBuffer.putFloat(x);
        dataBuffer.putFloat(y);
        dataBuffer.putFloat(w);
        dataBuffer.putFloat(h);

        // screen
        dataBuffer.putFloat(screenW);
        dataBuffer.putFloat(screenH);
        dataBuffer.putFloat(fbW);
        dataBuffer.putFloat(fbH);

        // beamColor
        dataBuffer.putFloat(r);
        dataBuffer.putFloat(g);
        dataBuffer.putFloat(b);
        dataBuffer.putFloat(a);

        // uTime, uProgress, uIntensity, uSize
        dataBuffer.putFloat(time);
        dataBuffer.putFloat(progress);
        dataBuffer.putFloat(intensity);
        dataBuffer.putFloat(uSize);

        // worldPosSize.xyz + width
        if (worldPos != null) {
            dataBuffer.putFloat((float) worldPos.x);
            dataBuffer.putFloat((float) worldPos.y);
            dataBuffer.putFloat((float) worldPos.z);
            dataBuffer.putFloat(worldW);
        } else {
            dataBuffer.putFloat(0f);
            dataBuffer.putFloat(0f);
            dataBuffer.putFloat(0f);
            dataBuffer.putFloat(1f);
        }

        // worldHeight + std140 padding
        dataBuffer.putFloat(worldH > 0f ? worldH : worldW);
        dataBuffer.putFloat(0f);
        dataBuffer.putFloat(0f);
        dataBuffer.putFloat(0f);
        dataBuffer.flip();

        try {
            int size = dataBuffer.remaining();

            if (uniformBuffer == null || uniformBuffer.size() < size) {
                if (uniformBuffer != null) uniformBuffer.close();

                uniformBuffer = RenderSystem.getDevice().createBuffer(
                    () -> "vexsiko:shaderkill_uniform",
                    GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST,
                    size
                );
            }

            CommandEncoder encoder =
                RenderSystem.getDevice().createCommandEncoder();

            encoder.writeToBuffer(uniformBuffer.slice(), dataBuffer);

            try (RenderPass pass = encoder.createRenderPass(
                    () -> "vexsiko:shaderkill_pass",
                    mc.getFramebuffer().getColorAttachmentView(),
                    OptionalInt.empty(),
                    mc.getFramebuffer().getDepthAttachmentView(),
                    OptionalDouble.empty())) {

                pass.setPipeline(pipeline);
                pass.setVertexBuffer(0, dummyVertexBuffer);
                RenderSystem.bindDefaultUniforms(pass);
                pass.setUniform("ShaderKillData", uniformBuffer);
                pass.draw(0, vertexCount);
            }
        } catch (Exception e) {
            if (!errorLogged) {
                System.err.println("[ShaderKillPipeline] draw failed: " + e.getMessage());
                e.printStackTrace();
                errorLogged = true;
            }
        }
    }

    public void close() {
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }

        if (dummyVertexBuffer != null) {
            dummyVertexBuffer.close();
            dummyVertexBuffer = null;
        }

        if (dataBuffer != null) {
            MemoryUtil.memFree(dataBuffer);
            dataBuffer = null;
        }

        initialized = false;
    }
}
