package rtx.vexsiko.mixin.shulkerview;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.vexsiko.api.mods.shulkerview.hook.ShulkerPreviewGuiGraphics;

@Mixin(net.minecraft.client.gui.DrawContext.class)

public abstract class GuiGraphicsExtractorMixin
implements ShulkerPreviewGuiGraphics {
    @Unique
    private int vexsiko_shulkerPreviewMouseX = Integer.MIN_VALUE;
    @Unique
    private int vexsiko_shulkerPreviewMouseY = Integer.MIN_VALUE;

    @Override
    public int vexsiko_getMouseX() {
        return this.vexsiko_shulkerPreviewMouseX;
    }

    @Override
    public int vexsiko_getMouseY() {
        return this.vexsiko_shulkerPreviewMouseY;
    }

    @Override
    public void vexsiko_setMouse(int mouseX, int mouseY) {
        this.vexsiko_shulkerPreviewMouseX = mouseX;
        this.vexsiko_shulkerPreviewMouseY = mouseY;
    }
}

