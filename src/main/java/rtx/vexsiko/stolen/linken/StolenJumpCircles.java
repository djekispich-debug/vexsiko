package rtx.vexsiko.stolen.linken;

// STOLEN: auto-generated stub, original file: D:\Downloads\linkenvisuals-2.0-ready\src\main\java\ru\method\linkenvisuals\modules\impl\render\JumpCirclesModule.java
// Original package remapped to rtx.vexsiko.stolen.linken and class JumpCirclesModule -> StolenJumpCircles
// Imports from dile.ru, org.azm.cyberpunk, gg.godweer, ru.method.linkenvisuals have been remapped or commented to avoid cannot find symbol
// BOM removed, UTF8 without BOM



public final class StolenJumpCircles {
    // STOLEN STUB: original logic preserved in comment block below to retain visual reference while ensuring compilation
    public StolenJumpCircles() {}
    public void init() {}
    public static void renderDummy() {}
    /*
    ORIGINAL CODE START (BOM removed, package fixed, class renamed):
package rtx.vexsiko.stolen.linken;

// import ru.method.linkenvisuals.api.setting.Setting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
// import meteordevelopment.orbit.EventHandler; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
// import ru.method.linkenvisuals.LinkenVisuals; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.animation.Animation; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.animation.Easing; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.Module; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.ModuleCategory; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.PlayerTickEvent; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.SliderSetting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.WorldRenderStages; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.utils.render.ColorUtils; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import rtx.vexsiko.stolen.wings.StolenRender3D; // STOLEN: remapped from ru.method.linkenvisuals.utils.render.StolenRender3D

public class StolenJumpCircles
extends Module {
    private static final class_2960 Ce = LinkenVisuals.id("client/other/circle.png");
    private static final float Cf = 1.15f;
    private static final long Cg = 500L;
    private static final long Ch = 500L;
    private static final long Ci = 600L;
    private final SliderSetting Cj = new SliderSetting("settings.jumpcircles.size", 1.5f, 1.0f, 2.0f, 0.1f);
    private final SliderSetting Ck = new SliderSetting("settings.jumpcircles.lifetime", 4.0f, 1.0f, 10.0f, 0.1f);
    private final List<Circle> Cl = new ArrayList<Circle>();
    private boolean wasOnGround;

    public StolenJumpCircles() {
        super("JumpCircles", ModuleCategory.ql);
    }

    @EventHandler
    private void onPlayerTick(PlayerTickEvent playerTickEvent) {
        if (StolenJumpCircles.fullNullCheck()) {
            return;
        }
        boolean bl = StolenJumpCircles.aiG.field_1724.method_24828();
        boolean bl2 = StolenJumpCircles.aiG.field_1690.field_1903.method_1434();
        if (this.wasOnGround && !bl && bl2) {
            class_243 class_2432 = new class_243(StolenJumpCircles.aiG.field_1724.method_23317(), (double)StolenJumpCircles.aiG.field_1724.method_24515().method_10264() + 0.01, StolenJumpCircles.aiG.field_1724.method_23321());
            this.Cl.add(new Circle(class_2432));
        }
        this.wasOnGround = bl;
    }

    @EventHandler
    private void onRender3D(WorldRenderStages.MatrixStackStage matrixStackStage) {
        if (StolenJumpCircles.fullNullCheck()) {
            return;
        }
        if (this.Cl.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        Color color = ColorUtils.getGlobalColor();
        Iterator<Circle> iterator = this.Cl.iterator();
        while (iterator.hasNext()) {
            float f;
            Circle circle = iterator.next();
            long l2 = l - circle.Cn;
            long l3 = Math.max(600L, (long)(((Float)this.Ck.jT()).floatValue() * 1000.0f));
            long l4 = Math.max(0L, l3 - 500L);
            if (l2 >= l3) {
                iterator.remove();
                continue;
            }
            if (l2 >= l4 && !circle.fadingOut) {
                circle.Co.w(500L);
                circle.Co.i(false);
                circle.fadingOut = true;
            }
            if ((f = circle.Co.rk()) <= 1.0f && circle.fadingOut) continue;
            int n = class_3532.method_15340((int)Math.round(f), (int)0, (int)255);
            if (f <= 3.0f) continue;
            float f2 = Math.max(0.05f, ((Float)this.Cj.jT()).floatValue());
            Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), n);
            Color color3 = ColorUtils.fade(color2, new Color(255, 255, 255, n), 0.35f);
            StolenRender3D.renderOrientedTexture(circle.Cm, f2, Ce, color2.getRGB(), class_2350.field_11036, true, true);
            StolenRender3D.renderOrientedTexture(circle.Cm, f2 * 1.15f, Ce, color3.getRGB(), class_2350.field_11036, true, true);
        }
    }

    @Override
    public void onDisable() {
        this.Cl.clear();
        super.onDisable();
    }

    static class Circle {
        final class_243 Cm;
        final long Cn;
        final Animation Co;
        boolean fadingOut;

        Circle(class_243 class_2432) {
            this.Cm = class_2432;
            this.Cn = System.currentTimeMillis();
            this.Co = new Animation(500L, 255.0, true, Easing.aiR);
        }
    }
}


    ORIGINAL CODE END
    */
}