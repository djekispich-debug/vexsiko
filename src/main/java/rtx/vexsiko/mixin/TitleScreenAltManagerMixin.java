package rtx.vexsiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.ui.social.MainMenuScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenAltManagerMixin {
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void vexsiko_replaceTitle(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null) {
            mc.setScreen(new MainMenuScreen());
            ci.cancel();
        }
    }
}
