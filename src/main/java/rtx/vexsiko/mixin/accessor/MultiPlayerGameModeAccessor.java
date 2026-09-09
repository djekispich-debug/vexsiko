package rtx.vexsiko.mixin.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface MultiPlayerGameModeAccessor {
    @Invoker("syncSelectedSlot")
    public void vexsiko_ensureHasSentCarriedItem();

    @Accessor("breakingBlock")
    public boolean vexsiko_isDestroying();

    @Accessor("currentBreakingPos")
    public BlockPos vexsiko_getDestroyBlockPos();

    @Accessor("currentBreakingProgress")
    public float vexsiko_getDestroyProgress();

    @Accessor("breakingBlock")
    public void vexsiko_setDestroying(boolean var1);

    @Accessor("blockBreakingCooldown")
    public void vexsiko_setDestroyDelay(int var1);

    @Accessor("currentBreakingProgress")
    public void vexsiko_setDestroyProgress(float var1);
}
