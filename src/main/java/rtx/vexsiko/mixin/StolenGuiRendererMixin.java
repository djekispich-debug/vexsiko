package rtx.vexsiko.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.gui.render.GuiRenderer;
import rtx.vexsiko.stolen.noise.Noise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class StolenGuiRendererMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("HEAD"))
    private void vexsiko$stolenNoiseCapture(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        Noise.captureBackdrop();
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", at = @At("TAIL"))
    private void vexsiko$stolenNoiseOverlay(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        Noise.renderMasks();
    }
}
