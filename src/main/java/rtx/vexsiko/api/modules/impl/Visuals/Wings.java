package rtx.vexsiko.api.modules.impl.Visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import rtx.vexsiko.api.events.EventHandler;
import rtx.vexsiko.utils.math.MathUtils;
import rtx.vexsiko.api.events.impl.render.WorldRenderEvent;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.modules.settings.impl.BooleanSetting;
import rtx.vexsiko.api.modules.settings.impl.ColorSetting;
import rtx.vexsiko.api.modules.settings.impl.ModeSetting;
import rtx.vexsiko.api.modules.settings.impl.MultiModeSetting;
import rtx.vexsiko.api.modules.settings.impl.NumberSetting;
import rtx.vexsiko.utils.storage.friend.FriendUtils;
import rtx.vexsiko.utils.render.wings.ClientPipelines;
import rtx.vexsiko.utils.render.wings.Render3D;
import rtx.vexsiko.utils.render.wings.WingsShaderRenderer;

import java.awt.Color;

/**
 * Kimiko adaptation of dile.ru Wings.
 * Package remapped to rtx.vexsiko.stolen.wings, imports adapted:
 * dile.ru.api.module.* -> rtx.vexsiko.api.modules.*
 * dile.ru.api.events.* -> rtx.vexsiko.api.events.*
 * dile.ru.api.settings.* -> rtx.vexsiko.api.modules.settings.impl.*
 * dile.ru.utils.render.* -> rtx.vexsiko.stolen.wings.* (Render3D etc.)
 * dile.ru.utils.repository.friend.FriendUtils -> rtx.vexsiko.utils.storage.friend.FriendUtils
 * Mojmap -> Yarn mappings (Minecraft -> MinecraftClient, PoseStack -> MatrixStack, Vec3 -> Vec3d, Mth -> MathHelper, Player -> PlayerEntity)
 * Extends Module and uses @EventHandler with WorldRenderEvent as in Kimiko visuals (see Trails.java:712, BlockOverlay.java:245).
 */
public final class Wings extends Module {
    private static Wings instance;

    private static final float DEFAULT_SPREAD = 8.0f;
    private static final int DEFAULT_ALPHA = 220;

    private static final WingPoint[][] SHAPES = new WingPoint[8][];
    private static final int ANGELIC = 0, DRAGON = 1, BUTTERFLY = 2, PHOENIX = 3,
            CRYSTAL = 4, MECHANICAL = 5, FAIRY = 6, DEMON = 7;

    static {
        SHAPES[ANGELIC] = new WingPoint[]{
                new WingPoint(0.08f, 0.10f, 0.88f),
                new WingPoint(0.28f, 0.34f, 0.78f),
                new WingPoint(0.56f, 0.82f, 0.62f),
                new WingPoint(0.86f, 0.30f, 0.52f),
                new WingPoint(1.14f, 0.46f, 0.40f),
                new WingPoint(1.24f, 0.04f, 0.30f),
                new WingPoint(1.02f, -0.18f, 0.28f),
                new WingPoint(1.18f, -0.64f, 0.22f),
                new WingPoint(0.86f, -0.46f, 0.20f),
                new WingPoint(0.80f, -0.98f, 0.14f),
                new WingPoint(0.54f, -0.74f, 0.16f),
                new WingPoint(0.30f, -1.16f, 0.12f),
                new WingPoint(0.10f, -0.54f, 0.18f)
        };

        SHAPES[DRAGON] = new WingPoint[]{
                new WingPoint(0.10f, 0.12f, 0.90f),
                new WingPoint(0.22f, 0.40f, 0.80f),
                new WingPoint(0.48f, 0.72f, 0.65f),
                new WingPoint(0.80f, 0.60f, 0.55f),
                new WingPoint(1.10f, 0.70f, 0.42f),
                new WingPoint(1.30f, 0.30f, 0.35f),
                new WingPoint(1.20f, -0.10f, 0.30f),
                new WingPoint(1.05f, -0.50f, 0.25f),
                new WingPoint(0.70f, -0.35f, 0.22f),
                new WingPoint(0.50f, -0.70f, 0.18f),
                new WingPoint(0.20f, -0.50f, 0.15f),
                new WingPoint(0.05f, -0.30f, 0.20f)
        };

        SHAPES[BUTTERFLY] = new WingPoint[]{
                new WingPoint(0.12f, 0.15f, 0.92f),
                new WingPoint(0.30f, 0.50f, 0.85f),
                new WingPoint(0.50f, 0.90f, 0.70f),
                new WingPoint(0.70f, 0.80f, 0.60f),
                new WingPoint(0.85f, 0.55f, 0.50f),
                new WingPoint(0.75f, 0.20f, 0.45f),
                new WingPoint(0.55f, -0.15f, 0.40f),
                new WingPoint(0.40f, -0.60f, 0.30f),
                new WingPoint(0.25f, -0.85f, 0.20f),
                new WingPoint(0.12f, -0.65f, 0.25f),
                new WingPoint(0.06f, -0.35f, 0.30f)
        };

        SHAPES[PHOENIX] = new WingPoint[]{
                new WingPoint(0.10f, 0.14f, 0.90f),
                new WingPoint(0.25f, 0.45f, 0.82f),
                new WingPoint(0.52f, 0.78f, 0.68f),
                new WingPoint(0.82f, 0.50f, 0.55f),
                new WingPoint(1.15f, 0.55f, 0.42f),
                new WingPoint(1.28f, 0.15f, 0.32f),
                new WingPoint(1.20f, -0.25f, 0.28f),
                new WingPoint(1.10f, -0.55f, 0.24f),
                new WingPoint(1.25f, -0.85f, 0.18f),
                new WingPoint(0.90f, -0.65f, 0.16f),
                new WingPoint(0.60f, -0.90f, 0.14f),
                new WingPoint(0.30f, -0.70f, 0.12f),
                new WingPoint(0.08f, -0.40f, 0.16f)
        };

        SHAPES[CRYSTAL] = new WingPoint[]{
                new WingPoint(0.15f, 0.10f, 0.85f),
                new WingPoint(0.40f, 0.35f, 0.75f),
                new WingPoint(0.70f, 0.60f, 0.60f),
                new WingPoint(1.00f, 0.40f, 0.50f),
                new WingPoint(0.85f, 0.10f, 0.45f),
                new WingPoint(1.10f, -0.15f, 0.35f),
                new WingPoint(0.90f, -0.45f, 0.30f),
                new WingPoint(0.65f, -0.30f, 0.25f),
                new WingPoint(0.45f, -0.60f, 0.20f),
                new WingPoint(0.20f, -0.40f, 0.22f),
                new WingPoint(0.08f, -0.15f, 0.30f)
        };

        SHAPES[MECHANICAL] = new WingPoint[]{
                new WingPoint(0.08f, 0.08f, 0.90f),
                new WingPoint(0.20f, 0.25f, 0.82f),
                new WingPoint(0.45f, 0.40f, 0.70f),
                new WingPoint(0.70f, 0.35f, 0.58f),
                new WingPoint(0.95f, 0.25f, 0.48f),
                new WingPoint(0.90f, 0.00f, 0.40f),
                new WingPoint(1.10f, -0.15f, 0.32f),
                new WingPoint(0.80f, -0.30f, 0.28f),
                new WingPoint(0.55f, -0.20f, 0.25f),
                new WingPoint(0.40f, -0.45f, 0.20f),
                new WingPoint(0.15f, -0.30f, 0.22f),
                new WingPoint(0.05f, -0.10f, 0.28f)
        };

        SHAPES[FAIRY] = new WingPoint[]{
                new WingPoint(0.10f, 0.12f, 0.90f),
                new WingPoint(0.25f, 0.38f, 0.82f),
                new WingPoint(0.42f, 0.65f, 0.68f),
                new WingPoint(0.55f, 0.70f, 0.58f),
                new WingPoint(0.60f, 0.45f, 0.50f),
                new WingPoint(0.50f, 0.15f, 0.42f),
                new WingPoint(0.38f, -0.10f, 0.35f),
                new WingPoint(0.30f, -0.35f, 0.28f),
                new WingPoint(0.18f, -0.45f, 0.22f),
                new WingPoint(0.08f, -0.25f, 0.26f)
        };

        SHAPES[DEMON] = new WingPoint[]{
                new WingPoint(0.10f, 0.12f, 0.88f),
                new WingPoint(0.25f, 0.38f, 0.80f),
                new WingPoint(0.55f, 0.65f, 0.65f),
                new WingPoint(0.85f, 0.50f, 0.52f),
                new WingPoint(1.15f, 0.55f, 0.40f),
                new WingPoint(1.25f, 0.20f, 0.32f),
                new WingPoint(1.10f, -0.10f, 0.28f),
                new WingPoint(1.30f, -0.45f, 0.22f),
                new WingPoint(1.15f, -0.70f, 0.18f),
                new WingPoint(0.85f, -0.55f, 0.16f),
                new WingPoint(0.55f, -0.85f, 0.14f),
                new WingPoint(0.25f, -0.65f, 0.12f),
                new WingPoint(0.08f, -0.35f, 0.18f)
        };
    }

    private static final int[] RIBS_DEFAULT = {2, 4, 7, 9, 11};
    private static final int[] RIBS_SMALL = {2, 4, 6, 8};

    private final ModeSetting wingType = register(new ModeSetting("Wing Type", "Type of wings to render.", "Angelic",
            "Angelic", "Dragon", "Butterfly", "Phoenix", "Crystal", "Mechanical", "Fairy", "Demon"));
    private final ModeSetting fillType = register(new ModeSetting("Заливка", "Тип заливки крыльев.", "Обычный",
            "Обычный", "Шейдерные"));
    private final MultiModeSetting targets = register(new MultiModeSetting("Targets", "Who can see the wings.",
            new String[]{"Self", "Friends", "Players"}, "Self"));
    private final NumberSetting wingScale = register(new NumberSetting("Scale", "Wing size.", 1.0, 0.3, 3.0, 0.1));
    private final NumberSetting height = register(new NumberSetting("Height", "Wing height on the back.", 1.5, 0.8, 2.5, 0.05));
    private final NumberSetting depthOffset = register(new NumberSetting("Depth", "How far wings stick out from back.", 0.15, 0.0, 0.5, 0.01));
    private final BooleanSetting flapping = register(new BooleanSetting("Flapping", "Enable wing flapping animation.", true));
    private final NumberSetting flapStrength = register(new NumberSetting("Flap Strength", "Strength of the flap bend.", 30.0, 5.0, 60.0, 1.0));
    private final NumberSetting flapSpeed = register(new NumberSetting("Flap Speed", "Speed of flapping.", 3.0, 0.5, 8.0, 0.5));
    private final BooleanSetting throughWalls = register(new BooleanSetting("Through Walls", "Render wings through walls.", false));
    private final ModeSetting colorMode = register(new ModeSetting("Цвет", "Источник цвета", "Свой", "Свой", "Клиент"));
    private final ColorSetting wingColor = register(new ColorSetting("Color", "Wing color.", new Color(255, 255, 255, 220)));

    private float selfBodyYaw;
    private boolean selfBodyYawInitialized;

    public Wings() {
        super("Wings", "Renders wings on player backs.", Category.VISUALS);
        instance = this;
        flapStrength.visibleWhen(() -> flapping.getValue());
        flapSpeed.visibleWhen(() -> flapping.getValue());
        wingColor.visibleWhen(() -> colorMode.is("Свой"));
    }

    public static Wings getInstance() {
        return instance;
    }

    private WingPoint[] getCurrentShape() {
        return switch (wingType.getValue()) {
            case "Dragon" -> SHAPES[DRAGON];
            case "Butterfly" -> SHAPES[BUTTERFLY];
            case "Phoenix" -> SHAPES[PHOENIX];
            case "Crystal" -> SHAPES[CRYSTAL];
            case "Mechanical" -> SHAPES[MECHANICAL];
            case "Fairy" -> SHAPES[FAIRY];
            case "Demon" -> SHAPES[DEMON];
            default -> SHAPES[ANGELIC];
        };
    }

    private int[] getCurrentRibIndices(WingPoint[] shape) {
        return shape.length <= 10 ? RIBS_SMALL : RIBS_DEFAULT;
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        // In Kimiko WorldRenderEvent uses getPartialTicks() and getStack() and getCamera()
        float tickDelta = event.getPartialTicks();
        Vec3d camera = event.getCamera() != null ? event.getCamera().getCameraPos() : Vec3d.ZERO;
        Render3D.lastCameraPos = camera;
        MatrixStack stack = event.getStack();

        if (camera == null) return;

        // Yarn: check first-person via Perspective
        boolean isFirstPerson = mc.options.getPerspective().isFirstPerson();
        if (targets.isSelected("Self") && !isFirstPerson
                && mc.player.isAlive() && !hasElytra(mc.player)) {
            try { renderWings(stack, mc.player, tickDelta, camera); } catch (Exception ignored) {}
        }

        if (targets.isSelected("Friends") || targets.isSelected("Players")) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;
                if (!player.isAlive() || hasElytra(player)) continue;
                if (!shouldRender(player)) continue;
                try { renderWings(stack, player, tickDelta, camera); } catch (Exception ignored) {}
            }
        }
    }

    private void renderWings(MatrixStack stack, PlayerEntity player, float tickDelta, Vec3d camera) {
        MinecraftClient mc = MinecraftClient.getInstance();
        VertexConsumerProvider.Immediate provider = mc.getBufferBuilders().getEntityVertexConsumers();

        Vec3d lerpedPos = MathUtils.interpolate(player, tickDelta);
        // Fallback to getLerpedPos if MathUtils is not available, ensures tickDelta interpolation (fixes dithering/blinking)
        // MathUtils.interpolate uses entity.getLerpedPos(tickDelta) internally
        double x = lerpedPos.x - camera.x;
        double y = lerpedPos.y - camera.y;
        double z = lerpedPos.z - camera.z;

        float bodyYaw = resolveBodyYaw(player, tickDelta);
        float move = MathHelper.clamp((float) player.getVelocity().horizontalLength() * 10f, 0f, 1f);

        WingPose pose = resolvePose(player, tickDelta);
        if (pose == null) return;

        float flap = 0f;
        if (flapping.getValue()) {
            float speedMul = flapSpeed.getFloat() / 3.0f;
            float ampMul = flapStrength.getFloat() / 30.0f;
            float effectiveSpeed = pose.flapSpeed * speedMul;
            float effectiveAmp = pose.flapAmplitude * ampMul;
            flap = (float) Math.sin((player.age + tickDelta) * effectiveSpeed) * effectiveAmp;
        }
        float open = (DEFAULT_SPREAD + flap + move * pose.motionSpreadBoost) * pose.openMultiplier;
        float ws = wingScale.getFloat() * pose.scaleMultiplier;

        WingPoint[] shape = getCurrentShape();
        int[] ribIndices = getCurrentRibIndices(shape);

        int baseColor;
        if (colorMode.is("Клиент")) {
            try {
                rtx.vexsiko.api.modules.impl.Interface.InterfaceModule im = rtx.vexsiko.api.modules.impl.Interface.InterfaceModule.getInstance();
                if (im != null) baseColor = im.clientPrimaryColorOpaque() | 0xFF000000;
                else baseColor = wingColor.getValue();
                // ensure alpha 220
                baseColor = (baseColor & 0x00FFFFFF) | (220 << 24);
            } catch (Throwable t) { baseColor = wingColor.getValue(); }
        } else {
            baseColor = wingColor.getValue();
        }
        int glowColor = interpolateColor(baseColor, 0xFFFFFFFF, 0.28f);
        int coreColor = interpolateColor(baseColor, 0xFFFFFFFF, 0.55f);
        int outlineColor = baseColor;

        boolean depth = !throughWalls.getValue();

        stack.push();
        stack.translate(x, y, z);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - bodyYaw));
        if (pose.preTranslateY != 0f || pose.preTranslateZ != 0f)
            stack.translate(0f, pose.preTranslateY, pose.preTranslateZ);
        if (pose.pitchRotation != 0f)
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.pitchRotation));
        if (pose.rollRotation != 0f)
            stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pose.rollRotation));
        // Fix: выносим крылья из тела — прибавляем depthOffset и height к anchor, +0.06 чтобы можно было придвинуть к телу (Depth 0 = почти в теле)
        float heightOff = height.getFloat() - 1.5f;
        float depthOff = depthOffset.getFloat();
        stack.translate(0f, pose.anchorY + heightOff, pose.anchorZ + depthOff + 0.06f);
        // scale expects floats in Yarn MatrixStack
        stack.scale(ws, ws, ws);

        renderWingSide(stack, provider, -1f, open, baseColor, glowColor, coreColor, outlineColor, pose, shape, ribIndices, depth);
        renderWingSide(stack, provider, 1f, open, baseColor, glowColor, coreColor, outlineColor, pose, shape, ribIndices, depth);

        stack.pop();

        // Flush batches - Yarn uses immediate.draw(layer)
        try {
            provider.draw(ClientPipelines.WINGS_GLOW);
            provider.draw(ClientPipelines.WINGS_GLOW_DEPTH);
            provider.draw(ClientPipelines.WINGS_FILLED);
            provider.draw(ClientPipelines.WINGS_FILLED_NOTHROUGH);
            provider.draw(ClientPipelines.WINGS_OUTLINE);
            provider.draw(ClientPipelines.WINGS_OUTLINE_DEPTH);
            provider.draw(ClientPipelines.WINGS_RIBS);
            provider.draw(ClientPipelines.WINGS_RIBS_DEPTH);
        } catch (Throwable ignored) {}
    }

    private void renderWingSide(MatrixStack stack, VertexConsumerProvider.Immediate provider,
                                float side, float open, int baseColor, int glowColor, int coreColor, int outlineColor,
                                WingPose pose, WingPoint[] shape, int[] ribIndices, boolean depth) {
        stack.push();
        stack.translate(side * pose.sideOffset, pose.sideYOffset, pose.sideZOffset);
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * open));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * pose.sideRoll));
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.sidePitch));

        net.minecraft.client.render.RenderLayer glowType = depth ? ClientPipelines.WINGS_GLOW_DEPTH : ClientPipelines.WINGS_GLOW;
        drawWingLayer(provider, stack, side, 1.22f, setAlpha(glowColor, (int) (DEFAULT_ALPHA * 0.22f)), setAlpha(glowColor, 0), glowType, shape);
        drawWingLayer(provider, stack, side, 0.84f, setAlpha(coreColor, (int) (DEFAULT_ALPHA * 0.26f)), setAlpha(coreColor, 0), glowType, shape);

        if (fillType.is("Шейдерные")) {
            Matrix4f modelView = new Matrix4f(stack.peek().getPositionMatrix());
            WingsShaderRenderer.begin();
            int rootColor = setAlpha(baseColor, DEFAULT_ALPHA);
            int edgeColor = setAlpha(baseColor, 10);
            for (int i = 0; i < shape.length; i++) {
                WingPoint cur = shape[i];
                WingPoint next = shape[(i + 1) % shape.length];
                WingsShaderRenderer.addVertex(0f, 0f, 0f, rootColor);
                WingsShaderRenderer.addVertex(side * cur.x, cur.y, 0f, applyPointAlpha(edgeColor, cur.alphaMul));
                WingsShaderRenderer.addVertex(side * next.x, next.y, 0f, applyPointAlpha(edgeColor, next.alphaMul));
            }
            WingsShaderRenderer.render(modelView, depth);
        } else {
            net.minecraft.client.render.RenderLayer baseType = depth ? ClientPipelines.WINGS_FILLED : ClientPipelines.WINGS_FILLED_NOTHROUGH;
            drawWingLayer(provider, stack, side, 1.0f, setAlpha(baseColor, DEFAULT_ALPHA), setAlpha(baseColor, 10), baseType, shape);
        }

        net.minecraft.client.render.RenderLayer outlineType = depth ? ClientPipelines.WINGS_OUTLINE_DEPTH : ClientPipelines.WINGS_OUTLINE;
        drawWingOutline(provider, stack, side, 1.0f, setAlpha(outlineColor, (int) (DEFAULT_ALPHA * 0.62f)), outlineType, shape);

        net.minecraft.client.render.RenderLayer ribsType = depth ? ClientPipelines.WINGS_RIBS_DEPTH : ClientPipelines.WINGS_RIBS;
        drawWingRibs(provider, stack, side, 0.96f, setAlpha(glowColor, (int) (DEFAULT_ALPHA * 0.20f)), ribsType, shape, ribIndices);

        stack.pop();
    }

    private void drawWingLayer(VertexConsumerProvider.Immediate provider, MatrixStack stack,
                               float side, float scale, int rootColor, int edgeColor, net.minecraft.client.render.RenderLayer renderType, WingPoint[] shape) {
        VertexConsumer consumer = provider.getBuffer(renderType);
        MatrixStack.Entry entry = stack.peek();
        for (int i = 0; i < shape.length; i++) {
            WingPoint cur = shape[i];
            WingPoint next = shape[(i + 1) % shape.length];
            consumer.vertex(entry, 0f, 0f, 0f).color(rootColor);
            consumer.vertex(entry, side * cur.x * scale, cur.y * scale, 0f).color(applyPointAlpha(edgeColor, cur.alphaMul));
            consumer.vertex(entry, side * next.x * scale, next.y * scale, 0f).color(applyPointAlpha(edgeColor, next.alphaMul));
        }
    }

    private void drawWingOutline(VertexConsumerProvider.Immediate provider, MatrixStack stack,
                                  float side, float scale, int color, net.minecraft.client.render.RenderLayer renderType, WingPoint[] shape) {
        VertexConsumer consumer = provider.getBuffer(renderType);
        MatrixStack.Entry entry = stack.peek();
        for (WingPoint point : shape) {
            consumer.vertex(entry, side * point.x * scale, point.y * scale, 0f).color(color);
        }
        consumer.vertex(entry, side * shape[0].x * scale, shape[0].y * scale, 0f).color(color);
    }

    private void drawWingRibs(VertexConsumerProvider.Immediate provider, MatrixStack stack,
                               float side, float scale, int color, net.minecraft.client.render.RenderLayer renderType, WingPoint[] shape, int[] ribIndices) {
        VertexConsumer consumer = provider.getBuffer(renderType);
        MatrixStack.Entry entry = stack.peek();
        for (int idx : ribIndices) {
            if (idx >= shape.length) continue;
            WingPoint point = shape[idx];
            consumer.vertex(entry, 0f, 0f, 0f).color(setAlpha(color, Math.max(8, (int) (alpha(color) * 0.75f))));
            consumer.vertex(entry, side * point.x * scale, point.y * scale, 0f).color(applyPointAlpha(color, point.alphaMul));
        }
    }

    private int applyPointAlpha(int color, float multiplier) {
        return setAlpha(color, Math.max(0, Math.min(255, (int) (alpha(color) * multiplier))));
    }

    private static int setAlpha(int color, int a) {
        return (MathHelper.clamp(a, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    private static int alpha(int color) { return (color >> 24) & 0xFF; }
    private static int red(int color) { return (color >> 16) & 0xFF; }
    private static int green(int color) { return (color >> 8) & 0xFF; }
    private static int blue(int color) { return color & 0xFF; }

    private static int getColor(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int interpolateColor(int color1, int color2, float t) {
        int r1 = red(color1), g1 = green(color1), b1 = blue(color1), a1 = alpha(color1);
        int r2 = red(color2), g2 = green(color2), b2 = blue(color2), a2 = alpha(color2);
        return getColor(
                (int) (r1 + (r2 - r1) * t),
                (int) (g1 + (g2 - g1) * t),
                (int) (b1 + (b2 - b1) * t),
                (int) (a1 + (a2 - a1) * t)
        );
    }

    private boolean shouldRender(PlayerEntity player) {
        if (FriendUtils.isFriend(player.getName().getString())) return targets.isSelected("Friends");
        return targets.isSelected("Players");
    }

    private boolean hasElytra(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
    }

    private float resolveBodyYaw(PlayerEntity player, float tickDelta) {
        MinecraftClient mcc = MinecraftClient.getInstance();
        float target = MathHelper.lerpAngleDegrees(tickDelta, player.lastBodyYaw, player.bodyYaw);
        if (player != mcc.player) return target;
        if (!selfBodyYawInitialized || player.age < 2) {
            selfBodyYaw = target;
            selfBodyYawInitialized = true;
            return selfBodyYaw;
        }
        selfBodyYaw = approachDegrees(selfBodyYaw, target, 14f);
        return selfBodyYaw;
    }

    private static float approachDegrees(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    private WingPose resolvePose(PlayerEntity player, float tickDelta) {
        float pitch = MathHelper.lerp(tickDelta, player.lastPitch, player.getPitch());

        if (player.isGliding()) {
            float flightTicks = (float) player.age + tickDelta;
            float flightProgress = MathHelper.clamp(flightTicks * flightTicks / 100f, 0f, 1f);
            float pitchRotation = flightProgress * (-90f - pitch);
            return new WingPose(0.34f, 0.46f, 0f, 0f, pitchRotation, 0f,
                    0.76f, 0.92f, 0.10f, 0.58f, 0.05f, 0.06f, -5f, -2f, 0.13f);
        }

        if (player.isTouchingWater()) return null;

        if (player.isSneaking()) {
            return new WingPose(0f, 0f, 0.96f, 0.10f, 18f, 0f,
                    1f, 1f, 0.18f, 4.5f, 0.06f, 0.02f, -11f, -4f, 0.12f);
        }

        return new WingPose(0f, 0f, 1.38f, 0.10f, 0f, 0f,
                1f, 1f, 0.18f, 4.5f, 0.06f, 0.02f, -11f, -4f, 0.12f);
    }

    @Override
    protected void onDisable() {
        selfBodyYawInitialized = false;
        super.onDisable();
    }

    private static final class WingPoint {
        final float x, y, alphaMul;
        WingPoint(float x, float y, float alphaMul) { this.x = x; this.y = y; this.alphaMul = alphaMul; }
    }

    private static final class WingPose {
        final float preTranslateY, preTranslateZ;
        final float anchorY, anchorZ;
        final float pitchRotation, rollRotation;
        final float openMultiplier, scaleMultiplier;
        final float motionSpreadBoost, flapAmplitude;
        final float sideOffset, sideYOffset, sideZOffset;
        final float sideRoll, sidePitch, flapSpeed;

        WingPose(float preTranslateY, float preTranslateZ, float anchorY, float anchorZ,
                 float pitchRotation, float rollRotation, float openMultiplier, float scaleMultiplier,
                 float motionSpreadBoost, float flapAmplitude, float sideOffset, float sideZOffset,
                 float sideRoll, float sidePitch, float flapSpeed) {
            this(preTranslateY, preTranslateZ, anchorY, anchorZ, pitchRotation, rollRotation,
                    openMultiplier, scaleMultiplier, motionSpreadBoost, flapAmplitude,
                    sideOffset, 0f, sideZOffset, sideRoll, sidePitch, flapSpeed);
        }

        WingPose(float preTranslateY, float preTranslateZ, float anchorY, float anchorZ,
                 float pitchRotation, float rollRotation, float openMultiplier, float scaleMultiplier,
                 float motionSpreadBoost, float flapAmplitude, float sideOffset, float sideYOffset,
                 float sideZOffset, float sideRoll, float sidePitch, float flapSpeed) {
            this.preTranslateY = preTranslateY;
            this.preTranslateZ = preTranslateZ;
            this.anchorY = anchorY;
            this.anchorZ = anchorZ;
            this.pitchRotation = pitchRotation;
            this.rollRotation = rollRotation;
            this.openMultiplier = openMultiplier;
            this.scaleMultiplier = scaleMultiplier;
            this.motionSpreadBoost = motionSpreadBoost;
            this.flapAmplitude = flapAmplitude;
            this.sideOffset = sideOffset;
            this.sideYOffset = sideYOffset;
            this.sideZOffset = sideZOffset;
            this.sideRoll = sideRoll;
            this.sidePitch = sidePitch;
            this.flapSpeed = flapSpeed;
        }
    }
}
