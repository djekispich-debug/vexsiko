package rtx.vexsiko.api.modules.impl.Visuals;

import rtx.vexsiko.api.events.EventHandler;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import rtx.vexsiko.api.events.impl.game.TickEvent;
import rtx.vexsiko.api.events.impl.render.WorldRenderEvent;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.modules.settings.impl.ColorSetting;
import rtx.vexsiko.api.modules.settings.impl.NumberSetting;
import rtx.vexsiko.utils.render.post.worldglow.WorldGlowRenderer;

import java.awt.Color;

public final class WorldGlow extends Module {
    private static WorldGlow instance;

    private final NumberSetting strength = this.register(new NumberSetting("Strength", "Opacity of the glow effect.", 1.0, 0.0, 1.0, 0.05));
    private final NumberSetting speed = this.register(new NumberSetting("Speed", "Speed of the moving glow.", 1.0, 0.1, 5.0, 0.1));
    private final ColorSetting color = this.register(new ColorSetting("Glow Color", "Color of the moving glow.", new Color(127, 242, 255, 255)));

    private float time;
    private long lastNanos;

    public WorldGlow() {
        super("World Glow", "Makes the world glow and shine through from you to the horizon.", Category.VISUALS);
        instance = this;
    }

    public static WorldGlow getInstance() {
        return instance;
    }

    @Override
    protected void onEnable() {
        time = 0.0f;
        lastNanos = 0L;
    }

    @Override
    protected void onDisable() {
        WorldGlowRenderer.clear();
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (!e.isPre()) return;
        long now = System.nanoTime();
        if (lastNanos != 0L) {
            time += (now - lastNanos) / 1_000_000_000.0f;
        }
        lastNanos = now;
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent e) {
        if (mc.player == null || mc.world == null || mc.gameRenderer == null) return;
        if (WorldGlowRenderer.isDisabledAfterError()) return;
        Framebuffer fb = mc.getFramebuffer();
        if (fb == null) return;
        Matrix4f proj = e.getProjectionMatrix();
        Matrix4f view = e.getPositionMatrix();
        Vec3d cam = e.getCamera() != null ? e.getCamera().getCameraPos() : mc.gameRenderer.getCamera().getCameraPos();
        if (proj == null || view == null || cam == null) return;
        WorldGlowRenderer.apply(
                fb,
                color.getColor(),
                strength.getFloat(),
                speed.getFloat(),
                time,
                proj,
                view,
                cam
        );
    }
}
