package rtx.vexsiko.mixin.accessor;

import java.util.Set;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPacketListenerAccessor {
    @Accessor("worldKeys")
    public void vexsiko_setLevels(Set<RegistryKey<World>> var1);

    @Accessor("worldProperties")
    public ClientWorld.Properties vexsiko_getLevelData();

    @Accessor("world")
    public void vexsiko_setLevel(ClientWorld var1);

    @Accessor("worldProperties")
    public void vexsiko_setLevelData(ClientWorld.Properties var1);

    @Accessor("chunkLoadDistance")
    public void vexsiko_setServerChunkRadius(int var1);

    @Accessor("simulationDistance")
    public void vexsiko_setServerSimulationDistance(int var1);

    @Accessor("simulationDistance")
    public int vexsiko_getServerSimulationDistance();

    @Accessor("chunkLoadDistance")
    public int vexsiko_getServerChunkRadius();
}
