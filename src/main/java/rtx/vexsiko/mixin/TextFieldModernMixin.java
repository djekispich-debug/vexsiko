package rtx.vexsiko.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldModernMixin {

    @Inject(method = "renderWidget", at = @At("HEAD"), require = 0)
    private void vexsiko_drawModernEditBox(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TextFieldWidget self = (TextFieldWidget)(Object)this;
        // only style reasonably sized text fields (server name/address, etc) – skip tiny search boxes
        int w = self.getWidth();
        int h = self.getHeight();
        if (w < 80 || h < 12 || h > 24) return;
        int x = self.getX();
        int y = self.getY();
        boolean focused = ((net.minecraft.client.gui.widget.ClickableWidget)(Object)self).isFocused();
        if (self.drawsBackground()) {
            self.setDrawsBackground(false);
        }
        Render2D.beginFrame(ctx);
        int bg = focused
            ? ColorUtil.lerpColor(0xFF1E1E32, ClientAccent.accent(45), 0.38f)
            : ColorUtil.withAlpha(0xFF1A1A2E, 165);
        Render2D.rect(x, y, w, h, 6f, bg);
        // inner top highlight
        Render2D.rect(x, y, w, h * 0.45f, 6f, 6f, 0, 0,
            ColorUtil.withAlpha(0xFFFFFFFF, 10), ColorUtil.withAlpha(0xFFFFFFFF, 10), 0, 0);
        // border
        int border = focused ? ClientAccent.accent(160) : ColorUtil.withAlpha(0xFFFFFFFF, 14);
        Render2D.outline(x + 0.5f, y + 0.5f, w - 1f, h - 1f, 5.5f, 1f, border);
        if (focused) {
            Render2D.circle(x + w / 2f, y + h / 2f, w * 0.38f, w * 0.62f, ClientAccent.accentSoft(12));
        }
        // let vanilla draw text/cursor on top – no flush here, Screen will flush at end
    }
}
