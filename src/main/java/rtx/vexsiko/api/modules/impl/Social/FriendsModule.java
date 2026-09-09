package rtx.vexsiko.api.modules.impl.Social;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.utils.storage.friend.FriendUtils;

public final class FriendsModule
extends Module {
    public FriendsModule() {
        super("Friends", "\u0421\u0438\u0441\u0442\u0435\u043c\u0430 \u0434\u0440\u0443\u0437\u0435\u0439: \u043f\u043e\u043a\u0430 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430, \u043c\u043e\u0434\u0443\u043b\u0438 \u0438\u0433\u043d\u043e\u0440\u0438\u0440\u0443\u044e\u0442 \u0441\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439. \u041a\u043e\u043c\u0430\u043d\u0434\u0430 .friend", Category.FRIENDS);
    }

    @Override
    protected void onEnable() {
        FriendUtils.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        FriendUtils.setEnabled(false);
    }
}
