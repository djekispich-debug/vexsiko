package rtx.vexsiko.api.ui.social;

import net.minecraft.util.math.MathHelper;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;

/**
 * Antigravity-style particle field for the title screen.
 * - Outer rounded-rect border (like Google Antigravity) made of drifting dots
 * - Inner big "V" letter in the center, also from particles
 * - Ambient dust over whole screen
 * Everything moves: wobble, breathing, parallax, mouse repel + trails.
 */
public final class VLogoParticles {
    private static final int OUTER_COUNT = 820;
    private static final int V_COUNT = 520;
    private static final int AMBIENT_COUNT = 46;
    private static final float REPEL_RADIUS = 132.0f;
    private static final float REPEL_STRENGTH = 36.0f;

    private final float[] outerT = new float[OUTER_COUNT];
    private final float[] outerJitter = new float[OUTER_COUNT];
    private final float[] outerSize = new float[OUTER_COUNT];
    private final float[] outerPhase = new float[OUTER_COUNT];
    private final float[] outerSpeed = new float[OUTER_COUNT];
    private final boolean[] outerAccent = new boolean[OUTER_COUNT];

    private final float[] vArm = new float[V_COUNT];
    private final float[] vT = new float[V_COUNT];
    private final float[] vSide = new float[V_COUNT];
    private final float[] vJitter = new float[V_COUNT];
    private final float[] vSize = new float[V_COUNT];
    private final float[] vPhase = new float[V_COUNT];
    private final boolean[] vAccent = new boolean[V_COUNT];

    private final float[] ambX = new float[AMBIENT_COUNT];
    private final float[] ambY = new float[AMBIENT_COUNT];
    private final float[] ambVX = new float[AMBIENT_COUNT];
    private final float[] ambVY = new float[AMBIENT_COUNT];
    private final float[] ambSize = new float[AMBIENT_COUNT];
    private final float[] ambPhase = new float[AMBIENT_COUNT];

    // smoothed offsets for mouse push
    private final float[] oOx = new float[OUTER_COUNT];
    private final float[] oOy = new float[OUTER_COUNT];
    private final float[] vOx = new float[V_COUNT];
    private final float[] vOy = new float[V_COUNT];

    private boolean init;

    private void ensureInit() {
        if (this.init) return;
        java.util.Random rnd = new java.util.Random(0x5A11A5AL);
        for (int i = 0; i < OUTER_COUNT; i++) {
            outerT[i] = rnd.nextFloat();
            outerJitter[i] = (rnd.nextFloat() - 0.5f) * 10.0f;
            outerSize[i] = 1.05f + rnd.nextFloat() * 1.9f;
            outerPhase[i] = rnd.nextFloat() * 6.2831855f;
            outerSpeed[i] = (0.02f + rnd.nextFloat() * 0.06f) * (rnd.nextBoolean() ? 1f : -1f);
            outerAccent[i] = rnd.nextFloat() < 0.78f;
        }
        for (int i = 0; i < V_COUNT; i++) {
            vArm[i] = rnd.nextBoolean() ? 0f : 1f;
            vT[i] = rnd.nextFloat();
            // distribute more densely near center line for crisp V
            float d = rnd.nextFloat();
            vSide[i] = (d < 0.65f)
                ? (rnd.nextFloat() - 0.5f) * 0.9f
                : (rnd.nextFloat() - 0.5f) * 2.2f;
            vJitter[i] = (rnd.nextFloat() - 0.5f) * 6.0f;
            vSize[i] = 1.15f + rnd.nextFloat() * 2.0f;
            vPhase[i] = rnd.nextFloat() * 6.2831855f;
            vAccent[i] = rnd.nextFloat() < 0.82f;
        }
        for (int i = 0; i < AMBIENT_COUNT; i++) {
            ambX[i] = rnd.nextFloat();
            ambY[i] = rnd.nextFloat();
            ambVX[i] = (rnd.nextFloat() - 0.5f) * 0.0065f;
            ambVY[i] = (rnd.nextFloat() - 0.5f) * 0.0045f;
            ambSize[i] = 0.7f + rnd.nextFloat() * 1.35f;
            ambPhase[i] = rnd.nextFloat() * 6.2831855f;
        }
        this.init = true;
    }

    public static float ringRadius(float w, float h) {
        return Math.min(w, h) * 0.23f;
    }

    // compute outer border point via rounded rect perimeter param t in [0,1)
    private static void outerPoint(float t, float cx, float cy, float wOuter, float hOuter, float r, float[] out) {
        float wt = wOuter - 2f * r;
        float ht = hOuter - 2f * r;
        float perim = 2f * (wt + ht) + 6.2831855f * r;
        float d = t * perim;
        // top edge
        if (d < wt) {
            out[0] = cx - wOuter * 0.5f + r + d;
            out[1] = cy - hOuter * 0.5f;
            out[2] = 0; // tangent angle 0
            return;
        }
        d -= wt;
        // top-right arc  -90 to 0
        float arcLen = 1.5707963f * r;
        if (d < arcLen) {
            float a = -1.5707963f + (d / r);
            out[0] = cx + wOuter * 0.5f - r + MathHelper.cos(a) * r;
            out[1] = cy - hOuter * 0.5f + r + MathHelper.sin(a) * r;
            out[2] = a + 1.5707963f;
            return;
        }
        d -= arcLen;
        if (d < ht) {
            out[0] = cx + wOuter * 0.5f;
            out[1] = cy - hOuter * 0.5f + r + d;
            out[2] = 1.5707963f;
            return;
        }
        d -= ht;
        if (d < arcLen) {
            float a = 0f + (d / r);
            out[0] = cx + wOuter * 0.5f - r + MathHelper.cos(a) * r;
            out[1] = cy + hOuter * 0.5f - r + MathHelper.sin(a) * r;
            out[2] = a + 1.5707963f;
            return;
        }
        d -= arcLen;
        if (d < wt) {
            out[0] = cx + wOuter * 0.5f - r - d;
            out[1] = cy + hOuter * 0.5f;
            out[2] = 3.1415927f;
            return;
        }
        d -= wt;
        if (d < arcLen) {
            float a = 1.5707963f + (d / r);
            out[0] = cx - wOuter * 0.5f + r + MathHelper.cos(a) * r;
            out[1] = cy + hOuter * 0.5f - r + MathHelper.sin(a) * r;
            out[2] = a + 1.5707963f;
            return;
        }
        d -= arcLen;
        if (d < ht) {
            out[0] = cx - wOuter * 0.5f;
            out[1] = cy + hOuter * 0.5f - r - d;
            out[2] = -1.5707963f;
            return;
        }
        d -= ht;
        // last arc
        float a = 3.1415927f + (d / r);
        out[0] = cx - wOuter * 0.5f + r + MathHelper.cos(a) * r;
        out[1] = cy - hOuter * 0.5f + r + MathHelper.sin(a) * r;
        out[2] = a + 1.5707963f;
    }

    public void render(float cx, float cy, float w, float h, float delta, float timeSec, float mouseX, float mouseY) {
        ensureInit();
        float dt = Math.min(delta, 3.0f);
        float ease = 1.0f - (float)Math.exp(-dt * 9.0);
        float wOuter = w * 0.62f;
        float hOuter = h * 0.78f;
        wOuter = Math.min(wOuter, hOuter * 1.18f);
        float r = Math.min(wOuter, hOuter) * 0.095f;

        // global parallax drift opposite to mouse
        float parX = (mouseX - cx) * 0.012f;
        float parY = (mouseY - cy) * 0.012f;

        // V shape geometry - big centered V behind the title
        float vHalfW = Math.min(w, h) * 0.30f;
        float vHalfH = Math.min(w, h) * 0.28f;
        float vTop = cy - vHalfH * 0.42f;
        float vBottom = cy + vHalfH * 0.86f;
        float vLeftX = cx - vHalfW;
        float vRightX = cx + vHalfW;
        float vThickness = vHalfW * 0.18f;

        // --- ambient ---
        for (int i = 0; i < AMBIENT_COUNT; i++) {
            ambX[i] += ambVX[i] * dt;
            ambY[i] += ambVY[i] * dt;
            if (ambX[i] < -0.02f) ambX[i] = 1.02f;
            if (ambX[i] > 1.02f) ambX[i] = -0.02f;
            if (ambY[i] < -0.02f) ambY[i] = 1.02f;
            if (ambY[i] > 1.02f) ambY[i] = -0.02f;
            float x = ambX[i] * w + MathHelper.sin(timeSec * 0.72f + ambPhase[i]) * 7.0f - parX * 0.35f;
            float y = ambY[i] * h + MathHelper.cos(timeSec * 0.55f + ambPhase[i] * 1.2f) * 5.5f - parY * 0.35f;
            // mouse push for ambient too
            float dxm = x - mouseX;
            float dym = y - mouseY;
            float dist = (float)Math.sqrt(dxm * dxm + dym * dym);
            if (dist < 90f && dist > 0.5f) {
                float f = (1f - dist / 90f);
                f *= f * 12f;
                x += dxm / dist * f;
                y += dym / dist * f;
            }
            float tw = 0.34f + 0.46f * (0.5f + 0.5f * MathHelper.sin(timeSec * 1.5f + ambPhase[i] * 2.1f));
            Render2D.circle(x, y, ambSize[i], ambSize[i] * 1.7f, ColorUtil.withAlpha(0xFFB9C6FF, (int)(tw * 42f)));
        }

        float[] tmp = new float[3];
        // --- outer border particles ---
        for (int i = 0; i < OUTER_COUNT; i++) {
            float tOff = outerT[i] + outerSpeed[i] * 0.012f * dt;
            outerT[i] = tOff - (float)Math.floor(tOff);
            outerPoint(outerT[i], cx, cy, wOuter, hOuter, r, tmp);
            float bx = tmp[0];
            float by = tmp[1];
            float ang = tmp[2];
            float nx = -MathHelper.sin(ang);
            float ny = MathHelper.cos(ang);
            // breathing + wobble
            float breathe = MathHelper.sin(timeSec * 0.48f + outerPhase[i]) * 5.2f;
            float wobX = MathHelper.sin(timeSec * 0.85f + outerPhase[i] * 1.13f) * 2.2f;
            float wobY = MathHelper.cos(timeSec * 0.72f + outerPhase[i] * 0.87f) * 2.0f;
            float px = bx + nx * (outerJitter[i] + breathe) + wobX - parX * 0.9f;
            float py = by + ny * (outerJitter[i] * 0.3f) + wobY - parY * 0.9f;

            // mouse repulsion
            float dxm = px - mouseX;
            float dym = py - mouseY;
            float dist = (float)Math.sqrt(dxm * dxm + dym * dym);
            float tox = 0f, toy = 0f;
            if (dist < REPEL_RADIUS && dist > 0.1f) {
                float f = 1f - dist / REPEL_RADIUS;
                f = f * f * f; // sharp fall
                float push = f * REPEL_STRENGTH;
                // push outward along normal a bit + away from mouse
                tox = dxm / dist * push + nx * push * 0.45f;
                toy = dym / dist * push + ny * push * 0.45f;
            }
            oOx[i] += (tox - oOx[i]) * ease;
            oOy[i] += (toy - oOy[i]) * ease;
            px += oOx[i];
            py += oOy[i];

            float tw = 0.58f + 0.42f * MathHelper.sin(timeSec * 2.05f + outerPhase[i] * 1.7f);
            // size pulse near mouse
            float near = dist < REPEL_RADIUS ? (1f - dist / REPEL_RADIUS) : 0f;
            float sz = outerSize[i] * (1f + near * 0.55f);
            float a = tw * (0.72f + near * 0.55f);
            int col;
            if (outerAccent[i]) {
                col = ClientAccent.accentSoft(a * 185f);
            } else {
                col = ColorUtil.withAlpha(0xFFEAF0FF, (int)(a * 135f));
            }
            Render2D.circle(px, py, sz, sz * 2.35f, col);
            // tiny halo for brighter dots
            if (outerAccent[i] && near > 0.35f) {
                Render2D.circle(px, py, sz * 2.8f, sz * 4.2f, ClientAccent.accentSoft(a * 22f * near));
            }
        }

        // --- V particles ---
        for (int i = 0; i < V_COUNT; i++) {
            // slight flow along V arms
            vT[i] += 0.00055f * dt * (vArm[i] == 0f ? 1f : -1f);
            if (vT[i] < 0f) vT[i] += 1f;
            if (vT[i] > 1f) vT[i] -= 1f;

            float t = vT[i];
            float ax, ay, bx, by;
            if (vArm[i] == 0f) {
                ax = vLeftX; ay = vTop;
                bx = cx; by = vBottom;
            } else {
                ax = vRightX; ay = vTop;
                bx = cx; by = vBottom;
            }
            float baseX = MathHelper.lerp(t, ax, bx);
            float baseY = MathHelper.lerp(t, ay, by);
            // perpendicular direction for thickness
            float dx = bx - ax;
            float dy = by - ay;
            float len = (float)Math.sqrt(dx * dx + dy * dy);
            float pnx = -dy / len;
            float pny = dx / len;

            float side = vSide[i] * (vThickness * 0.5f);
            float j = vJitter[i] + MathHelper.sin(timeSec * 0.92f + vPhase[i]) * 2.8f;
            float px = baseX + pnx * (side + j * 0.22f) + MathHelper.sin(timeSec * 0.68f + vPhase[i] * 1.22f) * 1.7f - parX * -0.45f;
            float py = baseY + pny * (side * 0.18f) + MathHelper.cos(timeSec * 0.62f + vPhase[i] * 0.95f) * 1.55f - parY * -0.45f;

            // mouse repulsion for V too - stronger
            float dxm = px - mouseX;
            float dym = py - mouseY;
            float dist = (float)Math.sqrt(dxm * dxm + dym * dym);
            float tox = 0f, toy = 0f;
            if (dist < REPEL_RADIUS * 1.05f && dist > 0.1f) {
                float f = 1f - dist / (REPEL_RADIUS * 1.05f);
                f = f * f * 1.2f;
                float push = f * (REPEL_STRENGTH * 0.88f);
                tox = dxm / dist * push;
                toy = dym / dist * push;
            }
            vOx[i] += (tox - vOx[i]) * ease;
            vOy[i] += (toy - vOy[i]) * ease;
            px += vOx[i];
            py += vOy[i];

            float tw = 0.62f + 0.38f * MathHelper.sin(timeSec * 2.35f + vPhase[i] * 1.42f);
            // density fade: central spine more opaque
            float spine = 1f - Math.min(1f, Math.abs(vSide[i]) * 0.85f);
            float a = tw * (0.62f + spine * 0.45f);
            float near = dist < 110f ? (1f - dist / 110f) : 0f;
            a *= (1f + near * 0.45f);
            float sz = vSize[i] * (0.9f + spine * 0.35f) * (1f + near * 0.4f);

            int col = vAccent[i]
                ? ClientAccent.accentSoft(Math.min(255f, a * 205f))
                : ColorUtil.withAlpha(0xFFEFF4FF, (int)(a * 145f));

            Render2D.circle(px, py, sz, sz * 2.45f, col);
            if (spine > 0.55f) {
                Render2D.circle(px, py, sz * 0.45f, sz * 0.9f, ColorUtil.withAlpha(0xFFFFFFFF, (int)(a * 42f * spine)));
            }
        }
    }
}
