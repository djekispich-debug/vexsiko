package rtx.vexsiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public abstract class ServerListBackgroundMixin {
    @Inject(method = "drawMenuListBackground", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_skipListBg(DrawContext ctx, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && (mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof net.minecraft.client.gui.screen.world.SelectWorldScreen)) {
            ci.cancel();
        }
    }
    @Inject(method = "drawHeaderAndFooterSeparators", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_skipSeparators(DrawContext ctx, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && (mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof net.minecraft.client.gui.screen.world.SelectWorldScreen)) {
            ci.cancel();
        }
    }
}
