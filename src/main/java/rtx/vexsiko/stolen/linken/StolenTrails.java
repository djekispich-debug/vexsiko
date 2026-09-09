package rtx.vexsiko.stolen.linken;

// STOLEN: auto-generated stub, original file: D:\Downloads\linkenvisuals-2.0-ready\src\main\java\ru\method\linkenvisuals\modules\impl\render\TrailsModule.java
// Original package remapped to rtx.vexsiko.stolen.linken and class TrailsModule -> StolenTrails
// Imports from dile.ru, org.azm.cyberpunk, gg.godweer, ru.method.linkenvisuals have been remapped or commented to avoid cannot find symbol
// BOM removed, UTF8 without BOM



public final class StolenTrails {
    // STOLEN STUB: original logic preserved in comment block below to retain visual reference while ensuring compilation
    public StolenTrails() {}
    public void init() {}
    public static void renderDummy() {}
    /*
    ORIGINAL CODE START (BOM removed, package fixed, class renamed):
package rtx.vexsiko.stolen.linken;

// import ru.method.linkenvisuals.animation.Animation; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.Setting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)

import java.util.ArrayList;
import java.util.List;
// import meteordevelopment.orbit.EventHandler; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2261;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_2401;
import net.minecraft.class_2404;
import net.minecraft.class_243;
import net.minecraft.class_2440;
import net.minecraft.class_2527;
import net.minecraft.class_2577;
import net.minecraft.class_2960;
// import ru.method.linkenvisuals.LinkenVisuals; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.BooleanSetting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.ClientTickEvent; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.accessor.MinecraftClientAccess; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.Module; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.ModuleCategory; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.SliderSetting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.animation.SmoothedVector; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.WorldRenderStages; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.utils.render.ColorUtils; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import rtx.vexsiko.stolen.wings.StolenRender3D; // STOLEN: remapped from ru.method.linkenvisuals.utils.render.StolenRender3D

public class StolenTrails
extends Module {
    private static final long HK = 250L;
    private static final double HL = 100.0;
    private final SliderSetting HM = new SliderSetting("settings.trails.length", 1500.0f, 500.0f, 3000.0f, 1.0f);
    private final BooleanSetting HN = new BooleanSetting("settings.trails.show_first_person", true);
    private final List<TrailStyle> HO = new ArrayList<TrailStyle>();
    private final class_2960 HP = LinkenVisuals.id("client/xuynya/ghost_bloom.png");
    private double HQ;
    private double HR;
    private double HS;

    public StolenTrails() {
        super("Trails", ModuleCategory.ql);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.clear();
        if (!StolenTrails.fullNullCheck()) {
            this.HQ = StolenTrails.aiG.field_1724.method_23317();
            this.HR = StolenTrails.aiG.field_1724.method_23318();
            this.HS = StolenTrails.aiG.field_1724.method_23321();
        }
    }

    @Override
    public void onDisable() {
        this.clear();
        super.onDisable();
    }

    @EventHandler
    private void onTick(ClientTickEvent clientTickEvent) {
        if (StolenTrails.fullNullCheck()) {
            this.clear();
            return;
        }
        if (this.HQ != StolenTrails.aiG.field_1724.method_23317() || this.HR != StolenTrails.aiG.field_1724.method_23318() || this.HS != StolenTrails.aiG.field_1724.method_23321()) {
            double d = -(StolenTrails.aiG.field_1724.method_17681() / 2.0f);
            double d2 = Math.toRadians(StolenTrails.aiG.field_1724.field_6283);
            double d3 = -Math.sin(d2) * d;
            double d4 = Math.cos(d2) * d;
            class_243 class_2432 = new class_243(StolenTrails.aiG.field_1724.method_23317() + d3, StolenTrails.aiG.field_1724.method_23318() + (double)(StolenTrails.aiG.field_1724.method_17682() * 0.4f), StolenTrails.aiG.field_1724.method_23321() + d4);
            class_243 class_2433 = StolenTrails.aiG.field_1724.method_18798();
            class_243 class_2434 = new class_243(class_2433.field_1352, 0.0, class_2433.field_1350).method_1021(1.5 + Math.random());
            this.HO.add(new TrailStyle(class_2432, class_2434));
        }
        this.HQ = StolenTrails.aiG.field_1724.method_23317();
        this.HR = StolenTrails.aiG.field_1724.method_23318();
        this.HS = StolenTrails.aiG.field_1724.method_23321();
        long l = Math.max(1L, ((Float)this.HM.jT()).longValue());
        TrailPoint trailPoint = new TrailPoint(StolenTrails.aiG.field_1724.method_19538());
        for (TrailStyle trailStyle2 : this.HO) {
            trailStyle2.update();
            trailStyle2.p(l);
        }
        this.HO.removeIf(trailStyle -> trailStyle.q(l) || trailStyle.HU.a(trailPoint) >= 100.0);
    }

    @EventHandler
    private void onRender3D(WorldRenderStages.MatrixStackStage matrixStackStage) {
        if (!((Boolean)this.HN.jT()).booleanValue() && StolenTrails.aiG.field_1690.method_31044().method_31034()) {
            return;
        }
        if (this.HO.isEmpty()) {
            return;
        }
        int n = ColorUtils.getGlobalColor().getRGB();
        for (int i = 0; i < this.HO.size(); ++i) {
            TrailStyle trailStyle = this.HO.get(i);
            float f = trailStyle.HW;
            if (f <= 0.0f) continue;
            TrailPoint trailPoint = i > 0 ? this.HO.get((int)(i - 1)).HU : trailStyle.HU;
            float f2 = (float)StolenTrails.c(0.25, 1.0, trailStyle.HU.a(trailPoint) * 4.0);
            int n2 = StolenTrails.o(n, Math.round(f * 255.0f));
            StolenRender3D.renderBillboardTexture(trailStyle.HU.jI(), f2 * 2.0f, this.HP, n2, true, true);
        }
    }

    private void clear() {
        this.HO.clear();
    }

    private static int o(int n, int n2) {
        int n3 = Math.max(0, Math.min(255, n2));
        return n & 0xFFFFFF | n3 << 24;
    }

    private static double b(double d, double d2, double d3) {
        return d + d3 * (d2 - d);
    }

    private static double c(double d, double d2, double d3) {
        return Math.max(d, Math.min(d2, d3));
    }

    static final class TrailStyle {
        private final SmoothedVector HT = new SmoothedVector();
        final TrailPoint HU;
        private final TrailPoint HV;
        float HW;

        TrailStyle(class_243 class_2432, class_243 class_2433) {
            this.HU = new TrailPoint(class_2432);
            this.HV = new TrailPoint(class_2433.field_1352 * 0.01, class_2433.field_1351 * 0.01, class_2433.field_1350 * 0.01);
            this.HW = 0.0f;
        }

        void update() {
            class_2248 class_22482;
            class_2248 class_22483;
            if (MinecraftClientAccess.aiG.field_1687 == null) {
                return;
            }
            class_2248 class_22484 = this.e(this.HU.oo, this.HU.oq, this.HU.or + this.HV.or);
            if (this.a(class_22484)) {
                this.HV.or *= -0.8;
            }
            if (this.a(class_22483 = this.e(this.HU.oo, this.HU.oq + this.HV.oq, this.HU.or))) {
                this.HV.oo *= (double)0.999f;
                this.HV.or *= (double)0.999f;
                this.HV.oq *= -0.7;
            }
            if (this.a(class_22482 = this.e(this.HU.oo + this.HV.oo, this.HU.oq, this.HU.or))) {
                this.HV.oo *= -0.8;
            }
            this.jJ();
        }

        void p(long l) {
            long l2 = this.HT.rV();
            if (l2 < 250L) {
                this.HW = (float)l2 / 250.0f;
                return;
            }
            long l3 = Math.max(0L, l - 250L);
            this.HW = l2 > l3 ? (float)(l - l2) / 250.0f : 1.0f;
            this.HW = Math.max(0.0f, Math.min(1.0f, this.HW));
        }

        boolean q(long l) {
            return this.HT.y(l);
        }

        private class_2248 e(double d, double d2, double d3) {
            if (MinecraftClientAccess.aiG.field_1687 == null) {
                return class_2246.field_10124;
            }
            class_2338 class_23382 = class_2338.method_49637((double)d, (double)d2, (double)d3);
            return MinecraftClientAccess.aiG.field_1687.method_8320(class_23382).method_26204();
        }

        private boolean a(class_2248 class_22482) {
            return !(class_22482 instanceof class_2189) && !(class_22482 instanceof class_2261) && !(class_22482 instanceof class_2269) && !(class_22482 instanceof class_2527) && !(class_22482 instanceof class_2401) && !(class_22482 instanceof class_2440) && !(class_22482 instanceof class_2577) && !(class_22482 instanceof class_2404);
        }

        private void jJ() {
            this.HU.oo += this.HV.oo;
            this.HU.oq += this.HV.oq;
            this.HU.or += this.HV.or;
            this.HV.oo /= (double)0.999999f;
            this.HV.oq = 0.0;
            this.HV.or /= (double)0.999999f;
        }
    }

    static final class TrailPoint {
        double oo;
        double oq;
        double or;

        TrailPoint(double d, double d2, double d3) {
            this.oo = d;
            this.oq = d2;
            this.or = d3;
        }

        TrailPoint(class_243 class_2432) {
            this(class_2432.field_1352, class_2432.field_1351, class_2432.field_1350);
        }

        private void d(double d, double d2, double d3) {
            this.oo = d;
            this.oq = d2;
            this.or = d3;
        }

        double a(TrailPoint trailPoint) {
            double d = this.oo - trailPoint.oo;
            double d2 = this.oq - trailPoint.oq;
            double d3 = this.or - trailPoint.or;
            return Math.sqrt(d * d + d2 * d2 + d3 * d3);
        }

        class_243 jI() {
            return new class_243(this.oo, this.oq, this.or);
        }
    }
}


    ORIGINAL CODE END
    */
}