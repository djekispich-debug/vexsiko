package rtx.vexsiko.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChatHud.class)
public abstract class ChatBackgroundFixMixin {

    // vanilla draws chat background with x1 = -2 (bipush -2) – extend left to -8 to avoid text sticking out with ChatHeads
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud$Backend;fill(IIIII)V"), index = 0, require = 0)
    private int vexsiko_extendChatBgLeft(int x1) {
        if (x1 == -2) {
            return -8;
        }
        return x1;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud$Backend;fill(IIIII)V"), index = 2, require = 0)
    private int vexsiko_extendChatBgRight(int x2) {
        // widen right side of main chat background to avoid clipping with heads
        try {
            int diff = rtx.vexsiko.api.mods.chathads.ChatHeads.getTextWidthDifference(rtx.vexsiko.api.mods.chathads.ChatHeads.getLineData());
            if (diff > 0 && x2 > 50) {
                return x2 + 6;
            }
        } catch (Exception ignored) {}
        return x2;
    }

    // per-line background (method_75802) – also extend left by 6px when head is present
    @ModifyArg(method = "method_75802", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud$Backend;fill(IIIII)V"), index = 0, require = 0)
    private static int vexsiko_extendLineBgLeft(int x1) {
        // original x1 comes from caller (usually 0), shift left to cover head icon
        // detect head case by checking if ChatHeads is active – if text width difference >0, shift
        try {
            int diff = rtx.vexsiko.api.mods.chathads.ChatHeads.getTextWidthDifference(rtx.vexsiko.api.mods.chathads.ChatHeads.getLineData());
            if (diff > 0) {
                return x1 - 6;
            }
        } catch (Exception ignored) {}
        return x1;
    }
}
