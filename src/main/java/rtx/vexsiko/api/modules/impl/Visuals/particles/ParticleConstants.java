package rtx.vexsiko.api.modules.impl.Visuals.particles;

import net.minecraft.util.Identifier;

public final class ParticleConstants {
    public static final ParticleTexture[] TEXTURES = new ParticleTexture[]{
        new ParticleTexture("Точка", Identifier.of("vexsiko", "textures/features/particles/point.png")),
        new ParticleTexture("Звезда", Identifier.of("vexsiko", "textures/features/particles/star.png")),
        new ParticleTexture("Молния", Identifier.of("vexsiko", "textures/features/particles/lighting.png")),
        new ParticleTexture("Крест", Identifier.of("vexsiko", "textures/features/particles/cross.png")),
        new ParticleTexture("Корона", Identifier.of("vexsiko", "textures/features/particles/crown.png")),
        new ParticleTexture("Сердце", Identifier.of("vexsiko", "textures/features/particles/heart.png")),
        new ParticleTexture("Линия", Identifier.of("vexsiko", "textures/features/particles/line.png")),
        new ParticleTexture("Ромб", Identifier.of("vexsiko", "textures/features/particles/rhombus.png")),
        new ParticleTexture("Доллар", Identifier.of("vexsiko", "textures/features/particles/dollar.png")),
        new ParticleTexture("Снежинка", Identifier.of("vexsiko", "textures/features/particles/snowflake.png")),
        new ParticleTexture("Треугольник", Identifier.of("vexsiko", "textures/features/particles/triangle.png"))
    };

    private ParticleConstants() {}
}
