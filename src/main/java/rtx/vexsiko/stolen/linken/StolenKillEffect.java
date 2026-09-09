package rtx.vexsiko.stolen.linken;

// STOLEN: auto-generated stub, original file: D:\Downloads\linkenvisuals-2.0-ready\src\main\java\ru\method\linkenvisuals\modules\impl\render\KillEffectModule.java
// Original package remapped to rtx.vexsiko.stolen.linken and class KillEffectModule -> StolenKillEffect
// Imports from dile.ru, org.azm.cyberpunk, gg.godweer, ru.method.linkenvisuals have been remapped or commented to avoid cannot find symbol
// BOM removed, UTF8 without BOM



public final class StolenKillEffect {
    // STOLEN STUB: original logic preserved in comment block below to retain visual reference while ensuring compilation
    public StolenKillEffect() {}
    public void init() {}
    public static void renderDummy() {}
    /*
    ORIGINAL CODE START (BOM removed, package fixed, class renamed):
package rtx.vexsiko.stolen.linken;

// import ru.method.linkenvisuals.api.setting.Setting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.Generated;
// import meteordevelopment.orbit.EventHandler; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_1937;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2960;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_8143;
import net.minecraft.class_9779;
// import ru.method.linkenvisuals.LinkenVisuals; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.mixins.accessors.IWorldRenderer; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.AttackEntityEvent; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.ClientTickEvent; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.EnumSetting; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.Module; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.module.ModuleCategory; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.setting.NamedOption; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.PacketEvent; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.api.event.WorldRenderStages; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
// import ru.method.linkenvisuals.utils.render.ColorUtils; // STOLEN: removed (no Kimiko equivalent, would cause cannot find symbol)
import rtx.vexsiko.stolen.wings.StolenRender3D; // STOLEN: remapped from ru.method.linkenvisuals.utils.render.StolenRender3D

public class StolenKillEffect
extends Module {
    private static final long Cp = 4500L;
    private static final class_2960 Cq = LinkenVisuals.id("client/xuynya/ghost_bloom.png");
    private static final double Cr = 12100.0;
    private static final float Cs = 1.65f;
    private static final float Ct = 0.85f;
    private static final int Cu = 48;
    private static final float Cv = 18.0f;
    private static final float Cw = 15.0f;
    private static final float Cx = 0.1f;
    private static final float Cy = 0.96f;
    private static final int Cz = 107;
    private static final float CA = 53.0f;
    private static final float CB = 3.5f;
    private final Map<Integer, Shard> CC = new HashMap<Integer, Shard>();
    private final EnumSetting<EffectState> CD = new EnumSetting<EffectState>("settings.killeffect.mode", EffectState.CT);
    private final List<Ring> CE = new ArrayList<Ring>();
    private final List<Effect> CF = new ArrayList<Effect>();
    private final List<Effect> CG = new ArrayList<Effect>();
    private final List<Effect> CH = new ArrayList<Effect>();
    private final List<Particle> CI = new ArrayList<Particle>();
    private static final float CJ = 16.0f;
    private static final float CK = 34.0f;
    private static final int CL = 8;
    private static final float CM = 9.0f;
    private static final int CN = 9;
    private static final int CP = 16;
    private static final float CQ = 120.0f;

    public StolenKillEffect() {
        super("KillEffect", ModuleCategory.ql);
    }

    @Override
    public void onDisable() {
        this.CC.clear();
        this.CE.clear();
        this.CF.clear();
        this.CG.clear();
        this.CH.clear();
        this.CI.clear();
        super.onDisable();
    }

    @EventHandler
    private void onAttack(AttackEntityEvent attackEntityEvent) {
        if (StolenKillEffect.fullNullCheck()) {
            return;
        }
        if (attackEntityEvent.a() != StolenKillEffect.aiG.field_1724) {
            return;
        }
        class_1297 class_12972 = attackEntityEvent.b();
        if (!(class_12972 instanceof class_1309)) {
            return;
        }
        class_1309 class_13092 = (class_1309)class_12972;
        if (class_13092 == StolenKillEffect.aiG.field_1724) {
            return;
        }
        long l = System.currentTimeMillis();
        this.CC.put(class_13092.method_5628(), new Shard(l, class_13092.method_19538()));
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive receive) {
        class_2663 class_26632;
        if (StolenKillEffect.fullNullCheck()) {
            return;
        }
        class_2596<?> class_25962 = receive.i();
        if (class_25962 instanceof class_8143) {
            class_8143 class_81432 = (class_8143)class_25962;
            this.a(class_81432);
        } else if (class_25962 instanceof class_2663 && (class_26632 = (class_2663)class_25962).method_11470() == 3) {
            this.a(class_26632);
        }
    }

    private void a(class_2663 class_26632) {
        class_1297 class_12972 = class_26632.method_11469((class_1937)StolenKillEffect.aiG.field_1687);
        if (!(class_12972 instanceof class_1309)) {
            return;
        }
        class_1309 class_13092 = (class_1309)class_12972;
        Shard shard = this.CC.remove(class_13092.method_5628());
        if (shard == null) {
            return;
        }
        class_243 class_2432 = shard.Dk != null ? shard.Dk : class_13092.method_19538();
        this.e(class_2432);
    }

    @EventHandler
    private void onTick(ClientTickEvent clientTickEvent) {
        if (StolenKillEffect.fullNullCheck()) {
            this.CC.clear();
            this.CE.clear();
            this.CF.clear();
            this.CG.clear();
            this.CH.clear();
            this.CI.clear();
            return;
        }
        this.iZ();
        this.ja();
        this.a(this.CG, 16.0f);
        this.a(this.CH, 34.0f);
        if (!this.CI.isEmpty()) {
            long gameTime = StolenKillEffect.aiG.field_1687.method_8510();
            this.CI.removeIf(particle -> gameTime - particle.Db > (long)(particle.Df + 1));
        }
        long l = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, Shard>> iterator = this.CC.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Shard> entry = iterator.next();
            Shard shard = entry.getValue();
            if (l - shard.Dj > 4500L) {
                iterator.remove();
                continue;
            }
            class_1297 class_12972 = StolenKillEffect.aiG.field_1687.method_8469(entry.getKey().intValue());
            if (class_12972 instanceof class_1309) {
                class_1309 class_13092 = (class_1309)class_12972;
                if (class_13092.method_5805() && class_13092.method_6032() > 0.0f) {
                    shard.Dk = class_13092.method_19538();
                    continue;
                }
                class_243 class_2432 = shard.Dk != null ? shard.Dk : class_13092.method_19538();
                this.e(class_2432);
                iterator.remove();
                continue;
            }
            iterator.remove();
        }
    }

    @EventHandler
    private void onRender3D(WorldRenderStages.MatrixStackStage matrixStackStage) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9;
        if (StolenKillEffect.fullNullCheck()) {
            return;
        }
        if (this.CE.isEmpty() && this.CF.isEmpty() && this.CG.isEmpty() && this.CH.isEmpty() && this.CI.isEmpty()) {
            return;
        }
        class_9779 class_97792 = matrixStackStage.k();
        float f10 = class_97792.method_60637(true);
        long l = StolenKillEffect.aiG.field_1687.method_8510();
        int n = ColorUtils.getGlobalColor().getRGB() & 0xFFFFFF;
        if (!this.CI.isEmpty()) {
            class_4587 matrices = matrixStackStage.l();
            Iterator<Particle> particleIterator = this.CI.iterator();
            while (particleIterator.hasNext()) {
                Particle particle = particleIterator.next();
                f9 = (float)(l - particle.Db) + f10;
                if (f9 >= (float)particle.Df) {
                    particleIterator.remove();
                    continue;
                }
                this.a(matrices, particle, f9, n);
            }
        }
        if (!this.CE.isEmpty()) {
            int n2 = 107;
            float f11 = 15.0f;
            float f12 = 0.1f;
            f9 = 0.96f;
            f8 = 3.5f;
            f7 = 53.0f;
            Iterator<Ring> iterator = this.CE.iterator();
            while (iterator.hasNext()) {
                Ring ring = iterator.next();
                f6 = (float)(l - ring.Dg) + f10;
                if (f6 >= f7) {
                    iterator.remove();
                    continue;
                }
                f5 = f6 / f7;
                f4 = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f5 / 0.08f), (float)0.0f, (float)1.0f));
                f3 = 1.0f - StolenKillEffect.smootherStep01(class_3532.method_15363((float)((f5 - 0.7f) / 0.3f), (float)0.0f, (float)1.0f));
                f2 = f4 * f3;
                f = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f5 / 0.35f), (float)0.0f, (float)1.0f));
                float f13 = f6 * 0.28f * (0.2f + 0.8f * f);
                float f14 = f >= 0.999f ? 1.0f : f;
                float f15 = 0.85f + 0.15f * f4;
                this.a(ring.Dh, ring.Di, f13, f8, f12, f11, f9 * f15, n2, f2, n, 0.0f, f14);
                this.a(ring.Dh, ring.Di, -f13 * 0.85f, f8, f12 * 0.92f, f11, f9 * 0.95f * f15, n2, f2, n, (float)Math.PI, f14);
            }
        }
        if (!this.CF.isEmpty()) {
            float f16 = 18.0f;
            float f17 = 1.65f;
            float f18 = 0.85f;
            int n3 = 48;
            Iterator<Effect> iterator = this.CF.iterator();
            while (iterator.hasNext()) {
                Effect effect = iterator.next();
                float f19 = (float)(l - effect.startTick) + f10;
                if (f19 >= f16) {
                    iterator.remove();
                    continue;
                }
                float f20 = f19 / f16;
                f6 = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f20 / 0.12f), (float)0.0f, (float)1.0f));
                f4 = f6 * (f5 = 1.0f - StolenKillEffect.smootherStep01(class_3532.method_15363((float)((f20 - 0.55f) / 0.45f), (float)0.0f, (float)1.0f)));
                if (f4 <= 0.001f) continue;
                f3 = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f20 / 0.55f), (float)0.0f, (float)1.0f));
                f2 = f17 * (0.3f + 0.7f * f3);
                f = Math.max(0.05f, f18 * (0.9f + 0.35f * (1.0f - f20)));
                this.a(effect.CR, effect.CS, n3, f2, f, f4, n);
            }
        }
        if (!this.CG.isEmpty()) {
            Iterator<Effect> sphereIterator = this.CG.iterator();
            while (sphereIterator.hasNext()) {
                Effect effect = sphereIterator.next();
                float f21 = (float)(l - effect.startTick) + f10;
                if (f21 >= 16.0f) {
                    sphereIterator.remove();
                    continue;
                }
                float f22 = f21 / 16.0f;
                f8 = 3.2f * StolenKillEffect.smootherStep01(f22);
                f7 = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f22 / 0.12f), (float)0.0f, (float)1.0f)) * (1.0f - StolenKillEffect.smootherStep01(f22));
                float f23 = 0.72f * (1.05f - 0.55f * f22);
                this.a(effect.CR, f8, f23, f7, n);
            }
        }
        if (!this.CH.isEmpty()) {
            Iterator<Effect> spiralIterator = this.CH.iterator();
            while (spiralIterator.hasNext()) {
                Effect effect = spiralIterator.next();
                float f24 = (float)(l - effect.startTick) + f10;
                if (f24 >= 34.0f) {
                    spiralIterator.remove();
                    continue;
                }
                float f25 = f24 / 34.0f;
                f8 = StolenKillEffect.smootherStep01(class_3532.method_15363((float)(f25 / 0.12f), (float)0.0f, (float)1.0f));
                f7 = 1.0f - StolenKillEffect.smootherStep01(class_3532.method_15363((float)((f25 - 0.6f) / 0.4f), (float)0.0f, (float)1.0f));
                this.a(effect.CR, effect.CS, f25, f8 * f7, n);
            }
        }
    }

    private void a(class_8143 class_81432) {
        class_1309 class_13092;
        int n;
        block7: {
            block6: {
                class_1297 class_12972;
                boolean bl;
                if (StolenKillEffect.aiG.field_1687 == null || StolenKillEffect.aiG.field_1724 == null) {
                    return;
                }
                n = class_81432.comp_1267();
                if (n == StolenKillEffect.aiG.field_1724.method_5628()) {
                    return;
                }
                int n2 = StolenKillEffect.aiG.field_1724.method_5628();
                boolean bl2 = bl = class_81432.comp_1269() == n2 || class_81432.comp_1270() == n2;
                class_1297 ownerEntity = StolenKillEffect.aiG.field_1687.method_8469(class_81432.comp_1270());
                if (!bl && (!(ownerEntity instanceof class_1676) || !this.b((class_1676)ownerEntity))) {
                    return;
                }
                class_12972 = StolenKillEffect.aiG.field_1687.method_8469(n);
                if (!(class_12972 instanceof class_1309)) break block6;
                class_13092 = (class_1309)class_12972;
                if (class_12972 != StolenKillEffect.aiG.field_1724) break block7;
            }
            return;
        }
        long l = System.currentTimeMillis();
        this.CC.put(n, new Shard(l, class_13092.method_19538()));
    }

    private void e(class_243 class_2432) {
        if (class_2432 == null || StolenKillEffect.aiG.field_1687 == null) {
            return;
        }
        switch (((EffectState)this.CD.jT()).ordinal()) {
            case 0: {
                this.f(class_2432);
                break;
            }
            case 1: {
                this.g(class_2432);
                break;
            }
            case 2: {
                this.h(class_2432);
                break;
            }
            case 3: {
                this.i(class_2432);
                break;
            }
            case 4: {
                this.a(this.CG, class_2432);
                break;
            }
            case 5: {
                this.a(this.CH, class_2432);
            }
        }
    }

    private void a(List<Effect> list, class_243 class_2432) {
        if (StolenKillEffect.aiG.field_1687 == null || class_2432 == null) {
            return;
        }
        long l = Double.doubleToLongBits(class_2432.field_1352) ^ Long.rotateLeft(Double.doubleToLongBits(class_2432.field_1350), 19) ^ StolenKillEffect.aiG.field_1687.method_8510();
        list.add(new Effect(StolenKillEffect.aiG.field_1687.method_8510(), class_2432, l));
    }

    private void f(class_243 class_2432) {
        if (StolenKillEffect.aiG.field_1687 == null || class_2432 == null) {
            return;
        }
        long l = Double.doubleToLongBits(class_2432.field_1352) ^ Long.rotateLeft(Double.doubleToLongBits(class_2432.field_1350), 21) ^ StolenKillEffect.aiG.field_1687.method_8510();
        this.CI.add(new Particle(StolenKillEffect.aiG.field_1687.method_8510(), class_2432, l, false, 8));
    }

    private void g(class_243 class_2432) {
        if (StolenKillEffect.aiG.field_1687 == null || class_2432 == null) {
            return;
        }
        long l = Double.doubleToLongBits(class_2432.field_1352) ^ Long.rotateLeft(Double.doubleToLongBits(class_2432.field_1350), 23) ^ StolenKillEffect.aiG.field_1687.method_8510();
        this.CI.add(new Particle(StolenKillEffect.aiG.field_1687.method_8510(), class_2432, l, true, 16));
        StolenKillEffect.aiG.field_1687.method_8486(class_2432.field_1352, class_2432.field_1351, class_2432.field_1350, class_3417.field_14865, class_3419.field_15252, 1.0f, 1.0f, false);
    }

    private void a(class_4587 class_45872, Particle particle, float f, int n) {
        int n2;
        if (StolenKillEffect.aiG.field_1724.method_19538().method_1025(particle.Dc) > 12100.0) {
            return;
        }
        float f2 = f / (float)particle.Df;
        float f3 = 0.55f + 0.45f * (float)Math.sin((double)f * 7.3);
        float f4 = (1.0f - StolenKillEffect.smootherStep01(class_3532.method_15363((float)((f2 - 0.35f) / 0.65f), (float)0.0f, (float)1.0f))) * f3;
        if (f < 1.5f) {
            f4 = Math.max(f4, 0.9f);
        }
        if ((f4 = class_3532.method_15363((float)f4, (float)0.0f, (float)1.0f)) <= 0.02f) {
            return;
        }
        class_243 class_2432 = StolenKillEffect.aiG.field_1773.method_19418().method_19326();
        Random random = new Random(particle.Dd);
        class_243[] class_243Array = particle.De ? this.b(random, particle.Dc) : this.a(random, particle.Dc);
        int n3 = class_243Array.length - 1;
        int n4 = class_3532.method_15340((int)((int)(f4 * 255.0f)), (int)0, (int)255) << 24 | 0xFFFFFF;
        int n5 = class_3532.method_15340((int)((int)(f4 * 120.0f)), (int)0, (int)255) << 24 | n;
        int n6 = class_3532.method_15340((int)((int)(f4 * 45.0f)), (int)0, (int)255) << 24 | n;
        for (n2 = 0; n2 < n3; ++n2) {
            if (particle.De) {
                this.a(class_45872, class_2432, class_243Array[n2], class_243Array[n2 + 1], 0.6f, n6);
            }
            this.a(class_45872, class_2432, class_243Array[n2], class_243Array[n2 + 1], 0.26f, n5);
            this.a(class_45872, class_2432, class_243Array[n2], class_243Array[n2 + 1], 0.075f, n4);
        }
        for (n2 = 0; n2 < (particle.De ? 5 : 2); ++n2) {
            int n7 = 1 + random.nextInt(Math.max(1, n3 - 1));
            class_243 class_2433 = class_243Array[n7];
            class_243 class_2434 = new class_243(random.nextDouble() - 0.5, -0.15 - random.nextDouble() * 0.35, random.nextDouble() - 0.5);
            if (class_2434.method_1027() < 1.0E-6) continue;
            class_243 class_2435 = class_2433.method_1019(class_2434.method_1029().method_1021(1.2 + random.nextDouble() * 1.6));
            this.a(class_45872, class_2432, class_2433, class_2435, 0.15f, n5);
            this.a(class_45872, class_2432, class_2433, class_2435, 0.05f, n4);
        }
    }

    private void a(class_4587 class_45872, class_243 class_2432, class_243 class_2433, class_243 class_2434, float f, int n) {
        class_243 class_2435 = class_2434.method_1020(class_2433);
        if (class_2435.method_1027() < 1.0E-9) {
            return;
        }
        class_243 class_2436 = class_2432.method_1020(class_2433.method_1019(class_2434).method_1021(0.5));
        class_243 class_2437 = class_2435.method_1036(class_2436);
        if (class_2437.method_1027() < 1.0E-9) {
            return;
        }
        class_2437 = class_2437.method_1029().method_1021((double)f);
        StolenRender3D.drawLightningQuad(class_45872, class_2433.method_1019(class_2437), class_2434.method_1019(class_2437), class_2434.method_1020(class_2437), class_2433.method_1020(class_2437), n);
    }

    private class_243[] a(Random random, class_243 class_2432) {
        float f = 9.0f + random.nextFloat() * 4.0f;
        double d = 0.55;
        class_243[] class_243Array = new class_243[10];
        class_243Array[0] = class_2432;
        for (int i = 1; i <= 9; ++i) {
            double d2 = (double)i / 9.0;
            double d3 = i < 9 ? 1.0 : 0.4;
            double d4 = class_2432.field_1352 + (random.nextDouble() - 0.5) * d * 2.0 * d3;
            double d5 = class_2432.field_1350 + (random.nextDouble() - 0.5) * d * 2.0 * d3;
            class_243Array[i] = new class_243(d4, class_2432.field_1351 + d2 * (double)f, d5);
        }
        return class_243Array;
    }

    private class_243[] b(Random random, class_243 class_2432) {
        ArrayList<class_243> arrayList = new ArrayList<class_243>();
        class_243 class_2433 = class_2432.method_1031(0.0, 120.0, 0.0);
        arrayList.add(class_2433);
        while (class_2433.field_1351 > class_2432.field_1351) {
            double d = (random.nextDouble() - 0.5) * 0.8;
            double d2 = -(6.0 + random.nextDouble() * 10.0);
            double d3 = (random.nextDouble() - 0.5) * 0.8;
            class_2433 = class_2433.method_1031(d, d2, d3);
            if (class_2433.field_1351 <= class_2432.field_1351) {
                class_2433 = class_2432;
            }
            arrayList.add(class_2433);
        }
        return arrayList.toArray(new class_243[0]);
    }

    private boolean b(class_1676 class_16762) {
        class_1657 class_16572;
        class_1297 class_12972 = class_16762.method_24921();
        return class_12972 instanceof class_1657 && (class_16572 = (class_1657)class_12972).method_5667().equals(StolenKillEffect.aiG.field_1724.method_5667());
    }

    private void h(class_243 class_2432) {
        if (StolenKillEffect.aiG.field_1687 == null) {
            return;
        }
        if (class_2432 == null) {
            return;
        }
        this.CE.add(new Ring(StolenKillEffect.aiG.field_1687.method_8510(), class_2432, (float)(Math.random() * Math.PI * 2.0)));
    }

    private void iZ() {
        if (StolenKillEffect.aiG.field_1687 == null || this.CE.isEmpty()) {
            return;
        }
        long l = StolenKillEffect.aiG.field_1687.method_8510();
        float f = 53.0f;
        this.CE.removeIf(ring -> (float)(l - ring.Dg) > f + 2.0f);
    }

    private void i(class_243 class_2432) {
        if (StolenKillEffect.aiG.field_1687 == null) {
            return;
        }
        if (class_2432 == null) {
            return;
        }
        long l = Double.doubleToLongBits(class_2432.field_1352) ^ Long.rotateLeft(Double.doubleToLongBits(class_2432.field_1350), 19) ^ StolenKillEffect.aiG.field_1687.method_8510();
        this.CF.add(new Effect(StolenKillEffect.aiG.field_1687.method_8510(), class_2432, l));
    }

    private void ja() {
        if (StolenKillEffect.aiG.field_1687 == null || this.CF.isEmpty()) {
            return;
        }
        long l = StolenKillEffect.aiG.field_1687.method_8510();
        float f = 18.0f;
        this.CF.removeIf(effect -> (float)(l - effect.startTick) > f + 2.0f);
    }

    private void a(List<Effect> list, float f) {
        if (StolenKillEffect.aiG.field_1687 == null || list.isEmpty()) {
            return;
        }
        long l = StolenKillEffect.aiG.field_1687.method_8510();
        list.removeIf(effect -> (float)(l - effect.startTick) > f + 2.0f);
    }

    private boolean j(class_243 class_2432) {
        return ((IWorldRenderer)StolenKillEffect.aiG.field_1769).getFrustum().method_23093(new class_238(class_2432.method_1023(0.5, 0.5, 0.5), class_2432.method_1031(0.5, 0.5, 0.5)));
    }

    private void a(class_243 class_2432, long l, int n, float f, float f2, float f3, int n2) {
        if (n <= 0 || f <= 0.0f || f2 <= 0.0f) {
            return;
        }
        if (StolenKillEffect.aiG.field_1724.method_19538().method_1025(class_2432) > 12100.0) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            float f4;
            int n3;
            float f5;
            float f6;
            float f7;
            long l2 = l + (long)i * -7046029254386353131L;
            float f8 = StolenKillEffect.l(l2);
            float f9 = StolenKillEffect.l(l2 ^ 0xD1B54A32D192ED03L);
            float f10 = StolenKillEffect.l(l2 ^ 0x94D049BB133111EBL);
            float f11 = f8 * ((float)Math.PI * 2);
            float f12 = 1.0f - 2.0f * f9;
            float f13 = class_3532.method_15355((float)Math.max(0.0f, 1.0f - f12 * f12));
            float f14 = class_3532.method_15362((float)f11) * f13;
            class_243 class_2433 = class_2432.method_1031((double)(f14 * f * (f7 = 0.75f + 0.5f * f10)), (double)((f6 = f12 * 0.85f + 0.15f) * f * f7), (double)((f5 = class_3532.method_15374((float)f11) * f13) * f * f7));
            if (!this.j(class_2433) || (n3 = class_3532.method_15340((int)((int)((f4 = class_3532.method_15363((float)(f3 * (0.55f + 0.45f * f10)), (float)0.0f, (float)1.0f)) * 255.0f)), (int)0, (int)255)) <= 3) continue;
            int n4 = n3 << 24 | n2;
            float f15 = Math.max(0.04f, f2 * (0.65f + 0.55f * f10));
            StolenRender3D.renderBillboardTexture(class_2433, f15, Cq, n4, true, true);
        }
    }

    private void a(class_243 class_2432, float f, float f2, float f3, int n) {
        if (StolenKillEffect.aiG.field_1724.method_19538().method_1025(class_2432) > 12100.0) {
            return;
        }
        if (f3 <= 0.003f || f2 <= 0.0f) {
            return;
        }
        int n2 = class_3532.method_15340((int)((int)(f3 * 0.85f * 255.0f)), (int)0, (int)255);
        if (n2 <= 3) {
            return;
        }
        int n3 = n2 << 24 | n;
        int n4 = 30;
        for (int i = 0; i < n4; ++i) {
            float f4 = (float)((double)((float)i / (float)n4) * Math.PI * 2.0);
            class_243 class_2433 = new class_243(class_2432.field_1352 + Math.cos(f4) * (double)f, class_2432.field_1351 + 0.06, class_2432.field_1350 + Math.sin(f4) * (double)f);
            if (!this.j(class_2433)) continue;
            StolenRender3D.renderOrientedTexture(class_2433, f2, Cq, n3, class_2350.field_11036, true, true);
        }
    }

    private void a(class_243 class_2432, long l, float f, float f2, int n) {
        if (StolenKillEffect.aiG.field_1724.method_19538().method_1025(class_2432) > 12100.0) {
            return;
        }
        if (f2 <= 0.003f) {
            return;
        }
        int n2 = 9;
        for (int i = 0; i < n2; ++i) {
            float f3;
            int n3;
            long l2 = l + (long)i * -7046029254386353131L;
            float f4 = StolenKillEffect.l(l2);
            float f5 = StolenKillEffect.l(l2 ^ 0xD1B54A32D192ED03L);
            float f6 = StolenKillEffect.l(l2 ^ 0x94D049BB133111EBL);
            float f7 = f4 * ((float)Math.PI * 2);
            float f8 = 0.2f + f5 * 0.55f;
            float f9 = f * (2.1f + f6 * 1.0f);
            float f10 = class_3532.method_15374((float)(f * 9.0f + f4 * 12.0f)) * 0.14f;
            class_243 class_2433 = class_2432.method_1031(Math.cos(f7) * (double)f8 + (double)f10, (double)f9 + 0.1, Math.sin(f7) * (double)f8);
            if (!this.j(class_2433) || (n3 = class_3532.method_15340((int)((int)((f3 = class_3532.method_15363((float)(f2 * (0.55f + 0.45f * f6)), (float)0.0f, (float)1.0f)) * 255.0f)), (int)0, (int)255)) <= 3) continue;
            int n4 = n3 << 24 | n;
            float f11 = 0.42f * (0.7f + 0.5f * f5);
            StolenRender3D.renderBillboardTexture(class_2433, f11, Cq, n4, true, true);
        }
    }

    private static float l(long l) {
        long l2 = l;
        l2 ^= l2 >>> 33;
        l2 *= -49064778989728563L;
        l2 ^= l2 >>> 33;
        l2 *= -4265267296055464877L;
        l2 ^= l2 >>> 33;
        return (float)(l2 & 0xFFFFFFL) / 1.6777216E7f;
    }

    private void a(class_243 class_2432, float f, float f2, float f3, float f4, float f5, float f6, int n, float f7, int n2, float f8, float f9) {
        if (f5 <= 0.0f || f6 <= 0.0f || n <= 0) {
            return;
        }
        if (StolenKillEffect.aiG.field_1724.method_19538().method_1025(class_2432) > 12100.0) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            class_243 class_2433;
            float f10;
            int n3;
            float f11 = n == 1 ? 0.0f : (float)i / (float)(n - 1);
            float f12 = f + f8 + f11 * f3 * ((float)Math.PI * 2) + f2;
            float f13 = 0.35f + (1.0f - f11) * 0.65f;
            float f14 = f4 * f13;
            double d = class_2432.field_1352 + Math.cos(f12) * (double)f14;
            double d2 = class_2432.field_1351 + (double)(f11 * f5);
            double d3 = class_2432.field_1350 + Math.sin(f12) * (double)f14;
            float f15 = class_3532.method_15374((float)(f11 * (float)Math.PI));
            float f16 = 1.0f;
            if (f9 < 0.999f) {
                f16 = 1.0f - StolenKillEffect.smootherStep01(class_3532.method_15363((float)((f11 - f9) / 0.06f), (float)0.0f, (float)1.0f));
            }
            if ((n3 = class_3532.method_15340((int)((int)((f10 = class_3532.method_15363((float)(f7 * f16 * (0.35f + 0.65f * f15)), (float)0.0f, (float)1.0f)) * 255.0f)), (int)0, (int)255)) <= 3 || !this.j(class_2433 = new class_243(d, d2, d3))) continue;
            float f17 = Math.max(0.04f, f6 * (0.75f + f15 * 0.35f));
            int n4 = n3 << 24 | n2;
            StolenRender3D.renderBillboardTexture(class_2433, f17, Cq, n4, true, true);
        }
    }

    private static float smootherStep01(float f) {
        float f2 = class_3532.method_15363((float)f, (float)0.0f, (float)1.0f);
        return f2 * f2 * f2 * (f2 * (f2 * 6.0f - 15.0f) + 10.0f);
    }

    static enum EffectState
    implements NamedOption  {
        CT("settings.killeffect.mode.lightning"),
        CU("settings.killeffect.mode.storm"),
        CV("settings.killeffect.mode.spiral"),
        CW("settings.killeffect.mode.burst"),
        CX("settings.killeffect.mode.shockwave"),
        CY("settings.killeffect.mode.ascension");
        private final String CZ;

@Override
        public String getName() {
            return this.CZ;
        }

        @Generated
        private EffectState(String string2) {
            this.CZ = string2;
        }

}

    static class Shard {
        long Dj;
        class_243 Dk;

        Shard(long l, class_243 class_2432) {
            this.Dj = l;
            this.Dk = class_2432;
        }
    }

    static final class Particle {
        final long Db;
        final class_243 Dc;
        final long Dd;
        final boolean De;
        final int Df;

        Particle(long l, class_243 class_2432, long l2, boolean bl, int n) {
            this.Db = l;
            this.Dc = class_2432;
            this.Dd = l2;
            this.De = bl;
            this.Df = n;
        }

        @Override
        public final String toString() {
            return "Particle[" + "startTick=" + this.Db + ", " + "origin=" + this.Dc + ", " + "seed=" + this.Dd + ", " + "sky=" + this.De + ", " + "durationTicks=" + this.Df + "]";
        }

        @Override
        public final int hashCode() {
            return java.util.Objects.hash(this.Db, this.Dc, this.Dd, this.De, this.Df);
        }

        @Override
        public final boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Particle other)) return false;
            return java.util.Objects.deepEquals(this.Db, other.Db)
                && java.util.Objects.deepEquals(this.Dc, other.Dc)
                && java.util.Objects.deepEquals(this.Dd, other.Dd)
                && java.util.Objects.deepEquals(this.De, other.De)
                && java.util.Objects.deepEquals(this.Df, other.Df);
        }

        public long jc() {
            return this.Db;
        }

        public class_243 jd() {
            return this.Dc;
        }

        public long je() {
            return this.Dd;
        }

        public boolean jf() {
            return this.De;
        }

        public int jg() {
            return this.Df;
        }
    }

    static class Ring {
        final long Dg;
        final class_243 Dh;
        final float Di;

        @Generated
        public Ring(long l, class_243 class_2432, float f) {
            this.Dg = l;
            this.Dh = class_2432;
            this.Di = f;
        }
    }

    static class Effect {
        final long startTick;
        final class_243 CR;
        final long CS;

        @Generated
        public Effect(long l, class_243 class_2432, long l2) {
            this.startTick = l;
            this.CR = class_2432;
            this.CS = l2;
        }
    }
}

    ORIGINAL CODE END
    */
}