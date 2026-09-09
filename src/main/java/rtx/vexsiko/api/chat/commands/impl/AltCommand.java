package rtx.vexsiko.api.chat.commands.impl;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import rtx.vexsiko.api.chat.commands.Command;
import rtx.vexsiko.api.ui.social.AltManagerScreen;
import rtx.vexsiko.utils.storage.alt.AltManager;

public final class AltCommand
extends Command {
    public AltCommand() {
        super("alt", "\u0410\u043b\u0442-\u043c\u0435\u043d\u0435\u0434\u0436\u0435\u0440: login/add/del/list/gui", "alts");
    }

    @Override
    public void execute(String string, String[] stringArray) {
        if (stringArray.length == 0) {
            this.openGui();
            return;
        }
        String string2 = stringArray[0].toLowerCase(Locale.ROOT);
        if (string2.equals("gui") || string2.equals("menu")) {
            this.openGui();
            return;
        }
        if (string2.equals("login")) {
            if (stringArray.length < 2) {
                this.usage();
                return;
            }
            String string3 = stringArray[1];
            boolean bl = AltManager.login(string3);
            this.logDirect(bl ? "\u0412\u044b\u043f\u043e\u043b\u043d\u0435\u043d \u0432\u0445\u043e\u0434 \u043a\u0430\u043a " + string3 + " (\u043e\u0444\u0444\u043b\u0430\u0439\u043d)" : "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0432\u043e\u0439\u0442\u0438 \u043a\u0430\u043a " + string3, bl ? Formatting.GREEN : Formatting.RED);
            return;
        }
        if (string2.equals("add")) {
            if (stringArray.length < 2) {
                this.usage();
                return;
            }
            this.logDirect(AltManager.add(stringArray[1]) ? "\u0410\u043b\u0442 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d: " + stringArray[1] : "\u0422\u0430\u043a\u043e\u0439 \u0430\u043b\u0442 \u0443\u0436\u0435 \u0435\u0441\u0442\u044c", null);
            return;
        }
        if (string2.equals("del") || string2.equals("remove")) {
            if (stringArray.length < 2) {
                this.usage();
                return;
            }
            this.logDirect(AltManager.remove(stringArray[1]) ? "\u0410\u043b\u0442 \u0443\u0434\u0430\u043b\u0451\u043d" : "\u0410\u043b\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d", null);
            return;
        }
        if (string2.equals("list")) {
            var alts = AltManager.getAlts();
            if (alts.isEmpty()) {
                this.logDirect("\u0421\u043f\u0438\u0441\u043e\u043a \u0430\u043b\u0442\u043e\u0432 \u043f\u0443\u0441\u0442.", null);
                return;
            }
            this.logDirect("\u0410\u043b\u0442\u044b (" + alts.size() + "): " + String.join(", ", alts), null);
            return;
        }
        this.usage();
    }

    private void openGui() {
        this.mc.send(() -> this.mc.setScreen((Screen)new AltManagerScreen()));
    }

    @Override
    public Stream<String> tabComplete(String string, String[] stringArray) {
        if (stringArray.length == 1) {
            String prefix = stringArray[0] == null ? "" : stringArray[0].toLowerCase(Locale.ROOT);
            return Stream.of("login", "add", "del", "list", "gui").filter(s -> s.startsWith(prefix));
        }
        if (stringArray.length == 2 && stringArray[0].equalsIgnoreCase("login")) {
            String lower = stringArray[1] == null ? "" : stringArray[1].toLowerCase(Locale.ROOT);
            return AltManager.getAlts().stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower));
        }
        return Stream.empty();
    }
}
