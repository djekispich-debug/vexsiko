package rtx.vexsiko.api.ui.social;

import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.render.render2d.Render2D;

/**
 * Ambient dust for the title screen: sparse particles that slowly
 * rise across the whole screen, shimmer with alpha breathing,
 * wobble on sine waves and get gently repelled by the cursor.
 */
public final class AmbientDust {
    private static final int COUNT = 56;
    private static final float REPEL_RADIUS = 110.0f;
    private static final float REPEL_STRENGTH = 30.0f;

    private final float[] nx = new float[COUNT];
    private final float[] ny = new float[COUNT];
    private final float[] rise = new float[COUNT];
    private final float[] phase = new float[COUNT];
    private final float[] swaySpeed = new float[COUNT];
    private final float[] swayAmp = new float[COUNT];
    private final float[] size = new float[COUNT];
    private final boolean[] bright = new boolean[COUNT];
    private final float[] ox = new float[COUNT];
    private final float[] oy = new float[COUNT];

    public AmbientDust() {
        java.util.Random rng = new java.util.Random(0x44555354L);
        for (int i = 0; i < COUNT; ++i) {
            this.nx[i] = rng.nextFloat();
            this.ny[i] = rng.nextFloat();
            this.rise[i] = 0.012f + rng.nextFloat() * 0.03f;
            this.phase[i] = rng.nextFloat() * (float)(Math.PI * 2.0);
            this.swaySpeed[i] = 0.3f + rng.nextFloat() * 0.7f;
            this.swayAmp[i] = 2.5f + rng.nextFloat() * 8.0f;
            this.size[i] = 0.7f + rng.nextFloat() * 1.3f;
            this.bright[i] = rng.nextFloat() < 0.18f;
        }
    }

    public void render(float width, float height, float mouseX, float mouseY, float delta) {
        long ms = System.currentTimeMillis();
        float t = ms * 0.001f;
        float dt = Math.min(delta, 3.0f);
        float ease = 1.0f - (float)Math.exp(-dt * 7.0);

        for (int i = 0; i < COUNT; ++i) {
            this.ny[i] -= this.rise[i] * dt;
            if (this.ny[i] < -0.04f) {
                this.ny[i] += 1.08f;
                this.ox[i] *= 0.2f;
                this.oy[i] *= 0.2f;
            }
            float px = this.nx[i] * width + (float)Math.sin(t * this.swaySpeed[i] + this.phase[i]) * this.swayAmp[i];
            float py = this.ny[i] * height;

            float rdx = px - mouseX;
            float rdy = py - mouseY;
            float dist = (float)Math.sqrt(rdx * rdx + rdy * rdy);
            float tox = 0.0f;
            float toy = 0.0f;
            if (dist < REPEL_RADIUS && dist > 0.01f) {
                float fall = 1.0f - dist / REPEL_RADIUS;
                float push = fall * fall * REPEL_STRENGTH;
                tox = rdx / dist * push;
                toy = rdy / dist * push;
            }
            this.ox[i] += (tox - this.ox[i]) * ease;
            this.oy[i] += (toy - this.oy[i]) * ease;

            float shimmer = 0.5f + 0.5f * (float)Math.sin(t * (0.6f + this.swaySpeed[i]) + this.phase[i]);
            float a = 16.0f + 58.0f * shimmer;
            int color = this.bright[i]
                ? ClientAccent.accentBright(a)
                : ClientAccent.accentSoft(a);
            Render2D.circle(px + this.ox[i], py + this.oy[i], this.size[i], this.size[i] * 3.0f, color);
        }
    }
}
