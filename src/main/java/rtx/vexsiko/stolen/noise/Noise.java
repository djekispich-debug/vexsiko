package rtx.vexsiko.stolen.noise;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.modules.settings.impl.ModeSetting;
import rtx.vexsiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.vexsiko.api.modules.settings.impl.SliderSetting;
import rtx.vexsiko.mixin.accessor.HandledScreenAccessor;

/**
 * Ported from org.azm.cyberpunk.client.module.impl.render.Noise
 * Noise closing/opening effect for container screens.
 * Original by strange - "Шумовое проявление окон контейнеров".
 */
public class Noise extends Module {

    public static Noise instance;

    // Settings - kept static for cross-class access (NoiseAnimation uses animType)
    public static MultiSelectSetting render;
    public static ModeSetting noise;
    public static ModeSetting animType;
    public static SliderSetting speed;
    public static SliderSetting noiseScale;
    public static SliderSetting softness;

    private static final int VANILLA_DIM_COLOR = 0x101010;
    private static final float CREATIVE_TAB_MARGIN = 28f;

    private enum Phase { IDLE, OPENING, CLOSING }

    private final NoiseAnimation inventoryNoise = new NoiseAnimation();

    private Phase phase = Phase.IDLE;
    private Screen lastScreen;
    private float rectX, rectY, rectWidth, rectHeight;
    private boolean backdropReady;

    @SuppressWarnings("unchecked")
    public Noise() {
        super("Noise", "Шумовое проявление окон контейнеров", Category.VISUALS);
        instance = this;
        render = register(new MultiSelectSetting("Element", "Выбор элемента").value("Inventory").selected("Inventory"));
        noise = register(new ModeSetting("Mode", "Тип шума", "noise1", "noise1", "noise2"));
        animType = register(new ModeSetting("Anim", "Тип анимации", "Fast to slow", "Fast to slow", "Default"));
        speed = register(new SliderSetting("Speed", "Скорость").range(0.03f, 0.2f).increment(0.01f).setValue(0.05f));
        noiseScale = register(new SliderSetting("Noise Scale", "Масштаб шума").range(1, 5).increment(1).setValue(3f));
        softness = register(new SliderSetting("Softness", "Мягкость").range(0.0f, 1.0f).increment(0.05f).setValue(0.0f));
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        inventoryNoise.reset();
        phase = Phase.IDLE;
        lastScreen = null;
        backdropReady = false;
    }

    public static void captureBackdrop() {
        Noise module = instance;
        if (module == null || !module.isEnabled()) return;
        try {
            module.capture();
        } catch (Exception ignored) {
        }
    }

    public static void renderMasks() {
        Noise module = instance;
        if (module == null || !module.isEnabled()) return;
        try {
            module.drawMasks();
        } catch (Exception ignored) {
        }
    }

    private void capture() {
        backdropReady = false;

        Screen screen = mc.currentScreen;
        boolean container = render.is("Inventory") && screen instanceof HandledScreen<?>;

        if (container) {
            if (screen != lastScreen) {
                lastScreen = screen;
                inventoryNoise.reset();
                phase = Phase.OPENING;
                pipeline().invalidateGui();
            }

            updateRect((HandledScreen<?>) screen);
            inventoryNoise.update(speed.getValue(), true);

            if (inventoryNoise.getNoiseProgress() > 0f) {
                pipeline().captureBackdrop();
                backdropReady = true;
            }
            return;
        }

        if (phase == Phase.OPENING) {
            phase = pipeline().hasGui() ? Phase.CLOSING : Phase.IDLE;
            lastScreen = null;
        }

        if (phase == Phase.CLOSING) {
            inventoryNoise.update(speed.getValue(), false);
            if (inventoryNoise.getNoiseProgress() >= 1f) {
                phase = Phase.IDLE;
                pipeline().invalidateGui();
            }
        }
    }

    private void drawMasks() {
        if (phase == Phase.IDLE) return;

        Identifier texture = Identifier.of("vexsiko", "textures/noises/" + noise.getValue() + ".png");
        float tiling = 3f / Math.max(1f, noiseScale.getValue());
        float blur = softness.getValue() * 0.012f;

        if (phase == Phase.OPENING) {
            pipeline().captureGui();

            if (backdropReady && inventoryNoise.getNoiseProgress() > 0f) {
                pipeline().draw(texture, rectX, rectY, rectWidth, rectHeight,
                        inventoryNoise.getReveal(), tiling, VANILLA_DIM_COLOR, 1f, false, blur);
            }
            return;
        }

        pipeline().draw(texture, rectX, rectY, rectWidth, rectHeight,
                inventoryNoise.getReveal(), tiling, VANILLA_DIM_COLOR, 0f, true, blur);
    }

    private void updateRect(HandledScreen<?> handled) {
        HandledScreenAccessor accessor = (HandledScreenAccessor) handled;
        float extra = handled instanceof CreativeInventoryScreen ? CREATIVE_TAB_MARGIN : 0f;

        rectX = accessor.getBackgroundX();
        rectY = accessor.getBackgroundY() - extra;
        rectWidth = accessor.getBackgroundWidth();
        rectHeight = accessor.getBackgroundHeight() + extra * 2f;
    }

    private NoiseMaskPipeline pipeline() {
        return NoiseMaskPipeline.getInstance();
    }
}
