package rtx.vexsiko.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {
    @Invoker("setWorld")
    public void vexsiko_updateLevelInEngines(ClientWorld var1);

    @Invoker("doAttack")
    public boolean vexsiko_startAttack();

    @Invoker("doItemUse")
    public void vexsiko_startUseItem();

    @Accessor("itemUseCooldown")
    public int vexsiko_getRightClickDelay();

    @Accessor("itemUseCooldown")
    public void vexsiko_setRightClickDelay(int var1);
}
