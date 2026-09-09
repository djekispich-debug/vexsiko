package rtx.vexsiko.api.ui.social;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.mixin.ScreenAccessor;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.fonts.Fonts;
import rtx.vexsiko.utils.render.others.RectUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;
import rtx.vexsiko.utils.chat.ChatMessage;
import rtx.vexsiko.utils.storage.alt.AltManager;
import rtx.vexsiko.utils.session.SessionChanger;

public final class AltManagerScreen
extends Screen {
    private static final int ROW_H = 24;
    private static final int PANEL_W = 320;
    private static final int PANEL_H = 240;
    private final Screen parent;
    private final List<String> alts = new ArrayList<String>();
    private final List<Btn> buttons = new ArrayList<Btn>();
    private String inputBuffer = "";
    private int selected = -1;
    private String status;
    private int statusColor = -1;
    private int px;
    private int py;
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private final MouseGlow glow = new MouseGlow();
    private final AmbientDust dust = new AmbientDust();

    public AltManagerScreen() {
        this(null);
    }

    public AltManagerScreen(Screen parent) {
        super(Text.literal("VexSiko Alt Manager"));
        this.parent = parent;
    }

    private float mx() {
        return (float)((double)this.client.mouse.getX() * (double)this.width / (double)this.client.getWindow().getScaledWidth());
    }

    private float my() {
        return (float)((double)this.client.mouse.getY() * (double)this.height / (double)this.client.getWindow().getScaledHeight());
    }

    @Override
    protected void init() {
        this.alts.clear();
        this.alts.addAll(AltManager.getAlts());
        this.px = (this.width - PANEL_W) / 2;
        this.py = (this.height - PANEL_H) / 2;
        this.listX = this.px + 14;
        this.listY = this.py + 52;
        this.listW = PANEL_W - 28;
        this.listH = PANEL_H - 118;
        this.buttons.clear();
        int bw = 66;
        int by = this.py + PANEL_H - 44;
        this.buttons.add(new Btn("Add", this.listX, by, bw, 18, this::addAlt));
        this.buttons.add(new Btn("Login", this.listX + bw + 6, by, bw, 18, this::loginSelected));
        this.buttons.add(new Btn("Remove", this.listX + (bw + 6) * 2, by, 72, 18, this::removeSelected));
        this.buttons.add(new Btn("Done", this.px + PANEL_W - 14 - bw, by, bw, 18, this::close));
    }

    private void addAlt() {
        String name = this.inputBuffer.trim();
        if (name.isEmpty()) {
            this.status = "Type a nickname first";
            this.statusColor = -256;
            return;
        }
        if (AltManager.add(name)) {
            if (!this.alts.contains(name)) {
                this.alts.add(name);
            }
            this.status = "Added: " + name;
            this.statusColor = -10177546;
        } else {
            this.status = "Already in list: " + name;
            this.statusColor = -256;
        }
        this.inputBuffer = "";
    }

    private void loginSelected() {
        String name = this.selected >= 0 && this.selected < this.alts.size() ? this.alts.get(this.selected) : this.inputBuffer.trim();
        if (name.isEmpty()) {
            this.status = "Select an account or type a nickname";
            this.statusColor = -256;
            return;
        }
        // debug sessionSetter
        if (SessionChanger.getCurrentUsername().isEmpty() && this.client != null && this.client.getSession() == null) {
            this.status = "Session not ready — try again";
            this.statusColor = -65536;
            return;
        }
        boolean ok = AltManager.login(name);
        if (ok) {
            this.status = "Logged in as " + name;
            this.statusColor = -10177546;
            if (this.client != null && this.client.player != null) {
                ChatMessage.brandmessage("Logged in as " + name + " — rejoin to apply");
            }
            // update UI immediately: show active
            this.selected = this.alts.indexOf(name);
            return;
        }
        this.status = "Login failed — see log";
        this.statusColor = -65536;
    }

    private void removeSelected() {
        if (this.selected < 0 || this.selected >= this.alts.size()) {
            this.status = "Select an account first";
            this.statusColor = -256;
            return;
        }
        String name = this.alts.get(this.selected);
        AltManager.remove(name);
        this.alts.remove(this.selected);
        this.selected = -1;
        this.status = "Removed: " + name;
        this.statusColor = -10177546;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float mx = this.mx();
        float my = this.my();
        float t = System.currentTimeMillis() * 0.001f;
        float cx = this.width / 2f;
        float cy = this.height / 2f;
        // distinct background – aurora gradient similar to MainMenu but without V, with diagonal orbs
        Render2D.beginFrame(context);
        Render2D.rect(-10, -10, this.width + 20, this.height + 20, 0,
            0xFF0C0C1E, 0xFF0E0E1E, 0xFF060608, 0xFF07070F);
        float parX = (mouseX - cx) * 0.008f;
        float parY = (mouseY - cy) * 0.008f;
        // diagonal aurora orbs – different placement from MainMenu
        Render2D.circle(cx - this.width * 0.28f - parX * 0.6f, cy - this.height * 0.22f - parY * 0.7f, 280f, 330f, ClientAccent.accentSoft(8));
        Render2D.circle(cx + this.width * 0.30f - parX * 0.9f, cy + this.height * 0.28f - parY * 0.8f, 320f, 360f, ClientAccent.accentSoft(6));
        // subtle horizontal accent band
        Render2D.rect(-10, cy - 1, this.width + 20, 1, ClientAccent.accentSoft(10));
        Render2D.rect(-10, cy - 24, this.width + 20, 48, 0,
            0x00000000, ClientAccent.accentSoft(5), ClientAccent.accentSoft(5), 0x00000000);
        this.dust.render(this.width, this.height, mouseX, mouseY, delta);
        this.glow.render((float)mouseX, (float)mouseY, delta);
        // top vignette
        Render2D.rect(-10, -10, this.width + 20, this.height * 0.38f, 0,
            ClientAccent.accentSoft(9), 0x00000000, 0x00000000, 0x00000000);
        Render2D.flush();
        Render2D.beginFrame(context);
        Render2D.rect(this.px, this.py, PANEL_W, PANEL_H, 14.0f, 0xF20E0E1A);
        Render2D.rect(this.px, this.py, PANEL_W, PANEL_H, 14.0f, ClientAccent.accentSoft(10));
        RectUtil.drawClientRect(this.px, this.py, PANEL_W, PANEL_H, 14.0f, 1.0f);
        int[] palette = ClientAccent.currentPalette();
        int accent = palette != null && palette.length > 0 ? palette[0] | 0xFF000000 : -2234369;
        // rounded accent pill line at top (inset so ends are round, not square)
        Render2D.rect(this.px + 14, this.py + 7, PANEL_W - 28, 2.5f, 1.25f, accent);
        Fonts.MONTSERRAT_BOLD.draw("ALT MANAGER", this.px + 14.0f, this.py + 12.0f, 10.0f, -1);
        String sub = "accounts: " + this.alts.size() + (AltManager.getActiveAlt() != null ? "   active: " + AltManager.getActiveAlt() : "");
        float sw = Fonts.MONTSERRAT_MEDIUM.width(sub, 6.5f);
        Fonts.MONTSERRAT_MEDIUM.draw(sub, this.px + PANEL_W - 14.0f - sw, this.py + 15.0f, 6.5f, -6710887);
        Render2D.rect(this.listX, this.py + 40.0f, this.listW, 1.0f, ColorUtil.multAlpha(-1, 0.08f));
        if (this.alts.isEmpty()) {
            String empty = "No accounts yet \u2014 type nickname and press Add";
            float ew = Fonts.MONTSERRAT_MEDIUM.width(empty, 7.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(empty, this.listX + (this.listW - ew) / 2.0f, this.listY + this.listH / 2.0f - 4.0f, 7.5f, -4144960);
        }
        for (int i = 0; i < this.alts.size(); ++i) {
            float ry = this.listY + (float)(i * ROW_H);
            if (ry + ROW_H > this.listY + this.listH) break;
            boolean sel = i == this.selected;
            boolean hover = mx >= (float)this.listX && mx <= (float)(this.listX + this.listW) && my >= ry && my < ry + (float)ROW_H;
            if (sel || hover) {
                Render2D.rect(this.listX, ry, this.listW, ROW_H - 3, 8.0f, sel ? ColorUtil.multAlpha(accent, 0.32f) : ColorUtil.multAlpha(accent, 0.14f));
            } else if (i % 2 == 0) {
                Render2D.rect(this.listX, ry, this.listW, ROW_H - 3, 8.0f, 1140850688);
            }
            String label = this.alts.get(i);
            if (label.equals(AltManager.getActiveAlt())) {
                label = "● " + label;
            }
            Fonts.MONTSERRAT_MEDIUM.draw(label, this.listX + 8.0f, ry + 7.0f, 7.5f, sel ? FRIEND : -1);
        }
        // custom input — no white TextFieldWidget, dark styled
        int ix = this.listX;
        int iy = this.py + PANEL_H - 68;
        int iw = this.listW - 4;
        int ih = 16;
        Render2D.rect(ix, iy, iw, ih, 6.0f, ColorUtil.multAlpha(accent, 0.18f));
        Render2D.rect(ix, iy, iw, ih, 6.0f, ColorUtil.multAlpha(-1, 0.06f));
        String display = this.inputBuffer.isEmpty() ? "Type nickname..." : this.inputBuffer + (System.currentTimeMillis() % 1000 < 500 ? "_" : "");
        Fonts.MONTSERRAT_MEDIUM.draw(display, ix + 6.0f, iy + 4.0f, 7.0f, this.inputBuffer.isEmpty() ? -4144960 : -1);
        if (this.status != null) {
            Fonts.MONTSERRAT_MEDIUM.draw(this.status, this.listX + 1.0f, this.py + PANEL_H - 86.0f, 6.5f, this.statusColor);
        }
        for (Btn btn : this.buttons) {
            boolean hov = mx >= (float)btn.x && mx <= (float)(btn.x + btn.w) && my >= (float)btn.y && my <= (float)(btn.y + btn.h);
            Render2D.rect(btn.x, btn.y, btn.w, btn.h, 8.0f, ColorUtil.multAlpha(accent, hov ? 0.85f : 0.45f));
            float tw = Fonts.MONTSERRAT_BOLD.width(btn.label, 7.5f);
            Fonts.MONTSERRAT_BOLD.draw(btn.label, (float)btn.x + ((float)btn.w - tw) * 0.5f, (float)btn.y + 5.5f, 7.5f, -1);
        }
        Render2D.flush();
    }

    private static final int FRIEND = -10177546;

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = (float)click.x();
        float my = (float)click.y();
        if (click.button() == 0 && my >= (float)(this.py + PANEL_H - 68) && my <= (float)(this.py + PANEL_H - 52) && mx >= (float)this.listX && mx <= (float)(this.listX + this.listW - 4)) {
            return true;
        }
        for (Btn btn : this.buttons) {
            if (!(mx >= (float)btn.x && mx <= (float)(btn.x + btn.w) && my >= (float)btn.y && my <= (float)(btn.y + btn.h))) continue;
            btn.action.run();
            return true;
        }
        if (click.button() == 0 && mx >= (float)this.listX && mx <= (float)(this.listX + this.listW)) {
            int index = (int)((my - (double)this.listY) / 24.0);
            if (index >= 0 && index < this.alts.size()) {
                this.selected = index;
                return true;
            }
            this.selected = -1;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (keyInput.key() == 256) {
            this.close();
            return true;
        }
        if (keyInput.key() == 259) {
            if (!this.inputBuffer.isEmpty()) {
                this.inputBuffer = this.inputBuffer.substring(0, this.inputBuffer.length() - 1);
            }
            return true;
        }
        if (keyInput.key() == 257) {
            this.addAlt();
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        String s = charInput.asString();
        if (s != null && !s.isEmpty() && s.chars().allMatch(c -> c >= 32 && c < 127)) {
            if (this.inputBuffer.length() + s.length() <= 16) {
                this.inputBuffer += s;
            }
            return true;
        }
        return super.charTyped(charInput);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private final class Btn {
        final String label;
        final int x;
        final int y;
        final int w;
        final int h;
        final Runnable action;

        Btn(String label, int x, int y, int w, int h, Runnable action) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.action = action;
        }
    }
}
