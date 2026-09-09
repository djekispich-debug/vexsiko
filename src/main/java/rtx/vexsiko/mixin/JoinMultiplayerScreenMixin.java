package rtx.vexsiko.mixin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.IAS;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.config.IASServerShortcutsConfig;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.screen.ServerShortcutButton;
import rtx.vexsiko.api.ui.social.AmbientDust;
import rtx.vexsiko.api.ui.social.MouseGlow;
import rtx.vexsiko.api.ui.social.VLogoParticles;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;

@Mixin(MultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    @Shadow
    private ButtonWidget buttonEdit;
    @Shadow
    private ButtonWidget buttonDelete;
    @Shadow
    private ButtonWidget buttonJoin;
    @Shadow
    private ThreePartsLayoutWidget layout;

    @Unique
    private final VLogoParticles vexsiko$mpParticles = new VLogoParticles();
    @Unique
    private final AmbientDust vexsiko$mpDust = new AmbientDust();
    @Unique
    private final MouseGlow vexsiko$mpGlow = new MouseGlow();
    @Unique
    private boolean vexsiko$layoutApplied;

    protected JoinMultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"), require = 0)
    private void vexsiko_addShortcutButtons(CallbackInfo ci) {
        MultiplayerScreen screen = (MultiplayerScreen)(Object)this;
        int index = 0;
        for (IASServerShortcutsConfig.ShortcutEntry shortcut : IASServerShortcutsConfig.load(IAS.configDirectory())) {
            Identifier texture = Identifier.of("ias", "textures/gui/server_shortcuts/" + shortcut.icon() + ".png");
            ServerShortcutButton button = new ServerShortcutButton(8 + index++ * 20, 8, () -> texture, shortcut.name(), pressed -> ConnectScreen.connect(screen, this.client, new ServerAddress(shortcut.address(), 25565), new ServerInfo(shortcut.name(), shortcut.address(), ServerInfo.ServerType.OTHER), false, null));
            this.addDrawableChild(button);
        }
        // apply layout after vanilla init
        vexsiko$applyModernLayout();
    }

    @Inject(method = "refreshWidgetPositions", at = @At("TAIL"), require = 0)
    private void vexsiko_onRefreshPositions(CallbackInfo ci) {
        vexsiko$applyModernLayout();
    }

    @Unique
    private void vexsiko$applyModernLayout() {
        if (this.serverListWidget == null || this.layout == null) return;
        int w = this.width;
        int h = this.height;
        if (w <= 0 || h <= 0) return;

        int headerH = this.layout.getHeaderHeight();
        int listLeft = 14;
        int rightPanelW = 190;
        int gap = 12;
        // list takes left side, full height between header and bottom
        int listW = Math.max(210, w - rightPanelW - listLeft - gap - 10);
        int listTop = headerH + 8;
        int listH = h - headerH - 14;
        if (listH < 60) listH = 60;
        try {
            // position(width, height, x, y)
            this.serverListWidget.position(listW, listH, listLeft, listTop);
        } catch (Exception ignored) {}

        // collect footer buttons (the 7 vanilla ones) – they are near bottom initially
        List<ButtonWidget> footerButtons = new ArrayList<>();
        for (Element el : this.children()) {
            if (el instanceof ButtonWidget bw) {
                // skip shortcut buttons at top (y < 30) and server entry buttons (inside list)
                // footer buttons are direct children of screen with y > h-100 before we move them
                // after first layout they will be at right panel, so we need to also capture them there
                // heuristic: ButtonWidget that is not IAS shortcut (x < 100 and y==8) and not inside server list
                boolean isShortcut = bw.getY() == 8 && bw.getX() < 180;
                if (isShortcut) continue;
                // after relocation, buttons are at right side – keep them
                footerButtons.add(bw);
                if (footerButtons.size() >= 7 && vexsiko$layoutApplied) break;
            }
        }
        // filter to at most 7 closest to expected labels (Join, Direct, Add, Edit, Delete, Refresh, Back)
        // sort by original position to preserve ordering
        // On first apply, footer buttons are still at bottom – sort by Y then X
        footerButtons.sort(Comparator.comparingInt((ButtonWidget b) -> b.getY()).thenComparingInt(ButtonWidget::getX));
        // Keep only last 7 if there are more (some mods add buttons)
        if (footerButtons.size() > 7) {
            // keep those with Y largest (bottom) – most likely vanilla
            footerButtons.sort(Comparator.comparingInt(ButtonWidget::getY).reversed());
            footerButtons = new ArrayList<>(footerButtons.subList(0, 7));
            footerButtons.sort(Comparator.comparingInt((ButtonWidget b) -> b.getY()).thenComparingInt(ButtonWidget::getX));
        }
        if (footerButtons.size() < 7) return;

        // reorder to desired vertical order: Join, Direct, Add, Edit, Delete, Refresh, Back
        // default order after sorting Y,X is: row1 (Join, Direct, Add) then row2 (Edit, Delete, Refresh, Back)
        // that already matches desired vertical order
        int panelX = w - rightPanelW - 12;
        int startY = listTop;
        int btnH = 20;
        int btnGap = 6;
        for (int i = 0; i < footerButtons.size(); i++) {
            ButtonWidget b = footerButtons.get(i);
            int ny = startY + i * (btnH + btnGap);
            // clamp to visible
            if (ny + btnH > h - 8) ny = h - 8 - btnH;
            b.setWidth(rightPanelW);
            b.setHeight(btnH);
            b.setPosition(panelX, ny);
        }
        vexsiko$layoutApplied = true;
    }

    @Inject(method = "updateButtonActivationStates", at = @At("TAIL"), require = 0)
    private void vexsiko_protectPinnedServerControls(CallbackInfo ci) {
        if (this.serverListWidget != null) {
            MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry serverEntry) {
                if ("mc.breakproject.pro".equalsIgnoreCase(serverEntry.getServer().address)) {
                    if (this.buttonEdit != null) this.buttonEdit.active = false;
                    if (this.buttonDelete != null) this.buttonDelete.active = false;
                }
            }
        }
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_replaceBackground(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // fully opaque to hide vanilla panorama/blur – like MainMenu
        ctx.fill(0, 0, this.width, this.height, 0xFF090B18);
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), require = 0)
    private void vexsiko_renderModernBackground(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;
        float cx = w / 2f;
        float cy = h / 2f;
        long now = System.currentTimeMillis();
        float t = now * 0.001f;
        float dt = Math.min(delta, 3.0f);

        Render2D.beginFrame(ctx);

        // dark blurred gradient base (replaces panorama)
        Render2D.rect(-10, -10, w + 20, h + 20, 0,
            0xFF0B0B18, 0xFF0C0D1E, 0xFF060608, 0xFF060608);

        // soft orbs
        float parX = (mouseX - cx) * 0.006f;
        float parY = (mouseY - cy) * 0.006f;
        Render2D.circle(cx - w * 0.22f - parX * 0.7f, h * 0.28f - parY * 0.6f, 220f, 260f, ClientAccent.accentSoft(9));
        Render2D.circle(cx + w * 0.25f - parX * 0.9f, h * 0.72f - parY * 0.8f, 280f, 300f, ClientAccent.accentSoft(7));

        // V particles in background (subtle, behind list)
        float vCx = cx - parX * 0.4f;
        float vCy = cy - parY * 0.4f;
        vexsiko$mpParticles.render(vCx, vCy, w, h, dt, t, mouseX, mouseY);
        vexsiko$mpDust.render(w, h, mouseX, mouseY, delta);
        vexsiko$mpGlow.render(mouseX, mouseY, delta);

        // right panel glass card behind buttons
        int rightPanelW = 190;
        int panelX = w - rightPanelW - 12;
        int headerH = (this.layout != null ? this.layout.getHeaderHeight() : 33);
        int panelY = headerH + 8;
        int panelH = h - headerH - 14;
        // glass + border
        Render2D.rect(panelX - 6, panelY - 6, rightPanelW + 12, panelH + 12, 10,
            ColorUtil.withAlpha(0xFF141422, 165));
        Render2D.rect(panelX - 6, panelY - 6, rightPanelW + 12, panelH * 0.55f + 12, 10, 10, 0, 0,
            ColorUtil.withAlpha(0xFFFFFFFF, 10), ColorUtil.withAlpha(0xFFFFFFFF, 10),
            0, 0);
        Render2D.outline(panelX - 5.5f, panelY - 5.5f, rightPanelW + 11, panelH + 11, 10f, 1f,
            ColorUtil.withAlpha(0xFFFFFFFF, 14));
        Render2D.outline(panelX - 5.5f, panelY - 5.5f, rightPanelW + 11, panelH + 11, 10f, 1f,
            ClientAccent.accentSoft(10));

        // left list card
        int listLeft = 14;
        int listW = Math.max(210, w - rightPanelW - listLeft - 22 - 10);
        Render2D.rect(listLeft - 6, panelY - 6, listW + 12, panelH + 12, 10,
            ColorUtil.withAlpha(0xFF0F0F1B, 150));
        Render2D.outline(listLeft - 5.5f, panelY - 5.5f, listW + 11, panelH + 11, 10f, 1f,
            ColorUtil.withAlpha(0xFFFFFFFF, 10));

        // top header bar
        Render2D.rect(-10, -10, w + 20, headerH + 10, 0,
            ClientAccent.accentSoft(7), ClientAccent.accentSoft(4), 0x00000000, 0x00000000);

        Render2D.flush();
    }

}
