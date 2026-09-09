package rtx.vexsiko.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("handSwingTicks")
    public void vexsiko_setSwingTime(int var1);

    @Accessor("handSwingProgress")
    public void vexsiko_setAttackAnim(float var1);

    @Accessor("handSwinging")
    public void vexsiko_setSwinging(boolean var1);

    @Accessor("jumpingCooldown")
    public void vexsiko_setNoJumpDelay(int var1);

    @Accessor("preferredHand")
    public void vexsiko_setSwingingArm(Hand var1);
}
