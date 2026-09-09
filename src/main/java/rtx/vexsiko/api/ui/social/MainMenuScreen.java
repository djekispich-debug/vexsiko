package rtx.vexsiko.api.ui.social;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.api.ui.theme.ThemeManager;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.fonts.Fonts;
import rtx.vexsiko.utils.render.render2d.Render2D;

public final class MainMenuScreen extends Screen {
    private static final String[] LABELS = {"Singleplayer", "Multiplayer", "Alt Manager", "Options"};
    private static final float BTN_W = 216;
    private static final float BTN_H = 36;
    private static final float BTN_GAP = 8;
    private static final float BTN_R = 10;

    private int hovered = -1;
    private long openStart;
    private boolean opened;
    private final float[] hoverAnim = new float[LABELS.length];

    private final MouseGlow glow = new MouseGlow();
    private final AmbientDust dust = new AmbientDust();
    private final VLogoParticles vParticles = new VLogoParticles();

    public MainMenuScreen() {
        super(Text.literal(""));
    }

    @Override
    protected void init() {
        super.init();
        if (!this.opened) {
            this.opened = true;
            this.openStart = System.currentTimeMillis();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        long now = System.currentTimeMillis();
        float t = now * 0.001f;
        float dt = Math.min(delta, 3.0f);
        float openP = clamp01((now - this.openStart) / 900.0f);
        float easeOpen = easeOutCubic(openP);

        float w = this.width;
        float h = this.height;
        float cx = w / 2f;
        float cy = h / 2f;
        int accent = ClientAccent.accent(255);

        // subtle parallax for whole scene
        float parX = (mouseX - cx) * 0.008f;
        float parY = (mouseY - cy) * 0.008f;

        Render2D.beginFrame(ctx);

        // base vertical gradient - slightly darker for particle contrast
        Render2D.rect(-10, -10, w + 20, h + 20, 0,
            0xFF0A0A16, 0xFF0B0B1C, 0xFF050508, 0xFF050508);

        // drifting accent orbs for depth (behind particles)
        float orb1x = cx - w * 0.24f + (float)Math.sin(t * 0.21f) * w * 0.03f - parX * 0.6f;
        float orb1y = h * 0.26f + (float)Math.cos(t * 0.17f) * h * 0.02f - parY * 0.6f;
        float orb2x = cx + w * 0.27f + (float)Math.cos(t * 0.16f) * w * 0.025f - parX * 0.8f;
        float orb2y = h * 0.74f + (float)Math.sin(t * 0.19f) * h * 0.022f - parY * 0.8f;
        Render2D.circle(orb1x, orb1y, 260.0f, 300.0f, ClientAccent.accentSoft(10));
        Render2D.circle(orb2x, orb2y, 320.0f, 300.0f, ClientAccent.accentSoft(7));
        Render2D.circle(cx - parX * 0.4f, h * 0.32f - parY * 0.4f, 200.0f, 220.0f, ClientAccent.accentSoft(6));

        // === Antigravity-style particle field: outer border + big V in center ===
        // V is centered slightly above buttons, behind UI but over orbs
        float vCx = cx - parX * 0.35f;
        float vCy = cy + h * 0.015f - parY * 0.35f;
        this.vParticles.render(vCx, vCy, w, h, dt, t, mouseX, mouseY);

        // rising ambient dust (extra layer, lighter)
        this.dust.render(w, h, mouseX, mouseY, delta);

        // top sheen and bottom shade for UI readability
        Render2D.rect(-10, -10, w + 20, h * 0.32f, 0,
            ClientAccent.accentSoft(8), ClientAccent.accentSoft(4), 0x00000000, 0x00000000);
        Render2D.rect(-10, h * 0.70f, w + 20, h * 0.32f + 12, 0,
            0x00000000, 0x00000000, 0x66000000, 0x66000000);

        // fluid cursor glow - on top of particles, under UI
        this.glow.render(mouseX, mouseY, delta);

        // header with parallax
        float logoSize = 34f;
        float logoY = h * 0.16f - (1.0f - easeOpen) * 20.0f + parY * 0.28f;
        float logoOffsetX = parX * 0.28f;
        int headA = alphaScale(easeOpen);

        // soft glow behind logo
        Render2D.circle(cx + logoOffsetX, logoY + 16 + parY * 0.15f, 130.0f, 150.0f,
            ColorUtil.multAlpha(ClientAccent.accentSoft(18), easeOpen));

        String part1 = "Vex";
        String part2 = "Siko";
        float w1 = Fonts.MONTSERRAT_EXTRABOLD.width(part1, logoSize);
        float w2 = Fonts.MONTSERRAT_EXTRABOLD.width(part2, logoSize);
        float logoX = cx - (w1 + w2) / 2f + logoOffsetX;
        Fonts.MONTSERRAT_EXTRABOLD.draw(part1, logoX, logoY, logoSize,
            join(headA, 0xFFFFFFFF), join(headA, 0xFFFFFFFF), join(headA, 0xFFE4E7F4), join(headA, 0xFFE4E7F4));
        Fonts.MONTSERRAT_EXTRABOLD.draw(part2, logoX + w1, logoY, logoSize,
            ClientAccent.gradientA(headA), ClientAccent.gradientB(headA),
            ClientAccent.gradientB(headA), ClientAccent.gradientA(headA));

        String themeName = ThemeManager.current().name().toUpperCase();
        float track = 2.4f;
        float tnW = spacedWidth(themeName, 7.5f, track);
        drawSpaced(themeName, cx - tnW / 2f + logoOffsetX * 0.5f, logoY + logoSize + 11, 7.5f, track,
            ColorUtil.multAlpha(ClientAccent.accentSoft(150), easeOpen));

        // buttons with slight mouse tilt
        float stackH = LABELS.length * (BTN_H + BTN_GAP) - BTN_GAP;
        float startY = h / 2f - stackH / 2f + h * 0.06f;

        for (int i = 0; i < LABELS.length; i++) {
            float p = clamp01(openP * 1.7f - i * 0.14f);
            float e = easeOutCubic(p);
            if (e <= 0f) continue;

            float targetHover = this.hovered == i ? 1f : 0f;
            float k = 1.0f - (float)Math.exp(-dt * 15.0);
            this.hoverAnim[i] += (targetHover - this.hoverAnim[i]) * k;
            float hv = this.hoverAnim[i];

            float by = startY + i * (BTN_H + BTN_GAP) + (1.0f - e) * 26.0f;
            // parallax for buttons row - subtle
            float btnParX = (mouseX - cx) * 0.006f * (1f + i * 0.07f);
            float bx = cx - BTN_W / 2f - 5f * hv + btnParX;
            float bw = BTN_W + 10f * hv;
            float bcy = by + BTN_H / 2f;

            if (hv > 0.01f) {
                Render2D.circle(bx + bw / 2f, bcy, 62.0f + hv * 18.0f, 78.0f,
                    ColorUtil.multAlpha(ClientAccent.accentSoft(55), hv * e));
            }

            int bg = ColorUtil.lerpColor(0xE6111126, ClientAccent.accent(225), 0.72f * hv);
            bg = ColorUtil.multAlpha(bg, e);
            Render2D.rect(bx, by, bw, BTN_H, BTN_R, bg);

            Render2D.rect(bx, by, bw, BTN_H / 2f, BTN_R, BTN_R, 0, 0,
                ColorUtil.multAlpha(0x14FFFFFF, hv * e));
            Render2D.rect(bx, by, bw, BTN_H, BTN_R,
                ColorUtil.multAlpha(0x18FFFFFF, e), ColorUtil.multAlpha(0x18FFFFFF, e),
                ColorUtil.multAlpha(0x00000000, e), ColorUtil.multAlpha(0x00000000, e));

            int border = ColorUtil.lerpColor(0x18FFFFFF, accent, hv * 0.75f);
            Render2D.outline(bx + 0.5f, by + 0.5f, bw - 1f, BTN_H - 1f, BTN_R - 0.5f, 1f,
                ColorUtil.multAlpha(border, e));

            if (hv > 0.01f) {
                float barH = 20.0f * hv;
                Render2D.rect(bx + 5.5f, bcy - barH / 2f, 2.6f, barH, 1.3f,
                    ColorUtil.multAlpha(accent, hv * e));
            }

            float tw = Fonts.MONTSERRAT_SEMIBOLD.width(LABELS[i], 13f);
            float tx = bx + bw / 2f - tw / 2f + 3.5f * hv;
            float ty = by + (BTN_H - 13f) / 2f + 0.5f;
            int textCol = ColorUtil.lerpColor(0xFFC9CCDC, 0xFFFFFFFF, hv);
            Fonts.MONTSERRAT_SEMIBOLD.draw(LABELS[i], tx, ty, 13f, ColorUtil.multAlpha(textCol, e));
        }

        // footer
        float footA = alphaScale(easeOpen);
        float fx = 12f;
        float fy = h - 17f;
        float dotPulse = 0.75f + 0.25f * (float)Math.sin(t * 2.2f);
        Render2D.circle(fx + 2.4f, fy + 5.4f, 2.4f, 5.0f,
            ColorUtil.multAlpha(ClientAccent.accent((float)(235 * dotPulse)), footA));
        String ver = "v1.1.5";
        Fonts.MONTSERRAT_MEDIUM.draw(ver, fx + 10f, fy + 1.5f, 8f, ColorUtil.multAlpha(0x88FFFFFF, footA));

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.getSession() != null && mc.getSession().getUsername() != null) {
            String user = mc.getSession().getUsername();
            float uw = Fonts.MONTSERRAT_MEDIUM.width(user, 8f);
            Fonts.MONTSERRAT_MEDIUM.draw(user, w - uw - 12f, fy + 1.5f, 8f, ColorUtil.multAlpha(0x77FFFFFF, footA));
        }

        Render2D.flush();
    }

    private static float easeOutCubic(float f) {
        f = clamp01(f);
        float inv = 1.0f - f;
        return 1.0f - inv * inv * inv;
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (Math.min(f, 1.0f));
    }

    private static int alphaScale(float f) {
        return (int)(clamp01(f) * 255.0f);
    }

    private static int join(int alpha, int rgb) {
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private float spacedWidth(String s, float size, float tracking) {
        float total = 0f;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            String str = Character.toString(c);
            total += Fonts.MONTSERRAT_REGULAR.width(str, size) + tracking;
        }
        return total - tracking;
    }

    private void drawSpaced(String s, float x, float y, float size, float tracking, int color) {
        float px = x;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            String str = Character.toString(c);
            Fonts.MONTSERRAT_REGULAR.draw(str, px, y, size, color);
            px += Fonts.MONTSERRAT_REGULAR.width(str, size) + tracking;
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        float cx = this.width / 2f;
        float startY = buttonStartY();

        for (int i = 0; i < LABELS.length; i++) {
            float by = startY + i * (BTN_H + BTN_GAP);
            if (click.x() >= cx - BTN_W / 2f - 5f && click.x() <= cx + BTN_W / 2f + 5f
                && click.y() >= by && click.y() <= by + BTN_H) {
                MinecraftClient mc = MinecraftClient.getInstance();
                switch (i) {
                    case 0 -> mc.setScreen(new SelectWorldScreen(this));
                    case 1 -> mc.setScreen(new MultiplayerScreen(this));
                    case 2 -> mc.setScreen(new AltManagerScreen(this));
                    case 3 -> mc.setScreen(new OptionsScreen(this, mc.options));
                }
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        float cx = this.width / 2f;
        float startY = buttonStartY();
        this.hovered = -1;
        for (int i = 0; i < LABELS.length; i++) {
            float by = startY + i * (BTN_H + BTN_GAP);
            if (mouseX >= cx - BTN_W / 2f - 5f && mouseX <= cx + BTN_W / 2f + 5f
                && mouseY >= by && mouseY <= by + BTN_H) {
                this.hovered = i;
                break;
            }
        }
    }

    private float buttonStartY() {
        float stackH = LABELS.length * (BTN_H + BTN_GAP) - BTN_GAP;
        return this.height / 2f - stackH / 2f + this.height * 0.06f;
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == 256) return false;
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        // Title screen should not close via ESC — like Dile (isPauseScreen=false + ESC ignored)
    }
}
