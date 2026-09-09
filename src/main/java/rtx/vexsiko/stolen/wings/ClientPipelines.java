package rtx.vexsiko.stolen.wings;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class ClientPipelines {

    private ClientPipelines() {
    }

    private static Identifier id(String path) {
        return Identifier.of("vexsiko", path);
    }

    // Copy of dile safe blend logic adapted to vexsiko compat
    private static BlendFunction worldBlend() {
        return RenderCompatibility.useSafeWorldEffects() ? BlendFunction.TRANSLUCENT : BlendFunction.LIGHTNING;
    }

    // ========== WINGS PIPELINES ==========
    // DepthTest differences are critical: Nothrough/Glow/Outline/Ribs without depth = NO_DEPTH_TEST (through walls), depth variants = LEQUAL

    public static final RenderPipeline WINGS_FILLED_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_filled"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                    .build()
    );

    public static final RenderLayer WINGS_FILLED = RenderLayerFactory.create("wings_filled", 8192, WINGS_FILLED_PIPELINE);

    public static final RenderPipeline WINGS_FILLED_NOTHROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_filled_nothrough"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                    .build()
    );

    public static final RenderLayer WINGS_FILLED_NOTHROUGH = RenderLayerFactory.create("wings_filled_nothrough", 8192, WINGS_FILLED_NOTHROUGH_PIPELINE);

    public static final RenderPipeline WINGS_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_glow"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                    .build()
    );

    public static final RenderLayer WINGS_GLOW = RenderLayerFactory.create("wings_glow", 8192, WINGS_GLOW_PIPELINE);

    public static final RenderPipeline WINGS_GLOW_DEPTH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_glow_depth"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                    .build()
    );

    public static final RenderLayer WINGS_GLOW_DEPTH = RenderLayerFactory.create("wings_glow_depth", 8192, WINGS_GLOW_DEPTH_PIPELINE);

    public static final RenderPipeline WINGS_OUTLINE_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_outline"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                    .build()
    );

    public static final RenderLayer WINGS_OUTLINE = RenderLayerFactory.create("wings_outline", 4096, WINGS_OUTLINE_PIPELINE);

    public static final RenderPipeline WINGS_OUTLINE_DEPTH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_outline_depth"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                    .build()
    );

    public static final RenderLayer WINGS_OUTLINE_DEPTH = RenderLayerFactory.create("wings_outline_depth", 4096, WINGS_OUTLINE_DEPTH_PIPELINE);

    public static final RenderPipeline WINGS_RIBS_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_ribs"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
                    .build()
    );

    public static final RenderLayer WINGS_RIBS = RenderLayerFactory.create("wings_ribs", 4096, WINGS_RIBS_PIPELINE);

    public static final RenderPipeline WINGS_RIBS_DEPTH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/wings_ribs_depth"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.LIGHTNING)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
                    .build()
    );

    public static final RenderLayer WINGS_RIBS_DEPTH = RenderLayerFactory.create("wings_ribs_depth", 4096, WINGS_RIBS_DEPTH_PIPELINE);

    // Keep other generic placeholders (optional, not used by wings but prevent linkage errors)
    public static final RenderPipeline CRYSTAL_FILLED_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/crystal_filled"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                    .build()
    );
    public static final RenderLayer CRYSTAL_FILLED = RenderLayerFactory.create("crystal_filled", 8192, CRYSTAL_FILLED_PIPELINE);

    public static final RenderPipeline CRYSTAL_GLOW_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/crystal_glow"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(worldBlend())
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                    .build()
    );
    public static final RenderLayer CRYSTAL_GLOW = RenderLayerFactory.create("crystal_glow", 4096, CRYSTAL_GLOW_PIPELINE);

    public static final RenderPipeline CHINA_HAT_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/china_hat"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(true)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_FAN)
                    .build()
    );
    public static final RenderLayer CHINA_HAT = RenderLayerFactory.create("china_hat", 8192, CHINA_HAT_PIPELINE);

    public static final RenderPipeline CHINA_HAT_OUTLINE_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                    .withLocation(id("pipeline/china_hat_outline"))
                    .withVertexShader("core/position_color")
                    .withFragmentShader("core/position_color")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(true)
                    .withCull(false)
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                    .build()
    );
    public static final RenderLayer CHINA_HAT_OUTLINE = RenderLayerFactory.create("china_hat_outline", 4096, CHINA_HAT_OUTLINE_PIPELINE);
}
