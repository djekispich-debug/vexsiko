package rtx.vexsiko.api.modules.impl.Social;
import net.minecraft.client.gui.screen.Screen;
import rtx.vexsiko.api.modules.Category;
import rtx.vexsiko.api.modules.Module;
import rtx.vexsiko.api.ui.social.AltManagerScreen;

public final class AltManagerModule
extends Module {
    public AltManagerModule() {
        super("Alt Manager", "\u041e\u0442\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u043c\u0435\u043d\u0435\u0434\u0436\u0435\u0440 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u043e\u0432 (\u043e\u0444\u0444\u043b\u0430\u0439\u043d-\u0432\u0445\u043e\u0434).", Category.FRIENDS);
    }

    @Override
    protected void onEnable() {
        this.disable();
        this.mc.send(() -> this.mc.setScreen((Screen)new AltManagerScreen()));
    }
}
