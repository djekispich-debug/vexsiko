package rtx.vexsiko.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.WorldBorderRendering;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.vexsiko.api.modules.impl.Visuals.Ambience;
import rtx.vexsiko.utils.render.post.customsky.CustomSkyRenderer;

@Mixin(WorldBorderRendering.class)
public abstract class WorldBorderCustomSkyMixin {
    @Unique
    private static RenderPipeline vexsiko_noDepthPipeline;
    @Unique
    private static boolean vexsiko_pipelineFailed;

    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    private void vexsiko_applySkyBeforeBorder(CallbackInfo ci) {
        Ambience ambience = Ambience.getInstance();
        if (ambience != null && ambience.isCustomSkyActive()) {
            CustomSkyRenderer.applyPending(MinecraftClient.getInstance().getFramebuffer());
        }
    }

    @Redirect(method="render", at=@At(value="FIELD", target="Lnet/minecraft/client/gl/RenderPipelines;RENDERTYPE_WORLD_BORDER:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode=178), require = 0)
    private RenderPipeline vexsiko_borderPipeline() {
        Ambience ambience = Ambience.getInstance();
        if (ambience == null || !ambience.isCustomSkyActive()) {
            return RenderPipelines.RENDERTYPE_WORLD_BORDER;
        }
        RenderPipeline pipeline = WorldBorderCustomSkyMixin.vexsiko_noDepthWrite();
        return pipeline != null ? pipeline : RenderPipelines.RENDERTYPE_WORLD_BORDER;
    }

    @Unique
    private static RenderPipeline vexsiko_noDepthWrite() {
        if (vexsiko_pipelineFailed) {
            return null;
        }
        if (vexsiko_noDepthPipeline != null) {
            return vexsiko_noDepthPipeline;
        }
        try {
            RenderPipeline vanilla = RenderPipelines.RENDERTYPE_WORLD_BORDER;
            RenderPipeline.Builder builder = RenderPipeline.builder(new RenderPipeline.Snippet[0])
                    .withLocation(Identifier.of("vexsiko", "pipeline/world_border_no_depth"))
                    .withVertexShader(Identifier.of("vexsiko", "core/world_border_fade"))
                    .withFragmentShader(Identifier.of("vexsiko", "core/world_border_fade"))
                    .withVertexFormat(vanilla.getVertexFormat(), vanilla.getVertexFormatMode())
                    .withCull(vanilla.isCull())
                    .withDepthTestFunction(vanilla.getDepthTestFunction())
                    .withDepthBias(vanilla.getDepthBiasScaleFactor(), vanilla.getDepthBiasConstant())
                    .withDepthWrite(false);
            vanilla.getBlendFunction().ifPresent(builder::withBlend);
            for (String sampler : vanilla.getSamplers()) {
                builder.withSampler(sampler);
            }
            for (RenderPipeline.UniformDescription uniform : vanilla.getUniforms()) {
                if (uniform.textureFormat() != null) {
                    builder.withUniform(uniform.name(), uniform.type(), uniform.textureFormat());
                    continue;
                }
                builder.withUniform(uniform.name(), uniform.type());
            }
            vexsiko_noDepthPipeline = RenderPipelines.register(builder.build());
        }
        catch (Throwable throwable) {
            vexsiko_pipelineFailed = true;
        }
        return vexsiko_noDepthPipeline;
    }
}
