package rtx.vexsiko.utils.storage.alt;
import java.util.ArrayList;
import java.util.List;
import rtx.vexsiko.api.mods.acountswiher.IasService;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.account.Account;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.account.OfflineAccount;
import rtx.vexsiko.api.mods.acountswiher.ru.vidtu.ias.auth.LoginData;
import rtx.vexsiko.utils.session.SessionChanger;

public final class AltManager {
    private static volatile String activeAlt = null;

    private AltManager() {
    }

    public static List<String> getAlts() {
        ArrayList<String> names = new ArrayList<String>();
        for (Account account : IasService.accounts()) {
            if (!(account instanceof OfflineAccount)) continue;
            names.add(account.name());
        }
        return names;
    }

    public static boolean add(String name) {
        if (name == null || (name = normalize(name)) == null) {
            return false;
        }
        for (String existing : getAlts()) {
            if (existing.equalsIgnoreCase(name)) {
                return false;
            }
        }
        IasService.addOffline(name);
        return true;
    }

    public static boolean remove(String name) {
        if (name == null || (name = normalize(name)) == null) {
            return false;
        }
        boolean bl = false;
        for (Account account : new ArrayList<Account>(IasService.accounts())) {
            if (!(account instanceof OfflineAccount) || !account.name().equalsIgnoreCase(name)) continue;
            IasService.remove((Account)((Object)account));
            bl = true;
        }
        return bl;
    }

    public static String getActiveAlt() {
        return activeAlt;
    }

    public static boolean login(String name) {
        if (name == null || (name = normalize(name)) == null) {
            return false;
        }
        try {
            SessionChanger.applyLoginData(new LoginData(name, OfflineAccount.uuid(name), "ias:offline", false));
            add(name);
            activeAlt = name;
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static String normalize(String string) {
        String string2 = string.replace("\u00a7", "").trim();
        return string2.isEmpty() ? null : string2;
    }
}
