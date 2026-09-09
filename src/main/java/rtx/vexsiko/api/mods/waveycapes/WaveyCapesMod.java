package rtx.vexsiko.api.mods.waveycapes;
import net.fabricmc.api.ClientModInitializer;
import rtx.vexsiko.api.mods.waveycapes.WaveyCapesBase;

public class WaveyCapesMod
extends WaveyCapesBase
implements ClientModInitializer {
    public static final WaveyCapesMod INSTANCE = new WaveyCapesMod();

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void initSupportHooks() {
        super.initSupportHooks();
    }

    public void onInitializeClient() {
        this.init();
    }
}

