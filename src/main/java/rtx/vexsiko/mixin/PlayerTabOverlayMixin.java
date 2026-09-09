package rtx.vexsiko.mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.modules.impl.Utils.Globals;
import rtx.vexsiko.api.modules.impl.Visuals.BetterMinecraft;
import rtx.vexsiko.utils.animations.TabListAnimationAccess;
import rtx.vexsiko.utils.net.ClientPresence;
import rtx.vexsiko.utils.render.TabBadgeRenderer;

@Mixin(PlayerListHud.class)
public abstract class PlayerTabOverlayMixin
implements TabListAnimationAccess {
    @Unique
    private static final long VEXSIKO_TAB_ANIMATION_MS = 300L;
    @Unique
    private long vexsiko_animationStart;
    @Unique
    private long vexsiko_animationDuration;
    @Unique
    private float vexsiko_animationFrom;
    @Unique
    private float vexsiko_animationTarget;
    @Unique
    private boolean vexsiko_visible;
    @Unique
    private boolean vexsiko_scaledForAnimation;

    @Inject(method="setVisible", at={@At(value="HEAD")}, require = 0)
    private void vexsiko_trackVisibility(boolean visible, CallbackInfo ci) {
        if (visible != this.vexsiko_visible) {
            float current = this.vexsiko_currentScale();
            this.vexsiko_visible = visible;
            this.vexsiko_animationFrom = current;
            this.vexsiko_animationTarget = visible ? 1.0f : 0.0f;
            this.vexsiko_animationStart = System.currentTimeMillis();
            this.vexsiko_animationDuration = Math.max(1L, (long)Math.round(300.0f * Math.abs(this.vexsiko_animationTarget - this.vexsiko_animationFrom)));
        }
    }

    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    private void vexsiko_beginTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        this.vexsiko_scaledForAnimation = BetterMinecraft.tabAnimationEnabled();
        if (!this.vexsiko_scaledForAnimation) {
            return;
        }
        float scale = Math.max(0.01f, Math.min(1.15f, this.vexsiko_currentScale()));
        graphics.getMatrices().pushMatrix();
        graphics.getMatrices().translate((float)windowWidth / 2.0f, 10.0f);
        graphics.getMatrices().scale(scale, scale);
        graphics.getMatrices().translate((float)(-windowWidth) / 2.0f, -10.0f);
    }

    @Inject(method="render", at={@At(value="RETURN")}, require = 0)
    private void vexsiko_endTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (this.vexsiko_scaledForAnimation) {
            graphics.getMatrices().popMatrix();
            this.vexsiko_scaledForAnimation = false;
        }
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/font/TextRenderer;method_27525(Lnet/minecraft/class_5348;)I", ordinal=0)}, require = 0)
    private int vexsiko_includeBadgeInFullNameWidth(TextRenderer font, StringVisitable fullServerName, Operation<Integer> original, @Local PlayerListEntry playerInfo) {
        int fullServerNameWidth = (Integer)original.call(new Object[]{font, fullServerName});
        if (!Globals.tabBadge()) {
            return fullServerNameWidth;
        }
        return fullServerNameWidth + TabBadgeRenderer.extraWidth();
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V")}, require = 0)
    private void vexsiko_drawFullTabName(DrawContext graphics, TextRenderer font, Text fullServerName, int x, int y, int color, Operation<Void> original, @Local PlayerListEntry playerInfo) {
        if (!Globals.tabBadge()) {
            original.call(new Object[]{graphics, font, fullServerName, x, y, color});
            return;
        }
        original.call(new Object[]{graphics, font, fullServerName, x + TabBadgeRenderer.extraWidth(), y, color});
        if (PlayerTabOverlayMixin.vexsiko_hasTabBadge(playerInfo)) {
            TabBadgeRenderer.drawBadge((DrawContext)graphics, (TextRenderer)font, (int)x, (int)y);
        }
    }

    @Unique
    private static boolean vexsiko_hasTabBadge(PlayerListEntry playerInfo) {
        return playerInfo != null && Globals.tabBadge() && ClientPresence.INSTANCE.isVexSikoUser(playerInfo.getProfile().name());
    }

    @Override
    public boolean vexsiko_shouldRenderClosingTab() {
        return BetterMinecraft.tabAnimationEnabled() && !this.vexsiko_visible && this.vexsiko_currentScale() > 0.01f;
    }

    @Unique
    private float vexsiko_currentScale() {
        long duration = Math.max(1L, this.vexsiko_animationDuration);
        float progress = Math.min(1.0f, (float)(System.currentTimeMillis() - this.vexsiko_animationStart) / (float)duration);
        float eased = this.vexsiko_animationTarget > this.vexsiko_animationFrom ? this.vexsiko_easeOutBack(progress) : this.vexsiko_easeInBack(progress);
        return this.vexsiko_animationFrom + (this.vexsiko_animationTarget - this.vexsiko_animationFrom) * eased;
    }

    @Unique
    private float vexsiko_easeOutBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float t = value - 1.0f;
        return 1.0f + c3 * t * t * t + c1 * t * t;
    }

    @Unique
    private float vexsiko_easeInBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return c3 * value * value * value - c1 * value * value;
    }
}

