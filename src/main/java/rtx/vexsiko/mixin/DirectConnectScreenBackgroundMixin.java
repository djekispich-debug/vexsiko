package rtx.vexsiko.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.social.AmbientDust;
import rtx.vexsiko.api.ui.social.MouseGlow;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.render.render2d.Render2D;

@Mixin(DirectConnectScreen.class)
public abstract class DirectConnectScreenBackgroundMixin extends Screen {
    @Unique
    private final AmbientDust vexsiko$dust = new AmbientDust();
    @Unique
    private final MouseGlow vexsiko$glow = new MouseGlow();
    public DirectConnectScreenBackgroundMixin(net.minecraft.text.Text title){ super(title); }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_replaceBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci){
        ctx.fill(0, 0, this.width, this.height, 0xFF090B18);
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), require = 0)
    private void vexsiko_drawAuroraBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci){
        int w=this.width; int h=this.height;
        float cx=w/2f; float cy=h/2f;
        Render2D.beginFrame(ctx);
        Render2D.rect(-10,-10,w+20,h+20,0, 0xFF0D0D1E, 0xFF0C0E22, 0xFF060710, 0xFF07070F);
        float parX=(mouseX-cx)*0.007f; float parY=(mouseY-cy)*0.007f;
        Render2D.circle(cx - w*0.30f - parX*0.6f, cy*0.50f - parY*0.6f, 250f, 300f, ClientAccent.accentSoft(7));
        Render2D.circle(cx + w*0.32f - parX*0.85f, cy*1.02f - parY*0.8f, 290f, 330f, ClientAccent.accentSoft(5));
        Render2D.rect(-10, h*0.45f, w+20, 1, ClientAccent.accentSoft(9));
        Render2D.rect(-10, h*0.45f-18, w+20, 36, 0, 0x00000000, ClientAccent.accentSoft(4), ClientAccent.accentSoft(4), 0x00000000);
        Render2D.rect(-10,-10,w+20,h*0.30f,0, ClientAccent.accentSoft(7), 0x00000000,0x00000000,0x00000000);
        vexsiko$dust.render(w,h,mouseX,mouseY,delta);
        vexsiko$glow.render(mouseX,mouseY,delta);
        Render2D.flush();
    }
}
