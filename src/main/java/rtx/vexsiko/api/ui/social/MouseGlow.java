package rtx.vexsiko.api.ui.social;

import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.render.render2d.Render2D;

/**
 * Fluid cursor glow: 4 cascading layers + velocity pulse.
 * Creates liquid trail that feels connected to the particle field.
 */
public final class MouseGlow {
    private final float[] xs = new float[4];
    private final float[] ys = new float[4];
    private boolean init;
    private float pulse;
    private float vx, vy;

    public void render(float mouseX, float mouseY, float delta) {
        if (!this.init) {
            for (int i = 0; i < 4; ++i) {
                this.xs[i] = mouseX;
                this.ys[i] = mouseY;
            }
            this.init = true;
        }
        float dx = mouseX - this.xs[0];
        float dy = mouseY - this.ys[0];
        float speed = (float)Math.sqrt(dx * dx + dy * dy);
        // smooth velocity
        this.vx += (dx - this.vx) * Math.min(1f, delta * 8f);
        this.vy += (dy - this.vy) * Math.min(1f, delta * 8f);
        this.pulse += (speed - this.pulse) * Math.min(1.0f, delta * 6.5f);
        float boost = Math.min(1.0f, this.pulse / 38.0f);

        float dt = Math.min(delta, 3.0f);
        float k0 = 1.0f - (float)Math.exp(-dt * 24.0);
        float k1 = 1.0f - (float)Math.exp(-dt * 14.0);
        float k2 = 1.0f - (float)Math.exp(-dt * 7.2);
        float k3 = 1.0f - (float)Math.exp(-dt * 3.9);

        this.xs[0] += (mouseX - this.xs[0]) * k0;
        this.ys[0] += (mouseY - this.ys[0]) * k0;
        this.xs[1] += (this.xs[0] - this.xs[1]) * k1;
        this.ys[1] += (this.ys[0] - this.ys[1]) * k1;
        this.xs[2] += (this.xs[1] - this.xs[2]) * k2;
        this.ys[2] += (this.ys[1] - this.ys[2]) * k2;
        this.xs[3] += (this.xs[2] - this.xs[3]) * k3;
        this.ys[3] += (this.ys[2] - this.ys[3]) * k3;

        // time wiggle
        float t = System.currentTimeMillis() * 0.0018f;
        float wob = (float)Math.sin(t * 2.3) * 2f;

        // outermost very soft halo
        Render2D.circle(this.xs[3], this.ys[3], 135f + boost * 34f + wob, 185f, ClientAccent.accentSoft(7f + boost * 6f));
        Render2D.circle(this.xs[2], this.ys[2], 92f + boost * 22f, 130f, ClientAccent.accentSoft(11f + boost * 7f));
        Render2D.circle(this.xs[1], this.ys[1], 52f + wob * 0.5f, 88f, ClientAccent.accentSoft(19f + boost * 11f));
        Render2D.circle(this.xs[0], this.ys[0], 16f + boost * 7f, 42f, ClientAccent.accentSoft(42f + boost * 18f));
        Render2D.circle(this.xs[0], this.ys[0], 6f, 14f, ClientAccent.accentBright(85f + boost * 40f));
    }

    public float trailX(int i) { return xs[Math.max(0, Math.min(i, 3))]; }
    public float trailY(int i) { return ys[Math.max(0, Math.min(i, 3))]; }
}
