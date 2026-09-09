package rtx.vexsiko.mixin;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.modules.impl.Utils.guishare.GuiHoldPose;
import rtx.vexsiko.api.modules.impl.Utils.guishare.GuiHoldPoseState;
import rtx.vexsiko.api.modules.impl.Visuals.NameTags;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public abstract class AvatarRendererMixin {
    @Inject(method="updateRenderState", at={@At(value="TAIL")}, require = 0)
    private void vexsiko_hidePlayerPlates(PlayerLikeEntity entity, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        if (NameTags.hidesNameTagFor((Entity)entity)) {
            state.displayName = null;
            state.playerName = null;
        }
    }

    @Inject(method="updateRenderState", at={@At(value="TAIL")}, require = 0)
    private void vexsiko_captureGuiHoldPose(PlayerLikeEntity entity, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        if (state instanceof GuiHoldPoseState) {
            GuiHoldPoseState holder = (GuiHoldPoseState)state;
            holder.vexsiko_setGuiHoldPose(GuiHoldPose.compute((PlayerLikeEntity)entity, (float)tickDelta));
        }
    }
}

