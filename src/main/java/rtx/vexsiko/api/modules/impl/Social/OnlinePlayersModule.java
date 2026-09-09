package rtx.vexsiko.api.modules.impl.Social;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.PlayerListEntry;
import rtx.vexsiko.api.events.EventHandler;
import rtx.vexsiko.api.events.impl.render.HudRenderEvent;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.modules.settings.impl.BooleanSetting;
import rtx.vexsiko.api.modules.settings.impl.SliderSetting;
import rtx.vexsiko.utils.render.fonts.Fonts;
import rtx.vexsiko.utils.render.render2d.Render2D;
import rtx.vexsiko.utils.storage.friend.FriendUtils;

public final class OnlinePlayersModule
extends Module {
    private static final int BG = 1526729974;
    private static final int HEADER = -134744073;
    private static final int FRIEND_COLOR = -10177546;
    private static final int TEXT_COLOR = -1;
    private final BooleanSetting showPing = this.register(new BooleanSetting("\u041f\u0438\u043d\u0433", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0443 \u0438\u0433\u0440\u043e\u043a\u0430.", true));
    private final SliderSetting maxLines = this.register(new SliderSetting("\u041c\u0430\u043a\u0441. \u0441\u0442\u0440\u043e\u043a", "\u041c\u0430\u043a\u0441\u0438\u043c\u0443\u043c \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435."));

    public OnlinePlayersModule() {
        super("Online Players", "\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0441 \u043e\u0442\u043c\u0435\u0442\u043a\u0430\u043c\u0438 \u0434\u0440\u0443\u0437\u0435\u0439.", Category.DISPLAY);
        this.maxLines.setValue(12.0f);
    }

    @EventHandler
    private void onHud(HudRenderEvent event) {
        // HUD disabled - integrated into ClickGUI Server tab (SocialRenderer)
        // ONLINE: [+ Friend] button, FRIENDS: online • offline counters
    }

    private static final class Row {
        final String name;
        final boolean friend;
        final int ping;

        Row(String name, boolean friend, int ping) {
            this.name = name;
            this.friend = friend;
            this.ping = ping;
        }
    }
}
