package rtx.vexsiko.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.modules.impl.Utils.guishare.RemoteGuiWorld;
import rtx.vexsiko.api.ui.UI;
import rtx.vexsiko.mixin.accessor.GuiRendererDrawAccessor;
import rtx.vexsiko.utils.render.post.guilayerblur.GuiCapture;
import rtx.vexsiko.utils.render.post.guilayerblur.GuiLayerBlurRenderer;
import rtx.vexsiko.utils.render.post.guimotionblur.GuiMotionBlurRenderer;
import rtx.vexsiko.utils.render.render2d.ClientSplits;
import rtx.vexsiko.utils.render.render2d.arc.ArcRenderer;
import rtx.vexsiko.utils.render.render2d.blur.BlurFramebuffer;
import rtx.vexsiko.utils.render.render2d.circle.CircleRenderer;
import rtx.vexsiko.utils.render.render2d.glass.GlassRenderer;
import rtx.vexsiko.utils.render.render2d.glow.GlowRenderer;
import rtx.vexsiko.utils.render.render2d.image.ImageRenderer;
import rtx.vexsiko.utils.render.render2d.line.LineRenderer;
import rtx.vexsiko.utils.render.render2d.outline.outline360.Outline360Renderer;
import rtx.vexsiko.utils.render.render2d.outline.outlinedefault.DefaultOutlineRenderer;
import rtx.vexsiko.utils.render.render2d.outline.outlineglass.GlassOutlineRenderer;
import rtx.vexsiko.utils.render.render2d.picker.PickerRenderer;
import rtx.vexsiko.utils.render.render2d.radialglass.RadialGlassRenderer;
import rtx.vexsiko.utils.render.render2d.rectangle.rectdefault.DefaultRectangleRenderer;
import rtx.vexsiko.utils.render.render2d.rectangle.recthalficon.HalfIconRectangleRenderer;
import rtx.vexsiko.utils.render.render2d.rectangle.recthalftone.HalftoneRectangleRenderer;
import rtx.vexsiko.utils.render.render2d.ripple.RippleRenderer;
import rtx.vexsiko.utils.render.render2d.sectormask.SectorMaskRenderer;
import rtx.vexsiko.utils.render.render2d.shape.ShapeRenderer;
import rtx.vexsiko.utils.render.render2d.shimmer.ShimmerRenderer;
import rtx.vexsiko.utils.render.render2d.zippy.ZippyRenderer;
import rtx.vexsiko.utils.render.renderitem.RenderItem;

@Mixin(net.minecraft.client.gui.render.GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Shadow
    @Final
    private List<?> draws;
    private RenderPass vexsiko_currentRenderPass;
    private boolean vexsiko_blurDrawActive;
    private boolean vexsiko_glassDrawActive;
    private boolean vexsiko_shapeDrawActive;
    private boolean vexsiko_glowDrawActive;
    private boolean vexsiko_glassOutlineDrawActive;
    private boolean vexsiko_circleDrawActive;
    private boolean vexsiko_arcDrawActive;
    private boolean vexsiko_radialGlassDrawActive;
    private boolean vexsiko_sectorMaskDrawActive;
    private boolean vexsiko_pickerDrawActive;
    private boolean vexsiko_rectangleDrawActive;
    private boolean vexsiko_halfIconRectangleDrawActive;
    private boolean vexsiko_halftoneRectangleDrawActive;
    private boolean vexsiko_zippyDrawActive;
    private boolean vexsiko_outlineDrawActive;
    private boolean vexsiko_outline360DrawActive;
    private boolean vexsiko_imageDrawActive;
    private boolean vexsiko_lineDrawActive;
    private boolean vexsiko_itemDrawActive;
    private boolean vexsiko_rippleDrawActive;
    private boolean vexsiko_shimmerDrawActive;

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"), require = 0)
    private void vexsiko_beginBlurFrame(CallbackInfo ci) {
        BlurFramebuffer.getInstance().beginGuiFrame();
        GlassRenderer.getInstance().beginGuiFrame();
        ShapeRenderer.getInstance().beginGuiFrame();
        GlowRenderer.getInstance().beginGuiFrame();
        GlassOutlineRenderer.getInstance().beginGuiFrame();
        CircleRenderer.getInstance().beginGuiFrame();
        ArcRenderer.getInstance().beginGuiFrame();
        RadialGlassRenderer.getInstance().beginGuiFrame();
        SectorMaskRenderer.getInstance().beginGuiFrame();
        PickerRenderer.getInstance().beginGuiFrame();
        DefaultRectangleRenderer.getInstance().beginGuiFrame();
        HalfIconRectangleRenderer.getInstance().beginGuiFrame();
        HalftoneRectangleRenderer.getInstance().beginGuiFrame();
        ZippyRenderer.getInstance().beginGuiFrame();
        DefaultOutlineRenderer.getInstance().beginGuiFrame();
        Outline360Renderer.getInstance().beginGuiFrame();
        ImageRenderer.getInstance().beginGuiFrame();
        LineRenderer.getInstance().beginGuiFrame();
        RippleRenderer.getInstance().beginGuiFrame();
        ShimmerRenderer.getInstance().beginGuiFrame();
        RenderItem.beginGuiFrame();
    }

    @Inject(method = "prepare()V", at = @At("HEAD"), require = 0)
    private void vexsiko_preparePendingBlurResources(CallbackInfo ci) {
        ClientSplits.update();
        BlurFramebuffer.getInstance().preparePending();
        GlowRenderer.getInstance().preparePending();
    }

    @Inject(method = "prepare()V", at = @At("RETURN"), require = 0)
    private void vexsiko_prepareRenderUniforms(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareBuffers();
        GlassRenderer.getInstance().prepareBuffers();
        ShapeRenderer.getInstance().prepareBuffers();
        GlowRenderer.getInstance().prepareBuffers();
        GlassOutlineRenderer.getInstance().prepareBuffers();
        CircleRenderer.getInstance().prepareBuffers();
        ArcRenderer.getInstance().prepareBuffers();
        RadialGlassRenderer.getInstance().prepareBuffers();
        SectorMaskRenderer.getInstance().prepareBuffers();
        PickerRenderer.getInstance().prepareBuffers();
        DefaultRectangleRenderer.getInstance().prepareBuffers();
        HalfIconRectangleRenderer.getInstance().prepareBuffers();
        HalftoneRectangleRenderer.getInstance().prepareBuffers();
        ZippyRenderer.getInstance().prepareBuffers();
        DefaultOutlineRenderer.getInstance().prepareBuffers();
        Outline360Renderer.getInstance().prepareBuffers();
        ImageRenderer.getInstance().prepareBuffers();
        LineRenderer.getInstance().prepareBuffers();
        RippleRenderer.getInstance().prepareBuffers();
        ShimmerRenderer.getInstance().prepareBuffers();
        RenderItem.prepareBuffers();
    }

    @Inject(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"), require = 0)
    private void vexsiko_prepareBlurCapture(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareGuiDraw();
        GuiLayerBlurRenderer.beginCapture(GuiCapture.active(), RemoteGuiWorld.captureRequested());
    }

    @WrapOperation(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V"), require = 0)
    private void vexsiko_routePanelRange(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        Framebuffer remoteTarget = GuiLayerBlurRenderer.remoteCaptureTarget();
        if (remoteTarget != null) {
            while (cursor < to) {
                if (GuiLayerBlurRenderer.isRemoteRouting()) {
                    int end = this.vexsiko_findRemoteMark(cursor, to, false);
                    if (end < cursor) {
                        this.vexsiko_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, to, original);
                        return;
                    }
                    if (cursor < end) {
                        this.vexsiko_drawCaptured(instance, label, remoteTarget, fog, transforms, indices, indexType, cursor, end, original);
                    }
                    GuiLayerBlurRenderer.setRemoteRouting(false);
                    cursor = end + 1;
                    continue;
                }
                int begin = this.vexsiko_findRemoteMark(cursor, to, true);
                if (begin < cursor) break;
                if (cursor < begin) {
                    this.vexsiko_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, begin, original);
                }
                GuiLayerBlurRenderer.setRemoteRouting(true);
                cursor = begin + 1;
            }
            if (cursor >= to) {
                return;
            }
        }
        this.vexsiko_routeLocal(instance, label, target, fog, transforms, indices, indexType, cursor, to, original);
    }

    @Unique
    private void vexsiko_routeLocal(GuiRenderer instance, Supplier<String> label, Framebuffer target, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        if (from >= to) {
            return;
        }
        Framebuffer captureTarget = GuiLayerBlurRenderer.captureTarget();
        if (captureTarget == null) {
            int popupBoundary = UI.popupLayerCapturePending() ? this.vexsiko_findBoundary(from, to, true) : -1;
            if (popupBoundary < from) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, to});
                return;
            }
            if (from < popupBoundary) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, popupBoundary});
            }
            if (UI.consumePopupLayerCapture()) {
                GuiMotionBlurRenderer.captureBackground(0.0f);
            }
            if (popupBoundary + 1 < to) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, popupBoundary + 1, to});
            }
            return;
        }
        int boundary = this.vexsiko_findBoundary(from, to, false);
        if (boundary < from) {
            if (GuiCapture.emitPanelBoundary()) {
                original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, from, to});
            } else {
                this.vexsiko_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, to, original);
            }
            return;
        }
        if (from < boundary) {
            this.vexsiko_drawCaptured(instance, label, captureTarget, fog, transforms, indices, indexType, from, boundary, original);
        }
        if (boundary + 1 < to) {
            original.call(new Object[]{instance, label, target, fog, transforms, indices, indexType, boundary + 1, to});
        }
    }

    @Unique
    private int vexsiko_findRemoteMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.vexsiko_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteBegin(pipeline) : GuiLayerBlurRenderer.isRemoteEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private void vexsiko_drawCaptured(GuiRenderer instance, Supplier<String> label, Framebuffer captureTarget, GpuBufferSlice fog, GpuBufferSlice transforms, GpuBuffer indices, VertexFormat.IndexType indexType, int from, int to, Operation<Void> original) {
        int cursor = from;
        boolean remote = captureTarget == GuiLayerBlurRenderer.remoteCaptureTarget();
        int popupBoundary;
        int cardBegin;
        int cardEnd;
        int boundary;
        while (cursor < to && (boundary = GuiRendererMixin.vexsiko_firstBoundary(popupBoundary = this.vexsiko_findBoundary(cursor, to, true), cardBegin = remote ? this.vexsiko_findRemoteCardMark(cursor, to, true) : -1, cardEnd = remote ? this.vexsiko_findRemoteCardMark(cursor, to, false) : -1)) >= cursor) {
            if (cursor < boundary) {
                original.call(new Object[]{instance, label, captureTarget, fog, transforms, indices, indexType, cursor, boundary});
            }
            if (boundary == cardBegin) {
                RemoteGuiWorld.beginCardBlurDraw(captureTarget);
            } else if (boundary == cardEnd) {
                RemoteGuiWorld.endCardBlurDraw(captureTarget);
            } else {
                BlurFramebuffer.getInstance().recaptureWorldBackdrop();
            }
            cursor = boundary + 1;
        }
        if (cursor < to) {
            original.call(new Object[]{instance, label, captureTarget, fog, transforms, indices, indexType, cursor, to});
        }
    }

    @Unique
    private int vexsiko_findRemoteCardMark(int from, int to, boolean begin) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.vexsiko_getPipeline();
            if (!(begin ? GuiLayerBlurRenderer.isRemoteCardBegin(pipeline) : GuiLayerBlurRenderer.isRemoteCardEnd(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Unique
    private static int vexsiko_firstBoundary(int first, int second, int third) {
        int result = -1;
        if (first >= 0) {
            result = first;
        }
        if (second >= 0 && (result < 0 || second < result)) {
            result = second;
        }
        if (third >= 0 && (result < 0 || third < result)) {
            result = third;
        }
        return result;
    }

    @Unique
    private int vexsiko_findBoundary(int from, int to, boolean popup) {
        int end = Math.min(to, this.draws.size());
        for (int i = Math.max(0, from); i < end; ++i) {
            Object draw = this.draws.get(i);
            if (!(draw instanceof GuiRendererDrawAccessor)) continue;
            GuiRendererDrawAccessor accessor = (GuiRendererDrawAccessor)draw;
            RenderPipeline pipeline = accessor.vexsiko_getPipeline();
            if (!(popup ? GuiLayerBlurRenderer.isPopupBoundary(pipeline) : GuiLayerBlurRenderer.isPanelBoundary(pipeline))) continue;
            return i;
        }
        return -1;
    }

    @Redirect(method = "renderPreparedDraws(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderBlur()V"), require = 0)
    private void vexsiko_cardLayerMidCapture(GameRenderer gameRenderer) {
        if (GuiCapture.active()) {
            GuiLayerBlurRenderer.markPanelRange();
            UI.consumePanelSplitMark();
            UI.consumeCardStratumMark();
            UI.consumePopupStratumMark();
            UI.consumeVanillaBlurRequest();
            return;
        }
        if (UI.consumePanelSplitMark()) {
            UI.applyMainCompositeAtSplit();
            if (UI.consumeVanillaBlurRequest() && !UI.isOpen()) {
                gameRenderer.renderBlur();
            }
            return;
        }
        if (UI.consumePopupStratumMark()) {
            BlurFramebuffer.getInstance().recaptureBackdrop();
            if (UI.consumePopupBlurCapture()) {
                GuiMotionBlurRenderer.captureBackground(0.0f);
            }
            return;
        }
        if (UI.consumeCardStratumMark()) {
            GuiMotionBlurRenderer.captureBackground(0.0f);
            return;
        }
        if (UI.isOpen()) {
            return;
        }
        gameRenderer.renderBlur();
    }

    @Redirect(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"), require = 0)
    private void vexsiko_trackPipeline(RenderPass renderPass, RenderPipeline pipeline) {
        this.vexsiko_currentRenderPass = renderPass;
        this.vexsiko_blurDrawActive = BlurFramebuffer.getInstance().isBlurPipeline(pipeline);
        this.vexsiko_glassDrawActive = GlassRenderer.getInstance().isGlassPipeline(pipeline);
        this.vexsiko_shapeDrawActive = ShapeRenderer.getInstance().isShapePipeline(pipeline);
        this.vexsiko_glowDrawActive = GlowRenderer.getInstance().isGlowPipeline(pipeline);
        this.vexsiko_glassOutlineDrawActive = GlassOutlineRenderer.getInstance().isGlassOutlinePipeline(pipeline);
        this.vexsiko_circleDrawActive = CircleRenderer.getInstance().isCirclePipeline(pipeline);
        this.vexsiko_arcDrawActive = ArcRenderer.getInstance().isArcPipeline(pipeline);
        this.vexsiko_radialGlassDrawActive = RadialGlassRenderer.getInstance().isRadialGlassPipeline(pipeline);
        this.vexsiko_sectorMaskDrawActive = SectorMaskRenderer.getInstance().isSectorMaskPipeline(pipeline);
        this.vexsiko_pickerDrawActive = PickerRenderer.getInstance().isPickerPipeline(pipeline);
        this.vexsiko_rectangleDrawActive = DefaultRectangleRenderer.getInstance().isRectanglePipeline(pipeline);
        this.vexsiko_halfIconRectangleDrawActive = HalfIconRectangleRenderer.getInstance().isHalfIconRectanglePipeline(pipeline);
        this.vexsiko_halftoneRectangleDrawActive = HalftoneRectangleRenderer.getInstance().isHalftoneRectanglePipeline(pipeline);
        this.vexsiko_zippyDrawActive = ZippyRenderer.getInstance().isZippyPipeline(pipeline);
        this.vexsiko_outlineDrawActive = DefaultOutlineRenderer.getInstance().isOutlinePipeline(pipeline);
        this.vexsiko_outline360DrawActive = Outline360Renderer.getInstance().isOutline360Pipeline(pipeline);
        this.vexsiko_imageDrawActive = ImageRenderer.getInstance().isImagePipeline(pipeline);
        this.vexsiko_lineDrawActive = LineRenderer.getInstance().isLinePipeline(pipeline);
        this.vexsiko_rippleDrawActive = RippleRenderer.getInstance().isRipplePipeline(pipeline);
        this.vexsiko_shimmerDrawActive = ShimmerRenderer.getInstance().isShimmerPipeline(pipeline);
        this.vexsiko_itemDrawActive = RenderItem.isItemPipeline(pipeline);
        renderPass.setPipeline(pipeline);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V", shift = At.Shift.BEFORE), require = 0)
    private void vexsiko_bindBlurParams(CallbackInfo ci) {
        if (this.vexsiko_blurDrawActive && this.vexsiko_currentRenderPass != null) {
            BlurFramebuffer.getInstance().bindBlurParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_glassDrawActive && this.vexsiko_currentRenderPass != null) {
            GlassRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_shapeDrawActive && this.vexsiko_currentRenderPass != null) {
            ShapeRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_glowDrawActive && this.vexsiko_currentRenderPass != null) {
            GlowRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_glassOutlineDrawActive && this.vexsiko_currentRenderPass != null) {
            GlassOutlineRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_circleDrawActive && this.vexsiko_currentRenderPass != null) {
            CircleRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_arcDrawActive && this.vexsiko_currentRenderPass != null) {
            ArcRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_radialGlassDrawActive && this.vexsiko_currentRenderPass != null) {
            RadialGlassRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_sectorMaskDrawActive && this.vexsiko_currentRenderPass != null) {
            SectorMaskRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_pickerDrawActive && this.vexsiko_currentRenderPass != null) {
            PickerRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_rectangleDrawActive && this.vexsiko_currentRenderPass != null) {
            DefaultRectangleRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_halfIconRectangleDrawActive && this.vexsiko_currentRenderPass != null) {
            HalfIconRectangleRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_halftoneRectangleDrawActive && this.vexsiko_currentRenderPass != null) {
            HalftoneRectangleRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_zippyDrawActive && this.vexsiko_currentRenderPass != null) {
            ZippyRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_outlineDrawActive && this.vexsiko_currentRenderPass != null) {
            DefaultOutlineRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_outline360DrawActive && this.vexsiko_currentRenderPass != null) {
            Outline360Renderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_imageDrawActive && this.vexsiko_currentRenderPass != null) {
            ImageRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_lineDrawActive && this.vexsiko_currentRenderPass != null) {
            LineRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_rippleDrawActive && this.vexsiko_currentRenderPass != null) {
            RippleRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_shimmerDrawActive && this.vexsiko_currentRenderPass != null) {
            ShimmerRenderer.getInstance().bindParams(this.vexsiko_currentRenderPass);
        }
        if (this.vexsiko_itemDrawActive && this.vexsiko_currentRenderPass != null) {
            RenderItem.bindParams(this.vexsiko_currentRenderPass);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At("RETURN"), require = 0)
    private void vexsiko_clearTrackedPipeline(CallbackInfo ci) {
        this.vexsiko_currentRenderPass = null;
        this.vexsiko_blurDrawActive = false;
        this.vexsiko_glassDrawActive = false;
        this.vexsiko_shapeDrawActive = false;
        this.vexsiko_glowDrawActive = false;
        this.vexsiko_glassOutlineDrawActive = false;
        this.vexsiko_circleDrawActive = false;
        this.vexsiko_arcDrawActive = false;
        this.vexsiko_radialGlassDrawActive = false;
        this.vexsiko_sectorMaskDrawActive = false;
        this.vexsiko_pickerDrawActive = false;
        this.vexsiko_rectangleDrawActive = false;
        this.vexsiko_halfIconRectangleDrawActive = false;
        this.vexsiko_halftoneRectangleDrawActive = false;
        this.vexsiko_zippyDrawActive = false;
        this.vexsiko_outlineDrawActive = false;
        this.vexsiko_outline360DrawActive = false;
        this.vexsiko_imageDrawActive = false;
        this.vexsiko_lineDrawActive = false;
        this.vexsiko_itemDrawActive = false;
        this.vexsiko_rippleDrawActive = false;
        this.vexsiko_shimmerDrawActive = false;
    }
}
