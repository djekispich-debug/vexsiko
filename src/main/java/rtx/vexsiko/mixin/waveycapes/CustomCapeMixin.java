package rtx.vexsiko.mixin.waveycapes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.vexsiko.api.modules.impl.Visuals.Customization;
import rtx.vexsiko.utils.render.cape.CapeGradient;

@Mixin(AbstractClientPlayerEntity.class)
public class CustomCapeMixin {
    @Unique
    private static final Identifier VEXSIKO_CAPE_ASSET_ID = Identifier.of("vexsiko", "capes/cape");
    @Unique
    private static final Identifier VEXSIKO_CAPE_TEXTURE = Identifier.of("vexsiko", "textures/capes/cape.png");
    @Unique
    private static final Identifier VANILLA_ELYTRA_ASSET_ID = Identifier.of("minecraft", "entity/equipment/wings/elytra");
    @Unique
    private static final Identifier VANILLA_ELYTRA_TEXTURE = Identifier.of("minecraft", "textures/entity/equipment/wings/elytra.png");
    @Unique
    private static final AssetInfo.TextureAsset VEXSIKO_CAPE_ASSET = new AssetInfo.TextureAssetInfo(VEXSIKO_CAPE_ASSET_ID, VEXSIKO_CAPE_TEXTURE);
    @Unique
    private static final AssetInfo.TextureAsset VANILLA_ELYTRA_ASSET = new AssetInfo.TextureAssetInfo(VANILLA_ELYTRA_ASSET_ID, VANILLA_ELYTRA_TEXTURE);

    @Inject(method="getSkinTextures", at={@At(value="RETURN")}, cancellable=true, require = 0)
    private void vexsiko_replaceCape(CallbackInfoReturnable<SkinTextures> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)(Object)this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !CustomCapeMixin.vexsiko_shouldUseCustomCape(player, (AbstractClientPlayerEntity)client.player)) {
            return;
        }
        Customization customization = Customization.getInstance();
        if (customization != null && customization.wingsEnabledFor(player)) {
            return;
        }
        CapeGradient.tick();
        SkinTextures skin = (SkinTextures)cir.getReturnValue();
        if (skin != null) {
            cir.setReturnValue(new SkinTextures(skin.body(), CapeGradient.asset(), skin.elytra() == null ? VANILLA_ELYTRA_ASSET : skin.elytra(), skin.model(), skin.secure()));
        }
    }

    @Unique
    private static boolean vexsiko_shouldUseCustomCape(AbstractClientPlayerEntity player, AbstractClientPlayerEntity localPlayer) {
        return player.getUuid().equals(localPlayer.getUuid());
    }
}
