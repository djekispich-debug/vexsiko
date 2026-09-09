package rtx.vexsiko.api.modules.impl.Visuals;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import rtx.vexsiko.api.events.EventHandler;
import rtx.vexsiko.api.events.impl.game.TickEvent;
import rtx.vexsiko.api.events.impl.player.AttackEntityEvent;
import rtx.vexsiko.api.events.impl.render.DrawEvent;
import rtx.vexsiko.api.events.impl.render.WorldRenderEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import rtx.vexsiko.api.drags.Position;
import rtx.vexsiko.utils.render.post.shaderkill.ShaderKillPipeline;
import net.minecraft.text.Text;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.modules.ModuleManager;
import rtx.vexsiko.api.modules.impl.Interface.InterfaceModule;
import rtx.vexsiko.api.modules.impl.Visuals.killeffect.KillEffectDeathMemoryTracker;
import rtx.vexsiko.api.modules.settings.impl.BooleanSetting;
import rtx.vexsiko.api.modules.settings.impl.ButtonSetting;
import rtx.vexsiko.api.modules.settings.impl.ColorSetting;
import rtx.vexsiko.api.modules.settings.impl.ModeSetting;
import rtx.vexsiko.api.modules.settings.impl.MultiSelectSetting;
import rtx.vexsiko.api.modules.settings.impl.SeparatorSetting;
import rtx.vexsiko.api.modules.settings.impl.SliderSetting;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.color.RainbowLut;
import rtx.vexsiko.utils.sounds.SoundManager;
import rtx.vexsiko.utils.storage.friend.FriendUtils;
import rtx.vexsiko.utils.time.StopWatch;

/**
 * Шейдер Килл — эффекты убийства.
 * Режимы: "Торнадо" (legacy billboard), "3D Столб" (настоящий world-space
 * 3D VFX: core/shaderkill/tornado3d, процедурная геометрия в VSH),
 * "Портал" (billboard). Цвет: Свой/Клиент/Радуга + кнопка теста на блоке.
 */
public final class ShaderKill extends Module {

    private static final String EFFECT_TORNADO = "Торнадо";
    private static final String EFFECT_TORNADO_3D = "3D Столб";
    private static final String EFFECT_PORTAL = "Портал";
    private static final String COLOR_OWN = "Свой";
    private static final String COLOR_CLIENT = "Клиент";
    private static final String COLOR_RAINBOW = "Радуга";

    private static final String TARGET_PLAYERS = "Игроки";
    private static final String TARGET_FRIENDS = "Друзья";
    private static final String TARGET_MOBS = "Мобы";
    private static final String TARGET_ANIMALS = "Животные";

    // настройки
    private final SeparatorSetting sepEffect = this.register(new SeparatorSetting("Эффект"));
    private final ModeSetting effectMode = this.register(new ModeSetting("Тип эффекта", "Выбор шейдера эффекта убийства.", EFFECT_TORNADO_3D, EFFECT_TORNADO, EFFECT_TORNADO_3D, EFFECT_PORTAL));
    private final SeparatorSetting sepColor = this.register(new SeparatorSetting("Цвета"));
    private final ModeSetting colorMode = this.register(new ModeSetting("Режим цвета", "Режим цвета шейдера.", COLOR_OWN, COLOR_OWN, COLOR_CLIENT, COLOR_RAINBOW));
    private final ColorSetting customColor = this.register(new ColorSetting("Цвет", "Основной цвет шейдера.", new Color(-50116, true)));
    private final BooleanSetting useSecondColor = this.register(new BooleanSetting("Второй цвет", "Использовать второй цвет для градиента.", false));
    private final ColorSetting customSecondColor = this.register(new ColorSetting("Цвет 2", "Второй цвет.", new Color(ColorUtil.lerpColor(-50116, new Color(16,16,16,75).getRGB(), 0.7f), true)));
    private final SeparatorSetting sepTargets = this.register(new SeparatorSetting("Цели"));
    private final MultiSelectSetting effectTargets = this.register(new MultiSelectSetting("Цели эффекта", "Какие сущности запускают шейдер.").value(TARGET_PLAYERS, TARGET_FRIENDS, TARGET_MOBS, TARGET_ANIMALS).selected(TARGET_PLAYERS, TARGET_FRIENDS, TARGET_MOBS, TARGET_ANIMALS));
    private final SeparatorSetting sepSize = this.register(new SeparatorSetting("Размеры"));
    private final SliderSetting tornadoWidth = this.register(new SliderSetting("Ширина торнадо", "Ширина шейдера торнадо в пикселях.").range(40, 250).setValue(110).increment(5));
    private final SliderSetting tornadoHeight = this.register(new SliderSetting("Высота торнадо", "Высота торнадо.").range(80, 400).setValue(220).increment(5));
    private final SliderSetting tornado3DWidth = this.register(new SliderSetting("Ширина 3D", "Ширина энергетического столба в блоках.").range(0.6f, 3.5f).setValue(1.4f).increment(0.1f));
    private final SliderSetting tornado3DHeight = this.register(new SliderSetting("Высота 3D", "Высота энергетического столба в блоках.").range(1.5f, 7.0f).setValue(3.8f).increment(0.1f));
    private final SliderSetting portalSize = this.register(new SliderSetting("Размер портала", "Размер портала (квадрат).").range(60, 350).setValue(160).increment(5));
    private final SliderSetting portalWidth = this.register(new SliderSetting("Ширина портала", "Ширина портала.").range(60, 350).setValue(160).increment(5));
    private final SliderSetting portalHeight = this.register(new SliderSetting("Высота портала", "Высота портала.").range(60, 350).setValue(160).increment(5));
    private final SliderSetting shaderIntensity = this.register(new SliderSetting("Интенсивность", "Яркость шейдера.").range(0.3f, 2.0f).setValue(1.0f).increment(0.05f));
    private final SliderSetting shaderSize = this.register(new SliderSetting("Масштаб шейдера", "Масштаб внутри шейдера (uSize).").range(0.5f, 2.5f).setValue(1.0f).increment(0.05f));
    private final SeparatorSetting sepTest = this.register(new SeparatorSetting("Тест"));
    private final ButtonSetting testButton = this.register(new ButtonSetting("Тест на блоке", "Показать эффект на блоке под прицелом, либо перед игроком.").label("Показать").onClick(this::triggerTestOnBlock));

    // атака память + трекер смерти (как в KillEffect)
    private final Map<Integer, Long> recentlyAttacked = new HashMap<>();
    private final KillEffectDeathMemoryTracker deathMemoryTracker = new KillEffectDeathMemoryTracker(2500L, this::handleRememberedDeath);
    private static final long ATTACK_MEMORY_MS = 2500L;

    // активные визуальные эффекты для WorldRender / частиц
    private static class ActiveEffect {
        Vec3d pos;
        long startMs;
        String type;
        int color1, color2;
        ActiveEffect(Vec3d p, String t, int c1, int c2) { pos=p; startMs=System.currentTimeMillis(); type=t; color1=c1; color2=c2; }
        float progress(long dur) { return Math.min((System.currentTimeMillis()-startMs)/(float)dur, 1f); }
        boolean expired(long dur) { return System.currentTimeMillis()-startMs >= dur; }
    }
    private final List<ActiveEffect> activeEffects = new ArrayList<>();
    private static final long TORNADO_DURATION_MS = 3500L;
    private static final long TORNADO_3D_DURATION_MS = 3200L;
    private static final long PORTAL_DURATION_MS = 2800L;

    private static boolean is3D(String type) { return EFFECT_TORNADO_3D.equals(type); }

    private static long durationFor(String type) {
        if (is3D(type)) return TORNADO_3D_DURATION_MS;
        if (EFFECT_PORTAL.equals(type)) return PORTAL_DURATION_MS;
        return TORNADO_DURATION_MS;
    }

    // для world шейдера — рисуем сразу в WorldRender, без проекции

    public ShaderKill() {
        super("Shader Kill", "Эффект при убийстве: 3D столб / торнадо / портал с выбором цвета.", Category.VISUALS);
        this.useSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_OWN));
        this.customColor.visibleWhen(() -> this.colorMode.is(COLOR_OWN));
        this.customSecondColor.visibleWhen(() -> this.colorMode.is(COLOR_OWN) && this.useSecondColor.getValue());
        this.tornadoWidth.visible(() -> this.effectMode.is(EFFECT_TORNADO));
        this.tornadoHeight.visible(() -> this.effectMode.is(EFFECT_TORNADO));
        this.tornado3DWidth.visible(() -> this.effectMode.is(EFFECT_TORNADO_3D));
        this.tornado3DHeight.visible(() -> this.effectMode.is(EFFECT_TORNADO_3D));
        this.portalSize.visible(() -> this.effectMode.is(EFFECT_PORTAL));
        this.portalWidth.visible(() -> this.effectMode.is(EFFECT_PORTAL));
        this.portalHeight.visible(() -> this.effectMode.is(EFFECT_PORTAL));
    }

    public static ShaderKill getInstance() { return ModuleManager.get().get(ShaderKill.class); }
    public static ShaderKill getInstanceIfReady() { try { return getInstance(); } catch (Exception e) { return null; } }

    @Override
    protected void onDisable() {
        this.recentlyAttacked.clear();
        this.deathMemoryTracker.clear();
        this.activeEffects.clear();
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (!e.isPost()) return;
        if (mc.player == null || mc.world == null) { this.activeEffects.clear(); return; }
        this.deathMemoryTracker.tick();
        // чистим протухшие эффекты
        activeEffects.removeIf(a -> a.expired(durationFor(a.type)));
        // частицы тик не нужен — спавним сразу пачкой
        pruneAttackMemory();
    }

    @EventHandler
    public void onAttack(AttackEntityEvent ev) {
        if (mc.player == null || ev.isSynthetic()) return;
        Entity ent = ev.getTarget();
        if (ent instanceof LivingEntity le && le != mc.player) {
            recentlyAttacked.put(le.getId(), System.currentTimeMillis());
            deathMemoryTracker.remember(le, true);
            pruneAttackMemory();
        }
    }

    // для screen проекции (fallback, тоже фиксирован на блоке)
    private Matrix4f lastPositionMatrix = new Matrix4f();
    private Matrix4f lastProjectionMatrix = new Matrix4f();
    private Vec3d lastCameraPos = Vec3d.ZERO;
    private boolean hasMatrices = false;

    @EventHandler
    public void onWorldRender(WorldRenderEvent ev) {
        if (ev.getPositionMatrix() != null && ev.getProjectionMatrix() != null && ev.getCamera() != null) {
            lastPositionMatrix = new Matrix4f(ev.getPositionMatrix());
            lastProjectionMatrix = new Matrix4f(ev.getProjectionMatrix());
            lastCameraPos = ev.getCamera().getCameraPos();
            hasMatrices = true;
        }
        if (!isEnabled() || activeEffects.isEmpty() || mc.world == null) return;
        // world-space шейдеры — фиксированы в точке смерти, не двигаются с взглядом
        for (ActiveEffect ae : new ArrayList<>(activeEffects)) {
            long dur = durationFor(ae.type);
            if (ae.expired(dur)) continue;
            float progress = ae.progress(dur);
            float time = (System.currentTimeMillis() - ae.startMs) / 1000f;
            float baseIntensity = shaderIntensity.getValue();
            float intensity = baseIntensity;
            if (progress > 0.65f) intensity = baseIntensity * (1f - (progress - 0.65f) / 0.35f);
            float uSize = shaderSize.getValue();
            try {
                if (is3D(ae.type)) {
                    // Настоящий 3D столб: размеры сразу в блоках Minecraft.
                    float wBlocks = tornado3DWidth.getValue();
                    float hBlocks = tornado3DHeight.getValue();
                    ShaderKillPipeline.getInstance().drawTornado3DWorld(ae.pos, wBlocks, hBlocks, time, progress, ae.color1, intensity, uSize);
                } else if (ae.type.equals(EFFECT_TORNADO)) {
                    // Legacy billboard, размеры в пикселях (конверсия в VSH).
                    float worldW = tornadoWidth.getValue();
                    float worldH = tornadoHeight.getValue();
                    ShaderKillPipeline.getInstance().drawTornadoBillboardWorld(ae.pos, worldW, worldH, time, progress, ae.color1, intensity, uSize);
                } else {
                    float ps = portalSize.getValue();
                    float w = portalWidth.getValue();
                    float h = portalHeight.getValue();
                    float worldW, worldH;
                    if (Math.abs(ps - 160) > 0.1f && Math.abs(w - 160) < 0.1f && Math.abs(h - 160) < 0.1f) {
                        worldW = ps;
                        worldH = ps;
                    } else {
                        worldW = w;
                        worldH = h;
                    }
                    ShaderKillPipeline.getInstance().drawPortalWorld(ae.pos, worldW, worldH, time, progress, ae.color1, intensity, uSize);
                }
            } catch (Exception e) {
                System.err.println("[ShaderKill] world draw failed: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onDraw(DrawEvent ev) {
        if (!isEnabled() || activeEffects.isEmpty() || !hasMatrices) return;
        float screenW = Position.screenWidth();
        float screenH = Position.screenHeight();
        if (screenW <= 0 || screenH <= 0) return;
        for (ActiveEffect ae : new ArrayList<>(activeEffects)) {
            // 3D столб уже отрисован в мире с depth-тестом — экранный фолбэк ему не нужен.
            if (is3D(ae.type)) continue;
            long dur = durationFor(ae.type);
            if (ae.expired(dur)) continue;
            float progress = ae.progress(dur);
            float time = (System.currentTimeMillis() - ae.startMs) / 1000f;
            Vector4f v = new Vector4f((float)(ae.pos.x - lastCameraPos.x), (float)(ae.pos.y - lastCameraPos.y), (float)(ae.pos.z - lastCameraPos.z), 1f);
            lastPositionMatrix.transform(v);
            lastProjectionMatrix.transform(v);
            if (v.w <= 0.0001f) continue;
            float ndcX = v.x / v.w;
            float ndcY = v.y / v.w;
            if (ndcX < -1.2f || ndcX > 1.2f || ndcY < -1.2f || ndcY > 1.2f) continue;
            float sx = (ndcX * 0.5f + 0.5f) * screenW;
            float sy = (1f - (ndcY * 0.5f + 0.5f)) * screenH;
            float w, h;
            if (ae.type.equals(EFFECT_TORNADO)) { w = tornadoWidth.getValue(); h = tornadoHeight.getValue(); sy -= h * 0.4f; }
            else { w = portalWidth.getValue(); h = portalHeight.getValue(); float ps = portalSize.getValue(); if (Math.abs(ps - 160) > 0.1f && Math.abs(w - 160) < 0.1f && Math.abs(h - 160) < 0.1f) { w = ps; h = ps; } sy -= h * 0.5f; }
            float x = sx - w * 0.5f;
            float y = sy - h * 0.1f;
            float baseIntensity = shaderIntensity.getValue();
            float intensity = baseIntensity;
            if (progress > 0.65f) intensity = baseIntensity * (1f - (progress - 0.65f) / 0.35f);
            float uSize = shaderSize.getValue();
            try {
                if (ae.type.equals(EFFECT_TORNADO)) ShaderKillPipeline.getInstance().drawTornado(x, y, w, h, time, progress, ae.color1, intensity, uSize);
                else ShaderKillPipeline.getInstance().drawPortal(x, y, w, h, time, progress, ae.color1, intensity, uSize);
            } catch (Exception ignored) {}
        }
    }

    private void pruneAttackMemory() {
        long cutoff = System.currentTimeMillis() - ATTACK_MEMORY_MS;
        recentlyAttacked.values().removeIf(v -> v < cutoff);
    }

    // --- цвета как в KillEffect ---
    private int accent() {
        if (colorMode.is(COLOR_RAINBOW)) return opaque(rainbow());
        if (colorMode.is(COLOR_CLIENT)) {
            InterfaceModule im = InterfaceModule.getInstance();
            if (im != null) {
                int c1 = im.clientPrimaryColorOpaque();
                int c2 = im.usesSecondClientColor() ? im.clientSecondaryColorOpaque() : c1;
                if (c1 == c2) return opaque(c1);
                return opaque(ColorUtil.lerpColor(c1, c2, (System.currentTimeMillis()/8%360>=180?360-(System.currentTimeMillis()/8%360):(System.currentTimeMillis()/8%360))/180f));
            }
            return opaque(-50116);
        }
        int c1 = customColor.getColorOpaque();
        int c2 = useSecondColor.getValue() ? customSecondColor.getColorOpaque() : c1;
        if (c1 == c2) return opaque(c1);
        int n3 = (int)(System.currentTimeMillis()/8%360);
        n3 = n3>=180?360-n3:n3;
        return opaque(ColorUtil.lerpColor(c1,c2, n3/180f));
    }
    private static int rainbow() { int n=(int)(System.currentTimeMillis()/8%360); return RainbowLut.sample(n,1f,1f); }
    private static int opaque(int n){ return 0xFF000000 | n & 0xFFFFFF; }
    private static int red(int n){ return n>>>16 &255; }
    private static int green(int n){ return n>>>8 &255; }
    private static int blue(int n){ return n &255; }
    private static int alpha(int n){ return n>>>24 &255; }
    private static int lighten(int n,float f){ float v=Math.clamp(f,0,1); return ColorUtil.rgba(Math.clamp(Math.round(red(n)+(255-red(n))*v),0,255), Math.clamp(Math.round(green(n)+(255-green(n))*v),0,255), Math.clamp(Math.round(blue(n)+(255-blue(n))*v),0,255), alpha(n)); }
    private static int darken(int n,float f){ float v=1-Math.clamp(f,0,1); return ColorUtil.rgba(Math.clamp(Math.round(red(n)*v),0,255), Math.clamp(Math.round(green(n)*v),0,255), Math.clamp(Math.round(blue(n)*v),0,255), alpha(n)); }

    private boolean matchesEffectTarget(LivingEntity le) {
        if (le==null || le==mc.player) return false;
        if (le instanceof PlayerEntity pe) {
            if (FriendUtils.isFriend(pe.getName().getString())) return effectTargets.isSelected(TARGET_FRIENDS);
            return effectTargets.isSelected(TARGET_PLAYERS);
        }
        if (le instanceof AnimalEntity) return effectTargets.isSelected(TARGET_ANIMALS);
        if (le instanceof MobEntity) return effectTargets.isSelected(TARGET_MOBS);
        return false;
    }

    private boolean localPlayerGotKill(LivingEntity le, DamageSource ds) {
        if (mc.player==null) return false;
        Long t=recentlyAttacked.get(le.getId());
        if (t!=null && System.currentTimeMillis()-t <= ATTACK_MEMORY_MS) return true;
        if (le.getPrimeAdversary()==mc.player) return true;
        return ds!=null && ds.getAttacker()==mc.player;
    }

    /**
     * Точка появления эффекта. Для 3D столба — ноги сущности (основание
     * столба стоит на земле), для legacy-режимов — eyePos как раньше.
     */
    private Vec3d basePosFor(LivingEntity le) {
        if (effectMode.is(EFFECT_TORNADO_3D)) {
            return new Vec3d(le.getX(), le.getY(), le.getZ());
        }
        return le.getEyePos();
    }

    private void handleDeath(LivingEntity le, DamageSource ds) {
        if (mc.player==null || mc.world==null || le==null || le==mc.player) return;
        if (!localPlayerGotKill(le, ds)) return;
        if (!matchesEffectTarget(le)) return;
        recentlyAttacked.remove(le.getId());
        deathMemoryTracker.forget(le.getId());
        triggerShaderEffectAt(basePosFor(le));
    }

    private void handleRememberedDeath(LivingEntity le) {
        if (!matchesEffectTarget(le)) return;
        recentlyAttacked.remove(le.getId());
        triggerShaderEffectAt(basePosFor(le));
    }

    public static void notifyEntityDied(LivingEntity le, DamageSource ds) {
        ShaderKill inst=getInstanceIfReady();
        if (inst==null || !inst.isEnabled()) return;
        inst.handleDeath(le, ds);
    }

    // === ПУБЛИЧНЫЕ ФУНКЦИИ ===

    /** Запустить шейдерный эффект в точке (вызывается при убийстве или кнопкой) */
    public void triggerShaderEffectAt(Vec3d pos) {
        if (pos==null || mc.player==null || mc.world==null) return;
        String type = effectMode.is(EFFECT_PORTAL) ? EFFECT_PORTAL
                : effectMode.is(EFFECT_TORNADO_3D) ? EFFECT_TORNADO_3D : EFFECT_TORNADO;
        int col = accent();
        int col2 = lighten(col, 0.3f);
        // только шейдер — без частиц
        activeEffects.add(new ActiveEffect(pos, type, col, col2));
        // звук
        try { SoundManager.playSound(SoundManager.FRAG_EFFECT_PULSE, 1f, 1f); } catch (Exception ignored) {}
        try { if (mc.world != null) mc.world.playSound(null, pos.x, pos.y, pos.z, net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, net.minecraft.sound.SoundCategory.PLAYERS, 0.6f, 1.4f); } catch (Exception ignored) {}
    }

    /** Кнопка теста — показывает эффект на блоке под прицелом */
    public void triggerTestOnBlock() {
        if (mc.player==null || mc.world==null) return;
        HitResult hr = mc.crosshairTarget;
        Vec3d pos;
        String targetInfo;
        if (hr != null && hr.getType()==HitResult.Type.BLOCK && hr instanceof BlockHitResult bhr) {
            pos = bhr.getPos().add(0, 0.15, 0);
            targetInfo = "блок " + bhr.getBlockPos().toShortString();
        } else if (hr != null && hr.getType()==HitResult.Type.ENTITY && hr instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity le) {
            pos = basePosFor(le);
            targetInfo = "сущность " + le.getName().getString();
        } else {
            pos = mc.player.getEyePos().add(mc.player.getRotationVector().multiply(3.0));
            targetInfo = "перед игроком";
        }
        triggerShaderEffectAt(pos);
        // чат лог чтобы точно знать что кнопка сработала
        try {
            if (mc.inGameHud != null) mc.inGameHud.getChatHud().addMessage(Text.of("§a[ShaderKill] §f" + effectMode.getValue() + " на " + targetInfo + " §7(" + String.format("%.1f,%.1f,%.1f", pos.x, pos.y, pos.z) + ")"));
        } catch (Exception ignored) {}
        System.out.println("[ShaderKill] test on " + targetInfo + " at " + pos + " effect=" + effectMode.getValue() + " color=" + String.format("#%06X", accent() & 0xFFFFFF));
    }

    private void spawnTornadoParticles(Vec3d center, int color) {
        if (mc.world==null) return;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        float r = ((color>>16 &255)/255f);
        float g = ((color>>8 &255)/255f);
        float b = ((color &255)/255f);
        // вертикальный столп 3.5 блока, 45 частиц спиралью
        for (int i=0;i<45;i++) {
            double y = center.y -0.3 + i*0.11;
            double ang = i*0.62 + rnd.nextDouble(-0.2,0.2);
            double rad = 0.18 + (y-center.y+0.3)*0.22 + rnd.nextDouble(-0.07,0.07);
            double x = center.x + Math.cos(ang)*rad;
            double z = center.z + Math.sin(ang)*rad;
            double vx = Math.cos(ang+Math.PI/2)*0.04;
            double vz = Math.sin(ang+Math.PI/2)*0.04;
            double vy = 0.06 + rnd.nextDouble(0,0.04);
            mc.world.addParticleClient(ParticleTypes.END_ROD, x, y, z, vx, vy, vz);
            if (i%3==0) mc.world.addParticleClient(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0,0,0);
        }
        // ядро свечение
        for (int i=0;i<12;i++) {
            double vx=rnd.nextDouble(-0.08,0.08), vy=rnd.nextDouble(0.02,0.12), vz=rnd.nextDouble(-0.08,0.08);
            mc.world.addParticleClient(ParticleTypes.GLOW, center.x, center.y+0.2, center.z, vx, vy, vz);
        }
    }

    private void spawnPortalParticles(Vec3d center, int color) {
        if (mc.world==null) return;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        // кольцо 24 точки
        for (int i=0;i<28;i++) {
            double ang = 2*Math.PI*i/28;
            double rad = 1.1 + rnd.nextDouble(-0.08,0.08);
            double x = center.x + Math.cos(ang)*rad;
            double z = center.z + Math.sin(ang)*rad;
            double y = center.y + rnd.nextDouble(-0.15,0.15);
            mc.world.addParticleClient(ParticleTypes.PORTAL, x, y, z, -Math.cos(ang)*0.02, rnd.nextDouble(-0.02,0.02), -Math.sin(ang)*0.02);
            mc.world.addParticleClient(ParticleTypes.REVERSE_PORTAL, x, y+0.15, z, 0,0.02,0);
        }
        // сфера внутри
        for (int i=0;i<18;i++) {
            double ang1=rnd.nextDouble()*Math.PI*2, ang2=rnd.nextDouble()*Math.PI-0.5;
            double rad=0.6*rnd.nextDouble();
            double x=center.x+Math.cos(ang1)*Math.cos(ang2)*rad;
            double y=center.y+0.4+Math.sin(ang2)*rad;
            double z=center.z+Math.sin(ang1)*Math.cos(ang2)*rad;
            mc.world.addParticleClient(ParticleTypes.WITCH, x, y, z, 0,0,0);
        }
        // вспышка центр
        for (int i=0;i<8;i++) mc.world.addParticleClient(ParticleTypes.FLAME, center.x, center.y+0.4, center.z, 0,0.04,0);
    }
}
