package rtx.vexsiko.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.modules.impl.Visuals.ShaderHands;
import rtx.vexsiko.api.modules.impl.Visuals.SwingAnimation;
import rtx.vexsiko.api.modules.impl.Visuals.ViewModel;
import rtx.vexsiko.utils.render.post.handsflame.HandsItemHitboxTracker;
import rtx.vexsiko.utils.render.post.itemoutline.ItemOutlineRenderer;
import rtx.vexsiko.utils.render.post.shaderhands.ShaderHandsRenderer;

@Mixin(HeldItemRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Unique
    private MatrixStack vexsiko_customSwingMatrices;
    @Unique
    private Hand vexsiko_customSwingHand;
    @Unique
    private float vexsiko_customSwingProgress;
    @Unique
    private Hand vexsiko_outlineHand;
    @Unique
    private float vexsiko_mainCx;
    @Unique
    private float vexsiko_mainCy;
    @Unique
    private float vexsiko_mainCz;
    @Unique
    private float vexsiko_offCx;
    @Unique
    private float vexsiko_offCy;
    @Unique
    private float vexsiko_offCz;
    @Unique
    private boolean vexsiko_mainCenterSet;
    @Unique
    private boolean vexsiko_offCenterSet;

    @WrapOperation(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"), require = 0)
    private void vexsiko_baseSwingAnimation(HeldItemRenderer instance, MatrixStack matrices, Arm arm, float equipProgress, Operation<Void> original, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true) Hand hand, @Local(argsOnly = true, ordinal = 2) float swingProgress) {
        ViewModel.apply(matrices, hand);
        if (player.isUsingItem() && player.getActiveHand() == hand) {
            float eq = ViewModel.suppressEatAnimation() ? 0.0f : equipProgress;
            original.call(instance, matrices, arm, eq);
            ViewModel.applyScale(matrices, hand);
            this.vexsiko_applyInPlaceEat(matrices, hand, arm, player);
            return;
        }
        if (SwingAnimation.applyAnimation(matrices, hand, swingProgress)) {
            ViewModel.applyScale(matrices, hand);
            this.vexsiko_markCustomSwing(matrices, hand, swingProgress);
            return;
        }
        original.call(instance, matrices, arm, equipProgress);
        ViewModel.applyScale(matrices, hand);
    }

    @WrapOperation(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEatOrDrinkTransformation(Lnet/minecraft/client/util/math/MatrixStack;FLnet/minecraft/util/Arm;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)V"), require = 0)
    private void vexsiko_suppressEatTransform(HeldItemRenderer instance, MatrixStack poseStack, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, Operation<Void> original) {
        if (ViewModel.suppressEatAnimation()) {
            return;
        }
        original.call(instance, poseStack, tickDelta, arm, stack, player);
    }

    @Unique
    private void vexsiko_applyInPlaceEat(MatrixStack matrices, Hand hand, Arm arm, AbstractClientPlayerEntity player) {
        float cz;
        float cy;
        float cx;
        boolean main;
        if (!ViewModel.suppressEatAnimation()) {
            return;
        }
        ItemStack stack = player.getStackInHand(hand);
        UseAction anim = stack.getUseAction();
        if (anim != UseAction.EAT && anim != UseAction.DRINK) {
            return;
        }
        main = hand == Hand.MAIN_HAND;
        if (main && this.vexsiko_mainCenterSet) {
            cx = this.vexsiko_mainCx;
            cy = this.vexsiko_mainCy;
            cz = this.vexsiko_mainCz;
        } else if (!main && this.vexsiko_offCenterSet) {
            cx = this.vexsiko_offCx;
            cy = this.vexsiko_offCy;
            cz = this.vexsiko_offCz;
        } else {
            int s = arm == Arm.RIGHT ? 1 : -1;
            cx = 0.070625f * (float)s;
            cy = 0.2f;
            cz = 0.070625f;
        }
        float ft = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        float g = (float)player.getItemUseTimeLeft() - ft + 1.0f;
        float h = g / (float)stack.getMaxUseTime((LivingEntity)player);
        if (h < 0.8f) {
            float bob = MathHelper.abs((float)(MathHelper.cos((double)(g / 4.0f * (float)Math.PI)) * 0.1f));
            matrices.translate(0.0f, bob, 0.0f);
        }
        float i = 1.0f - (float)Math.pow(h, 27.0);
        int j = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)(-j) * i * 0.2f, i * -0.05f, 0.0f);
        matrices.translate(cx, cy, cz);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)j * i * 90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(i * 10.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)j * i * 30.0f));
        matrices.translate(-cx, -cy, -cz);
    }

    @WrapOperation(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V"), require = 0)
    private void vexsiko_swingAnimation(HeldItemRenderer instance, float swingProgress, MatrixStack matrices, int armX, Arm arm, Operation<Void> original, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true) Hand hand) {
        if (player.isUsingItem() && player.getActiveHand() == hand) {
            original.call(instance, swingProgress, matrices, armX, arm);
            return;
        }
        if (this.vexsiko_consumeCustomSwing(matrices, hand, swingProgress)) {
            return;
        }
        if (!SwingAnimation.applyAnimation(matrices, hand, swingProgress)) {
            original.call(instance, swingProgress, matrices, armX, arm);
        }
    }

    @Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("TAIL"), require = 0)
    private void vexsiko_clearSwingAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue nodeCollector, int light, CallbackInfo ci) {
        this.vexsiko_clearCustomSwing();
        this.vexsiko_outlineHand = null;
    }

    @Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("HEAD"), require = 0)
    private void vexsiko_captureOutlineHand(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue nodeCollector, int light, CallbackInfo ci) {
        this.vexsiko_outlineHand = hand;
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("HEAD"), require = 0)
    private void vexsiko_captureShaderHandsScene(float tickProgress, MatrixStack matrices, OrderedRenderCommandQueue collector, ClientPlayerEntity player, int light, CallbackInfo ci) {
        ViewModel.beginHandFrame();
        if (ShaderHands.isOldModeActive() || ViewModel.wantsHandMask()) {
            ShaderHandsRenderer.captureScene();
        }
    }

    @WrapOperation(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/RenderDispatcher;render()V"), require = 0)
    private void vexsiko_beginShaderHandsCapture(RenderDispatcher instance, Operation<Void> original) {
        if ((ShaderHands.isOldModeActive() || ViewModel.wantsHandMask()) && ShaderHandsRenderer.beginHandCapture()) {
            try {
                original.call(instance);
            }
            catch (Throwable t) {
                ShaderHandsRenderer.endHandCapture();
                throw t;
            }
        } else {
            original.call(instance);
        }
    }

    @WrapOperation(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"), require = 0)
    private void vexsiko_captureShaderHands(VertexConsumerProvider.Immediate instance, Operation<Void> original) {
        original.call(instance);
        if (ShaderHandsRenderer.isCapturing()) {
            ShaderHandsRenderer.endHandCapture();
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At("TAIL"), require = 0)
    private void vexsiko_drawItemOutline(float tickProgress, MatrixStack matrices, OrderedRenderCommandQueue collector, ClientPlayerEntity player, int light, CallbackInfo ci) {
        if (ShaderHands.isOldModeActive()) {
            ShaderHands.composite();
        } else if (ViewModel.wantsHandMask()) {
            ShaderHandsRenderer.compositePlain();
        }
        if (ViewModel.wantsHandMask() || ShaderHands.isOldModeActive()) {
            ShaderHandsRenderer.updateHandMask();
        }
        ItemOutlineRenderer.run();
    }

    @WrapOperation(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V"), require = 0)
    private void vexsiko_stampOutline(ItemRenderState state, MatrixStack pose, OrderedRenderCommandQueue collector, int light, int overlay, int outlineColor, Operation<Void> original) {
        ClientPlayerEntity player;
        int color = outlineColor;
        if (this.vexsiko_outlineHand != null && ViewModel.outlineAlpha(this.vexsiko_outlineHand) > 0.001f) {
            color = ItemOutlineRenderer.outlineColor();
        }
        if (this.vexsiko_outlineHand != null && ViewModel.suppressEatAnimation()) {
            Vec3d c = state.getModelBoundingBox().getCenter();
            if (this.vexsiko_outlineHand == Hand.MAIN_HAND) {
                this.vexsiko_mainCx = (float)c.x;
                this.vexsiko_mainCy = (float)c.y;
                this.vexsiko_mainCz = (float)c.z;
                this.vexsiko_mainCenterSet = true;
            } else {
                this.vexsiko_offCx = (float)c.x;
                this.vexsiko_offCy = (float)c.y;
                this.vexsiko_offCz = (float)c.z;
                this.vexsiko_offCenterSet = true;
            }
        }
        if (ShaderHands.isNewModeActive() && this.vexsiko_outlineHand != null && (player = MinecraftClient.getInstance().player) != null) {
            HandsItemHitboxTracker.capture(ItemInHandRendererMixin.vexsiko_handDisplayContext(player, this.vexsiko_outlineHand), pose, state);
        }
        original.call(state, pose, collector, light, overlay, color);
    }

    @Unique
    private static ItemDisplayContext vexsiko_handDisplayContext(ClientPlayerEntity player, Hand hand) {
        boolean right = hand == Hand.MAIN_HAND == (player.getMainArm() == Arm.RIGHT);
        return right ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    }

    @Unique
    private void vexsiko_markCustomSwing(MatrixStack matrices, Hand hand, float swingProgress) {
        this.vexsiko_customSwingMatrices = matrices;
        this.vexsiko_customSwingHand = hand;
        this.vexsiko_customSwingProgress = swingProgress;
    }

    @Unique
    private boolean vexsiko_consumeCustomSwing(MatrixStack matrices, Hand hand, float swingProgress) {
        boolean matches = this.vexsiko_customSwingMatrices == matrices && this.vexsiko_customSwingHand == hand && Float.compare(this.vexsiko_customSwingProgress, swingProgress) == 0;
        if (matches) {
            this.vexsiko_clearCustomSwing();
        }
        return matches;
    }

    @Unique
    private void vexsiko_clearCustomSwing() {
        this.vexsiko_customSwingMatrices = null;
        this.vexsiko_customSwingHand = null;
        this.vexsiko_customSwingProgress = 0.0f;
    }
}
