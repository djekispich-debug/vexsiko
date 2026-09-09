package rtx.vexsiko.mixin;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.vexsiko.api.modules.impl.Utils.guishare.GuiHoldPoseState;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateMixin
implements GuiHoldPoseState {
    @Unique
    private float[] vexsiko_guiHoldPose;

    @Override
    public void vexsiko_setGuiHoldPose(float[] pose) {
        this.vexsiko_guiHoldPose = pose;
    }

    @Override
    public float[] vexsiko_getGuiHoldPose() {
        return this.vexsiko_guiHoldPose;
    }
}

