package rtx.vexsiko.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.vexsiko.api.modules.impl.Visuals.seeinvisible.RevealTintHolder;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateRevealMixin implements RevealTintHolder {
    @Unique
    private int vexsiko_revealTint = -1;

    @Override
    public int vexsiko$getRevealTint() {
        return this.vexsiko_revealTint;
    }

    @Override
    public void vexsiko$setRevealTint(int tint) {
        this.vexsiko_revealTint = tint;
    }
}
