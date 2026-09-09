package rtx.vexsiko;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rtx.vexsiko.api.mods.geckolib.GeckoLib;
import rtx.vexsiko.manager.Manager;

public class VexSiko
implements ModInitializer,
ClientModInitializer {
    public static final String MOD_ID = "vexsiko";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"vexsiko");

    public void onInitializeClient() {
        Manager.init();
    }

    public void onInitialize() {
        new GeckoLib().onInitialize();
    }
}
