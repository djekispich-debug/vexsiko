package rtx.vexsiko.utils.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;

public final class TabBadgeRenderer {
    private static final Identifier BADGE = Identifier.of("vexsiko", "textures/bage_logo.png");
    private TabBadgeRenderer() {}

    public static int extraWidth() {
        return 11;
    }

    public static void drawBadge(DrawContext graphics, TextRenderer font, int x, int y) {
        // draw 8x8 badge slightly centered vertically on text line (font height ~8)
        int bx = x + 1;
        int by = y - 1;
        // shadow/glow behind badge
        graphics.fill(bx - 1, by - 1, bx + 9, by + 9, 0x22000000);
        try {
            graphics.drawTexture(RenderPipelines.GUI_TEXTURED, BADGE, bx, by, 0, 0, 8, 8, 8, 8);
        } catch (Exception e) {
            // fallback: small colored rect if texture missing
            graphics.fill(bx, by, bx + 8, by + 8, 0xFFAA88FF);
        }
    }
}
