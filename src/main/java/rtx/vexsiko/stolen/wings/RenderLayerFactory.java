package rtx.vexsiko.stolen.wings;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class RenderLayerFactory {
    private RenderLayerFactory() {
    }

    public static RenderLayer create(String name, int expectedBufferSize, RenderPipeline pipeline) {
        return create(name, expectedBufferSize, pipeline, null);
    }

    public static RenderLayer create(String name, int expectedBufferSize, RenderPipeline pipeline, @Nullable Identifier texture) {
        RenderSetup.Builder builder = RenderSetup.builder(pipeline).expectedBufferSize(expectedBufferSize);
        if (texture != null) {
            builder.texture("Sampler0", texture);
        }
        return RenderLayer.of(name, builder.build());
    }
}
