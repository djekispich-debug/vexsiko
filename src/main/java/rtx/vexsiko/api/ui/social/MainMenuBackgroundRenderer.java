package rtx.vexsiko.api.ui.social;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.fonts.Fonts;
import rtx.vexsiko.utils.render.render2d.Render2D;

public final class MainMenuBackgroundRenderer {
    private static final int ORBIT_COUNT = 3;
    private static final int TRAILS_PER_ORBIT = 20;
    private static final float BASE_RADIUS = 120f;
    private static final float RADIUS_SPACING = 45f;
    private static final float[] SPEEDS = {0.35f, 0.55f, 0.25f};
    private static final float[] RADII = {BASE_RADIUS, BASE_RADIUS + RADIUS_SPACING, BASE_RADIUS + 2 * RADIUS_SPACING};

    // HSV hue values from user's code: 100°, 90°, 180°, 270°
    private static final float[] HUES = {100f, 90f, 180f, 270f};

    private long startTime = System.currentTimeMillis();
    private float pulsePhase = 0f;

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        float centerX = width / 2f;
        float centerY = height / 2f;

        // 1) Solid black background
        Render2D.beginFrame(context);
        Render2D.rect(-10, -10, width + 20, height + 20, 0, 0xFF000000);
        Render2D.flush();

        // 2) Update pulse
        long time = System.currentTimeMillis();
        float timeSec = (time - startTime) / 1000f;
        pulsePhase += delta * 2f;

        // 3) Render orbiting trails (3 orbits, 20 trails each)
        Render2D.beginFrame(context);

        for (int orbit = 0; orbit < 3; orbit++) {
            float radius = RADII[orbit];
            float speed = SPEEDS[orbit] * 2 * MathHelper.PI; // rad/sec
            float angleOffset = orbit * 2.094f; // 120° between orbits

            float baseAngle = timeSec * speed + angleOffset;

            // Orbital wobble
            float wobble = MathHelper.sin(timeSec * 0.7f + orbit) * 8f;
            float currRadius = radius + wobble;

            for (int trail = 0; trail < 20; trail++) {
                float trailProgress = trail / 20f;
                float trailAngle = baseAngle - trailProgress * 0.6f;
                
                float x = centerX + MathHelper.cos(trailAngle) * currRadius;
                float y = centerY + MathHelper.sin(trailAngle) * currRadius;

                // Trail alpha fading (like user's code: 4.0 - trail * 0.15)
                float alphaBase = 1f - trailProgress;
                float alpha = alphaBase * 0.6f * (0.85f + 0.15f * MathHelper.sin(pulsePhase * 1.5f + trail * 0.3f));

                // HSV color shifting (4 colors per trail vertex like user's code)
                float hueShift = (timeSec * 30f + trail * 12f) % 360f;
                int color1 = ColorUtil.hsvToRgb((100f + hueShift) % 360f, 0.85f, 1f, alpha * 0.9f);
                int color2 = ColorUtil.hsvToRgb((90f + hueShift) % 360f, 0.8f, 0.95f, alpha * 0.8f);
                int color3 = ColorUtil.hsvToRgb((180f + hueShift) % 360f, 0.75f, 0.9f, alpha * 0.7f);
                int color4 = ColorUtil.hsvToRgb((270f + hueShift) % 360f, 0.9f, 1f, alpha * 0.6f);

                // Trail size decreases with distance
                float size = 18f * (0.4f + 0.6f * alphaBase);

                // Draw trail particle as 4-corner gradient rect
                Render2D.rect(
                    x - size, y - size,
                    size * 2f, size * 2f,
                    8f,
                    color1, color2, color3, color4
                );
            }
        }

        Render2D.flush();

        // 2) Central glow ring behind "V"
        Render2D.beginFrame(context);
        float ringPulse = 1f + 0.15f * MathHelper.sin(pulsePhase * 1.2f);
        float ringSize = 90f * ringPulse;
        float ringAlpha = 0.25f + 0.1f * MathHelper.sin(pulsePhase * 0.8f);
        int ringColor1 = ColorUtil.hsvToRgb((timeSec * 40f) % 360f, 0.9f, 1f, ringAlpha);
        int ringColor2 = ColorUtil.hsvToRgb((timeSec * 40f + 90f) % 360f, 0.8f, 0.95f, ringAlpha * 0.8f);
        int ringColor3 = ColorUtil.hsvToRgb((timeSec * 40f + 180f) % 360f, 0.8f, 0.9f, ringAlpha * 0.6f);
        int ringColor4 = ColorUtil.hsvToRgb((timeSec * 40f + 270f) % 360f, 0.95f, 1f, ringAlpha * 0.5f);
        Render2D.rect(centerX - ringSize, centerY - ringSize, ringSize * 2f, ringSize * 2f, ringSize * 0.5f,
            ringColor1, ringColor2, ringColor3, ringColor4);
        Render2D.flush();

        // 3) Center "V" letter
        Render2D.beginFrame(context);
        String v = "V";
        float vSize = 80f;
        float vWidth = Fonts.MONTSERRAT_BLACK.width(v, vSize);
        float vX = centerX - vWidth / 2f;
        float vY = centerY - vSize / 2f + 4f;
        
        // V letter glow behind
        float glowSize = vSize * 1.4f;
        Render2D.rect(vX - (glowSize - vWidth) / 2f - 10f, vY - 10f, glowSize + 20f, vSize + 20f, 15f,
            0x20FFFFFF, 0x20FFFFFF, 0x20FFFFFF, 0x20FFFFFF);
        
        // V letter - white with subtle accent tint
        Fonts.MONTSERRAT_BLACK.draw(v, vX, vY, vSize, 0xFFFFFFFF);
        Render2D.flush();
    }
}