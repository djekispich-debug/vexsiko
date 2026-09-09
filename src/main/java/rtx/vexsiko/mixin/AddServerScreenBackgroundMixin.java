package rtx.vexsiko.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.social.AmbientDust;
import rtx.vexsiko.api.ui.social.MouseGlow;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.render.render2d.Render2D;

@Mixin(AddServerScreen.class)
public abstract class AddServerScreenBackgroundMixin extends Screen {

    @Unique
    private final AmbientDust vexsiko$dust = new AmbientDust();
    @Unique
    private final MouseGlow vexsiko$glow = new MouseGlow();

    public AddServerScreenBackgroundMixin(net.minecraft.text.Text title) { super(title); }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_replaceBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ctx.fill(0, 0, this.width, this.height, 0xFF090B18);
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), require = 0)
    private void vexsiko_drawAuroraBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;
        float cx = w / 2f;
        float cy = h / 2f;
        float t = System.currentTimeMillis() * 0.001f;

        Render2D.beginFrame(ctx);
        // distinct aurora – darker than main menu, vertical gradient with accent haze
        Render2D.rect(-10, -10, w + 20, h + 20, 0,
            0xFF0D0D1E, 0xFF0C0E22, 0xFF060710, 0xFF07070F);
        float parX = (mouseX - cx) * 0.007f;
        float parY = (mouseY - cy) * 0.007f;
        // two offset orbs diagonal, softer than main menu
        Render2D.circle(cx - w * 0.32f - parX * 0.6f, cy * 0.55f - parY * 0.6f, 260f, 310f, ClientAccent.accentSoft(7));
        Render2D.circle(cx + w * 0.34f - parX * 0.85f, cy * 1.05f - parY * 0.8f, 300f, 340f, ClientAccent.accentSoft(5));
        // central accent band – thinner and lower than alt manager
        Render2D.rect(-10, h * 0.42f, w + 20, 1, ClientAccent.accentSoft(9));
        Render2D.rect(-10, h * 0.42f - 18, w + 20, 36, 0,
            0x00000000, ClientAccent.accentSoft(4), ClientAccent.accentSoft(4), 0x00000000);
        // subtle vignette
        Render2D.rect(-10, -10, w + 20, h * 0.32f, 0,
            ClientAccent.accentSoft(7), 0x00000000, 0x00000000, 0x00000000);
        Render2D.rect(-10, h * 0.78f, w + 20, h * 0.24f + 10, 0,
            0x00000000, 0x00000000, 0x44000000, 0x44000000);

        this.vexsiko$dust.render(w, h, mouseX, mouseY, delta);
        this.vexsiko$glow.render(mouseX, mouseY, delta);

        // frame for content – very faint border to tie with server list style
        Render2D.flush();
    }
}
