package rtx.vexsiko.mixin.chatanim;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.mods.chatanim.config.ModConfig;
import rtx.vexsiko.api.modules.impl.Visuals.BetterMinecraft;

@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)

public abstract class ChatScreenMixin {
    @Unique
    private boolean vexsiko_chatAnimWasOpenedLastFrame = false;
    @Unique
    private long vexsiko_chatAnimLastOpenTime = 0L;
    @Unique
    private float vexsiko_chatAnimDisplacement = 0.0f;

    @Unique
    private float vexsiko_chatAnimCalculateDisplacement() {
        ModConfig config = ModConfig.getConfig();
        if (!BetterMinecraft.chatAnimationsEnabled() || !config.enableTextFieldAnimation) {
            return 0.0f;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !this.vexsiko_chatAnimWasOpenedLastFrame && !client.player.isSleeping()) {
            this.vexsiko_chatAnimWasOpenedLastFrame = true;
            this.vexsiko_chatAnimLastOpenTime = System.currentTimeMillis();
        }
        float fadeTime = config.fadeTimeTextField;
        float fadeOffset = 8.0f;
        float screenFactor = (float)client.getWindow().getFramebufferHeight() / 1080.0f;
        float timeSinceOpen = Math.min((float)(System.currentTimeMillis() - this.vexsiko_chatAnimLastOpenTime), fadeTime);
        float alpha = 1.0f - timeSinceOpen / fadeTime;
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float modifiedAlpha = c3 * alpha * alpha * alpha - c1 * alpha * alpha;
        return modifiedAlpha * fadeOffset * screenFactor;
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_25294(IIIII)V")}, require=0)
    private void vexsiko_chatAnimWrapBackgroundFill(DrawContext graphics, int x0, int y0, int x1, int y1, int color, Operation<Void> original) {
        this.vexsiko_chatAnimDisplacement = this.vexsiko_chatAnimCalculateDisplacement();
        if (this.vexsiko_chatAnimDisplacement != 0.0f) {
            graphics.getMatrices().pushMatrix();
            graphics.getMatrices().translate(0.0f, this.vexsiko_chatAnimDisplacement);
            original.call(new Object[]{graphics, x0, y0, x1, y1, color});
            graphics.getMatrices().popMatrix();
        } else {
            original.call(new Object[]{graphics, x0, y0, x1, y1, color});
        }
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/screen/Screen;method_25394(Lnet/minecraft/class_332;IIF)V")}, require=0)
    private void vexsiko_chatAnimWrapSuperAndSuggestions(ChatScreen instance, DrawContext graphics, int mouseX, int mouseY, float delta, Operation<Void> original) {
        if (this.vexsiko_chatAnimDisplacement != 0.0f) {
            graphics.getMatrices().pushMatrix();
            graphics.getMatrices().translate(0.0f, this.vexsiko_chatAnimDisplacement);
            original.call(new Object[]{instance, graphics, mouseX, mouseY, Float.valueOf(delta)});
            graphics.getMatrices().popMatrix();
        } else {
            original.call(new Object[]{instance, graphics, mouseX, mouseY, Float.valueOf(delta)});
        }
    }

    @Inject(method="removed", at={@At(value="HEAD")}, require = 0)
    private void vexsiko_chatAnimClosed(CallbackInfo ci) {
        this.vexsiko_chatAnimWasOpenedLastFrame = false;
    }
}

