package rtx.vexsiko.api.chat.commands.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rtx.vexsiko.api.chat.commands.Command;
import rtx.vexsiko.utils.storage.friend.FriendUtils;

public final class FriendCommand
extends Command {
    public FriendCommand() {
        super("friend", "\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438: add/del/list/clear", "f", "friends");
    }

    @Override
    public void execute(String string, String[] stringArray) {
        if (stringArray.length == 0) {
            this.usage();
            return;
        }
        String string2 = stringArray[0].toLowerCase(Locale.ROOT);
        if (string2.equals("add") || string2.equals("+")) {
            if (stringArray.length < 2) {
                this.usage();
                return;
            }
            String string3 = stringArray[1];
            this.logDirect(FriendUtils.add(string3) ? (MutableText)this.text("\u0414\u0440\u0443\u0433 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d: ", Formatting.GREEN).append((Text)Text.literal(string3).formatted(Formatting.WHITE)) : (MutableText)this.text("\u0422\u0430\u043a\u043e\u0439 \u0434\u0440\u0443\u0433 \u0443\u0436\u0435 \u0435\u0441\u0442\u044c: ", Formatting.YELLOW).append((Text)Text.literal(string3).formatted(Formatting.WHITE)));
            return;
        }
        if (string2.equals("del") || string2.equals("remove") || string2.equals("-")) {
            if (stringArray.length < 2) {
                this.usage();
                return;
            }
            String string4 = stringArray[1];
            this.logDirect(FriendUtils.remove(string4) ? (MutableText)this.text("\u0414\u0440\u0443\u0433 \u0443\u0434\u0430\u043b\u0451\u043d: ", Formatting.GREEN).append((Text)Text.literal(string4).formatted(Formatting.WHITE)) : (MutableText)this.text("\u041d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0434\u0440\u0443\u0433: ", Formatting.RED).append((Text)Text.literal(string4).formatted(Formatting.WHITE)));
            return;
        }
        if (string2.equals("list")) {
            List<String> list = FriendUtils.getFriendNames();
            if (list.isEmpty()) {
                this.logDirect("\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439 \u043f\u0443\u0441\u0442.", Formatting.YELLOW);
                return;
            }
            this.logDirect(this.text("\u0414\u0440\u0443\u0437\u044c\u044f (" + list.size() + "):", Formatting.AQUA));
            this.logDirect(Text.literal(String.join(", ", list)).formatted(Formatting.GRAY));
            return;
        }
        if (string2.equals("clear")) {
            int n = FriendUtils.clear();
            this.logDirect("\u0423\u0434\u0430\u043b\u0435\u043d\u043e \u0434\u0440\u0443\u0437\u0435\u0439: " + n, n > 0 ? Formatting.GREEN : Formatting.YELLOW);
            return;
        }
        this.usage();
    }

    private MutableText text(String string, Formatting formatting) {
        return Text.literal(string).formatted(formatting);
    }

    @Override
    public Stream<String> tabComplete(String string, String[] stringArray) {
        if (stringArray.length == 1) {
            String prefix = stringArray[0] == null ? "" : stringArray[0].toLowerCase(Locale.ROOT);
            return Stream.of("add", "del", "list", "clear").filter(s -> s.startsWith(prefix));
        }
        if (stringArray.length == 2 && (stringArray[0].equalsIgnoreCase("add") || stringArray[0].equalsIgnoreCase("del"))) {
            ArrayList<String> arrayList = new ArrayList<String>();
            if (this.mc.getNetworkHandler() != null) {
                for (net.minecraft.client.network.PlayerListEntry entry : this.mc.getNetworkHandler().getPlayerList()) {
                    String name = entry.getProfile().name();
                    if (name != null) {
                        arrayList.add(name);
                    }
                }
            }
            String lower = stringArray[1] == null ? "" : stringArray[1].toLowerCase(Locale.ROOT);
            Stream<String> stream = arrayList.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower));
            if (stringArray[0].equalsIgnoreCase("del")) {
                stream = Stream.concat(FriendUtils.getFriendNames().stream(), stream).distinct();
            }
            return stream;
        }
        return Stream.empty();
    }
}
