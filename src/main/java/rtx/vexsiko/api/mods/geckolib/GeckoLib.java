package rtx.vexsiko.api.mods.geckolib;
import net.fabricmc.api.ModInitializer;
import rtx.vexsiko.api.mods.geckolib.GeckoLibConstants;
import rtx.vexsiko.api.mods.geckolib.service.GeckoLibNetworking;

public final class GeckoLib
implements ModInitializer {
    public void onInitialize() {
        GeckoLibConstants.init();
        GeckoLibNetworking.init();
    }
}

