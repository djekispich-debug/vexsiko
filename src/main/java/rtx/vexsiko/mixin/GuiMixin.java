package rtx.vexsiko.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.events.EventBus;
import rtx.vexsiko.api.events.impl.render.DrawEvent;
import rtx.vexsiko.api.events.impl.render.HudRenderEvent;
import rtx.vexsiko.api.modules.ModuleManager;
import rtx.vexsiko.api.modules.impl.Interface.PotionsModule;
import rtx.vexsiko.api.modules.impl.Utils.guishare.RemoteGuiWorld;
import rtx.vexsiko.api.modules.impl.Visuals.BetterMinecraft;
import rtx.vexsiko.api.modules.impl.Visuals.emotions.EmotionWheelOverlay;
import rtx.vexsiko.api.ui.BaseScreen;
import rtx.vexsiko.api.ui.UI;
import rtx.vexsiko.utils.render.others.LoadingVisualGuard;
import rtx.vexsiko.utils.render.warmup.Render2DWarmup;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Unique
    private boolean vexsiko_debugEarly;

    @Shadow
    public abstract void renderDebugHud(DrawContext context);

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", shift = At.Shift.BEFORE), require = 0)
    private void vexsiko_renderHudUnderVanilla(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (LoadingVisualGuard.shouldSuppressHud(this.client)) {
            return;
        }
        EventBus.get().post(new HudRenderEvent(graphics, deltaTracker));
        graphics.createNewRootLayer();
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), require = 0)
    private void vexsiko_renderRemoteGuiPanels(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (LoadingVisualGuard.shouldSuppressHud(this.client)) {
            return;
        }
        RemoteGuiWorld.renderPanels(graphics);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("RETURN"), require = 0)
    private void vexsiko_renderTopLayer(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        if (LoadingVisualGuard.shouldSuppressHud(this.client)) {
            Render2DWarmup.runWarmupFrame(graphics);
            return;
        }
        EventBus.get().post(new DrawEvent(graphics, deltaTracker));
        UI.renderClosingPanelOverHud(graphics);
        BaseScreen.renderClosingOverlay(graphics);
        EmotionWheelOverlay.render(graphics);
        if (UI.isOpen() && this.client.getDebugHud() != null && this.client.getDebugHud().shouldShowDebugHud()) {
            this.vexsiko_debugEarly = true;
            this.renderDebugHud(graphics);
            this.vexsiko_debugEarly = false;
        }
    }

    @Inject(method = "renderDebugHud(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_moveDebugUnderClickGui(DrawContext graphics, CallbackInfo ci) {
        if (!this.vexsiko_debugEarly && UI.isOpen()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void vexsiko_hideVanillaEffects(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        PotionsModule potions = ModuleManager.get().get(PotionsModule.class);
        if (potions != null && potions.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V", at = @At("RETURN"), require = 0)
    private void vexsiko_renderSaturation(DrawContext graphics, PlayerEntity player, int top, int right, CallbackInfo ci) {
        BetterMinecraft.renderSaturation(graphics, player, top, right);
    }
}
