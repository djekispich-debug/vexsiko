package rtx.vexsiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.utils.render.render2d.Render2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(TitleScreen.class)
public class TitleScreenGlowParticlesMixin {

    @Unique
    private final List<GlowParticle> vexsiko$particles = new ArrayList<>();
    @Unique
    private final Random vexsiko$random = new Random();
    @Unique
    private boolean vexsiko$initialized = false;

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void vexsiko_drawInteractiveGlow(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        if (w <= 0 || h <= 0) return;

        if (!vexsiko$initialized) {
            for (int i = 0; i < 52; i++) {
                vexsiko$particles.add(new GlowParticle(
                    vexsiko$random.nextFloat() * w,
                    vexsiko$random.nextFloat() * h,
                    vexsiko$random
                ));
            }
            vexsiko$initialized = true;
        }

        // solid black like original, but will be covered by glow
        ctx.fill(0, 0, w, h, 0xFF000000);

        Render2D.beginFrame(ctx);
        for (GlowParticle p : vexsiko$particles) {
            p.update(mouseX, mouseY, w, h);
            p.render();
        }
        Render2D.flush();
        ci.cancel();
    }

    @Unique
    private static class GlowParticle {
        float x, y;
        float vx, vy;
        float baseRadius;
        float currentRadius;
        float alphaPhase;
        float alphaSpeed;
        int r, g, b;

        GlowParticle(float x, float y, Random rand) {
            this.x = x;
            this.y = y;
            this.vx = (rand.nextFloat() - 0.5f) * 0.42f;
            this.vy = (rand.nextFloat() - 0.5f) * 0.42f;
            this.baseRadius = 15f + rand.nextFloat() * 20f;
            this.currentRadius = baseRadius;
            this.alphaPhase = rand.nextFloat() * 100f;
            this.alphaSpeed = 0.015f + rand.nextFloat() * 0.02f;
            this.r = 150 + rand.nextInt(60);
            this.g = 210 + rand.nextInt(45);
            this.b = 255;
        }

        void update(float mouseX, float mouseY, int sw, int sh) {
            x += vx;
            y += vy;
            alphaPhase += alphaSpeed;
            float dx = mouseX - x;
            float dy = mouseY - y;
            float dist = (float)Math.sqrt(dx * dx + dy * dy);
            if (dist < 130f && dist > 1f) {
                float force = (130f - dist) / 130f;
                vx += (dx / dist) * force * 0.09f;
                vy += (dy / dist) * force * 0.09f;
                currentRadius = baseRadius + force * 12f;
            } else {
                currentRadius = baseRadius;
                vx *= 0.985f;
                vy *= 0.985f;
            }
            if (x < 0 || x > sw) vx *= -1;
            if (y < 0 || y > sh) vy *= -1;
            // clamp velocity
            float maxV = 1.8f;
            if (vx > maxV) vx = maxV;
            if (vx < -maxV) vx = -maxV;
            if (vy > maxV) vy = maxV;
            if (vy < -maxV) vy = -maxV;
        }

        void render() {
            float alphaMult = (float)(Math.sin(alphaPhase) + 1.0) / 2.0f;
            // slightly brighter than original to match our theme
            int centerAlpha = (int)(72 * alphaMult);
            if (centerAlpha <= 1) return;
            int col = (centerAlpha << 24) | (r << 16) | (g << 8) | b;
            // soft outer fade via Render2D circle (inner solid, outer transparent)
            Render2D.circle(x, y, currentRadius * 0.55f, currentRadius * 1.9f, col);
            // inner core a bit brighter
            if (centerAlpha > 20) {
                int coreCol = ((Math.min(255, centerAlpha + 28) << 24) | (r << 16) | (g << 8) | b);
                Render2D.circle(x, y, currentRadius * 0.22f, currentRadius * 0.45f, coreCol);
            }
        }
    }
}
