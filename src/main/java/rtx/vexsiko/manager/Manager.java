package rtx.vexsiko.manager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import rtx.vexsiko.api.chat.commands.CommandManager;
import rtx.vexsiko.api.chat.irc.IrcClient;
import rtx.vexsiko.api.config.ConfigManager;
import rtx.vexsiko.api.drags.DragSystem;
import rtx.vexsiko.api.events.funtime.FunTimeEventsClient;
import rtx.vexsiko.api.liteapi.LiteApiClient;
import rtx.vexsiko.api.mods.chatanim.ChatAnimationMod;
import rtx.vexsiko.api.mods.chathads.ChatHeads;
import rtx.vexsiko.api.mods.geckolib.GeckoLibClient;
import rtx.vexsiko.api.mods.shulkerview.ShulkerViewMod;
import rtx.vexsiko.api.mods.waveycapes.WaveyCapesMod;
import rtx.vexsiko.api.modules.ModuleManager;
import rtx.vexsiko.api.modules.impl.Visuals.custompet.CustomPetWarmup;
import rtx.vexsiko.api.party.PartyClient;
import rtx.vexsiko.utils.animations.AnimationUtil;
import rtx.vexsiko.utils.discord.rpc.DiscordRPCManager;
import rtx.vexsiko.utils.input.GuiMovementHandler;
import rtx.vexsiko.utils.render.render2d.Render2D;
import rtx.vexsiko.utils.render.render2d.gif.GifRenderer;
import rtx.vexsiko.utils.render.warmup.Load;
import rtx.vexsiko.utils.render.warmup.Render2DWarmup;
import rtx.vexsiko.utils.sounds.SoundManager;
import rtx.vexsiko.utils.storage.macro.MacroHandler;

public final class Manager {
    private Manager() {
    }

    public static void shutdown() {
        ConfigManager.saveAll();
        FunTimeEventsClient.INSTANCE.shutdown();
        IrcClient.INSTANCE.stop();
        PartyClient.INSTANCE.stop();
        LiteApiClient.INSTANCE.stop();
        Render2D.close();
    }

    public static void init() {
        Render2D.init();
        Render2DWarmup.init();
        AnimationUtil.init();
        GuiMovementHandler.init();
        ModuleManager.get().init();
        ConfigManager.init();
        CommandManager.get().init();
        new rtx.vexsiko.utils.storage.friend.FriendProtection();
        MacroHandler.init();
        DragSystem.get().init();
        FunTimeEventsClient.INSTANCE.start();
        new GeckoLibClient().onInitializeClient();
        SoundManager.init();
        LiteApiClient.INSTANCE.start();
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraftClient -> Manager.shutdown());
        WaveyCapesMod.INSTANCE.init();
        ShulkerViewMod.init();
        ChatHeads.init();
        ChatAnimationMod.init();
        Thread thread = new Thread(DiscordRPCManager::start, "VexSiko-Discord-RPC-Init");
        thread.setDaemon(true);
        thread.start();
        MinecraftClient minecraftClient2 = MinecraftClient.getInstance();
        Runnable runnable = () -> {
            GifRenderer.preload("vexsiko:gif/kity.gif");
            Load.runStartupWarmup();
        };
        if (minecraftClient2 != null) {
            minecraftClient2.execute(runnable);
        } else {
            runnable.run();
        }
        Identifier identifier = Identifier.of((String)"vexsiko", (String)"ui_font_rewarmup");
        ResourceLoader.get((ResourceType)ResourceType.CLIENT_RESOURCES).registerReloader(identifier, (ResourceReloader)((SynchronousResourceReloader)resourceManager -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) {
                return;
            }
            minecraftClient.execute(() -> {
                boolean bl = !Load.initialReloadSeen;
                Load.initialReloadSeen = true;
                if (bl) {
                    Load.runStartupWarmup();
                    return;
                }
                Load.warmupFonts();
                try {
                    Render2DWarmup.reset();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    CustomPetWarmup.warmup();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            });
        }));
    }
}

