package rtx.vexsiko.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;

@Mixin(PressableWidget.class)
public abstract class MultiplayerButtonStyleMixin {

    @Inject(method = "drawButton", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_drawModernButton(DrawContext ctx, CallbackInfo ci) {
        PressableWidget self = (PressableWidget)(Object)this;
        int w = self.getWidth();
        int h = self.getHeight();
        // handle all pressable widgets: from tiny arrows (16-30px) to full buttons, skip too flat
        if (h < 12 || h > 32) return;
        if (w < 14) return;

        ci.cancel();

        int x = self.getX();
        int y = self.getY();
        boolean active = self.active;
        boolean hovered = false;
        try {
            hovered = self.isHovered();
        } catch (Exception ignored) {}

        Render2D.beginFrame(ctx);

        float hv = hovered && active ? 1f : 0f;
        int bg;
        if (!active) {
            bg = ColorUtil.withAlpha(0xFF2A2A3F, 130);
        } else if (hovered) {
            bg = ColorUtil.lerpColor(0xFF1E1E32, ClientAccent.accent(195), 0.54f);
        } else {
            bg = ColorUtil.lerpColor(0xFF1A1A2E, ClientAccent.accent(175), 0.30f);
        }
        if (!active) bg = ColorUtil.multAlpha(bg, 0.60f);

        boolean small = w < 50;
        float r = small ? Math.min(6f, h * 0.38f) : (h >= 20 ? 8f : 6f);
        Render2D.rect(x, y, w, h, r, bg);
        if (!small) {
            Render2D.rect(x, y, w, h * 0.48f, r, r, 0, 0,
                ColorUtil.withAlpha(0xFFFFFFFF, (int)(18 + 10 * hv)), ColorUtil.withAlpha(0xFFFFFFFF, (int)(18 + 10 * hv)), 0, 0);
        } else {
            Render2D.rect(x, y, w, h * 0.45f, r, r, 0, 0,
                ColorUtil.withAlpha(0xFFFFFFFF, (int)(12 + 6 * hv)), ColorUtil.withAlpha(0xFFFFFFFF, (int)(12 + 6 * hv)), 0, 0);
        }
        int border = !active
            ? ColorUtil.withAlpha(0xFFFFFFFF, 10)
            : (hovered ? ClientAccent.accent(150) : ColorUtil.withAlpha(0xFFFFFFFF, 18));
        Render2D.outline(x + 0.5f, y + 0.5f, w - 1f, h - 1f, r - 0.5f, 1f, border);
        if (hovered && active) {
            float glowR = small ? w * 0.85f : w * 0.42f;
            float glowR2 = small ? w * 1.05f : w * 0.68f;
            Render2D.circle(x + w / 2f, y + h / 2f, glowR, glowR2, ClientAccent.accentSoft(small ? 18 : 14));
            if (!small) {
                Render2D.rect(x + 5f, y + 5.5f, 2.2f, h - 11f, 1.1f, ClientAccent.accent(215));
            } else {
                Render2D.rect(x + 2.5f, y + 4f, 1.8f, h - 8f, 0.9f, ClientAccent.accent(215));
            }
        }
        if (!(hovered && active)) {
            Render2D.outline(x + 1.5f, y + 1.5f, w - 3f, h - 3f, r - 1.2f, 0.5f, ColorUtil.withAlpha(0xFFFFFFFF, 6));
        }
    }
}
