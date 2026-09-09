package rtx.vexsiko.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.social.AmbientDust;
import rtx.vexsiko.api.ui.social.MouseGlow;
import rtx.vexsiko.api.ui.theme.ClientAccent;

@Mixin(SelectWorldScreen.class)
public abstract class SelectWorldScreenBackgroundMixin extends Screen {
    @Unique private final AmbientDust vexsiko$dust = new AmbientDust();
    @Unique private final MouseGlow vexsiko$glow = new MouseGlow();
    public SelectWorldScreenBackgroundMixin(net.minecraft.text.Text title){ super(title); }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_replaceBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci){
        ctx.fill(0,0,this.width,this.height, 0xFF090B18);
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER), require = 0)
    private void vexsiko_drawBg(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci){
        int w=this.width, h=this.height;
        float cx=w/2f, cy=h/2f;
        long time=System.currentTimeMillis();
        float t=time*0.001f;
        ctx.fill(0,0,w,h, 0xFF090B18);
        ctx.fillGradient(0,0,w,h/2, 0xFF0C0D22, 0x00000000);
        ctx.fillGradient(0,h/2,w,h, 0x00000000, 0xFF07070F);
        float parX=(mouseX-cx)*0.007f, parY=(mouseY-cy)*0.007f;
        // soft orbs via Render2D circles (instead of solid rects)
        rtx.vexsiko.utils.render.render2d.Render2D.beginFrame(ctx);
        rtx.vexsiko.utils.render.render2d.Render2D.circle(cx - w*0.28f - parX*0.6f, cy - h*0.22f - parY*0.7f, 220f, 260f, ClientAccent.accentSoft(8));
        rtx.vexsiko.utils.render.render2d.Render2D.circle(cx + w*0.30f - parX*0.85f, cy + h*0.28f - parY*0.8f, 260f, 300f, ClientAccent.accentSoft(6));
        rtx.vexsiko.utils.render.render2d.Render2D.flush();
        // simple star dots via DrawContext
        for(int i=0;i<36;i++){
            long seed=i*6364136223846793L + time/220;
            float rx=((seed>>16 & 0xFF)/255f)*w;
            float ry=((seed>>8 & 0xFF)/255f)*h;
            float tw=0.45f+0.55f*(float)Math.sin(t*1.0f + i*0.7f);
            int a=(int)(tw*120);
            if(i%4==0) ctx.fill((int)rx,(int)ry,(int)rx+1,(int)ry+1, (a<<24)|0xFFFFFF);
        }
        ctx.fillGradient(0,0,w,h/3, 0x22000000, 0x00000000);
    }
}
