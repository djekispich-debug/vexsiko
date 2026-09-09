package rtx.vexsiko.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.class)
public interface ItemStackRenderStateAccessor {
    @Accessor("layers")
    public ItemRenderState.LayerRenderState[] vexsiko_getLayers();

    @Accessor("layerCount")
    public int vexsiko_getActiveLayerCount();
}
