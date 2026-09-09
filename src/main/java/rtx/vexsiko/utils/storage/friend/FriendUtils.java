package rtx.vexsiko.utils.storage.friend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import rtx.vexsiko.VexSiko;
import rtx.vexsiko.utils.storage.RepositoryStorage;

public final class FriendUtils {
    private static final Gson GSON = new Gson();
    private static final Set<String> FRIENDS = new ConcurrentSkipListSet<String>(Comparator.comparing(s -> s.toLowerCase(Locale.ROOT)));
    private static volatile boolean loaded = false;
    private static volatile boolean enabled = true;

    private FriendUtils() {
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (FriendUtils.class) {
            if (loaded) {
                return;
            }
            Path path = file();
            try {
                if (Files.exists(path)) {
                    JsonObject object = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
                    if (object.has("enabled")) {
                        enabled = object.get("enabled").getAsBoolean();
                    }
                    if (object.has("friends")) {
                        for (JsonElement element : object.getAsJsonArray("friends")) {
                            String name = element.getAsString().trim();
                            if (!name.isEmpty()) {
                                FRIENDS.add(name);
                            }
                        }
                    }
                }
            }
            catch (Exception exception) {
                VexSiko.LOGGER.error("[Friends] Failed to load friends.json", (Throwable)exception);
            }
            loaded = true;
        }
    }

    private static Path file() {
        return RepositoryStorage.configRoot().resolve("friends.json");
    }

    public static void save() {
        synchronized (FriendUtils.class) {
            JsonObject object = new JsonObject();
            object.addProperty("enabled", Boolean.valueOf(enabled));
            JsonArray array = new JsonArray();
            for (String name : FRIENDS) {
                array.add((JsonElement)new Gson().toJsonTree(name));
            }
            object.add("friends", array);
            try {
                Files.createDirectories(file().getParent(), new FileAttribute[0]);
                Files.writeString(file(), GSON.toJson(object), StandardCharsets.UTF_8, new OpenOption[0]);
            }
            catch (IOException exception) {
                VexSiko.LOGGER.error("[Friends] Failed to save friends.json", (Throwable)exception);
            }
        }
    }

    public static boolean isEnabled() {
        ensureLoaded();
        return enabled;
    }

    public static void setEnabled(boolean bl) {
        ensureLoaded();
        enabled = bl;
        save();
    }

    public static List<String> getFriendNames() {
        ensureLoaded();
        return Collections.unmodifiableList(new ArrayList<String>(FRIENDS));
    }

    public static List<String> friends() {
        return getFriendNames();
    }

    public static boolean isFriend(String string) {
        if (string == null || !isEnabled()) {
            return false;
        }
        ensureLoaded();
        return FRIENDS.contains(string.trim());
    }

    public static boolean add(String string) {
        if (string == null || (string = normalize(string)) == null) {
            return false;
        }
        ensureLoaded();
        boolean bl = FRIENDS.add(string);
        if (bl) {
            save();
        }
        return bl;
    }

    public static boolean remove(String string) {
        if (string == null || (string = normalize(string)) == null) {
            return false;
        }
        ensureLoaded();
        boolean bl = FRIENDS.remove(string);
        if (bl) {
            save();
        }
        return bl;
    }

    public static int clear() {
        ensureLoaded();
        int n = FRIENDS.size();
        FRIENDS.clear();
        if (n > 0) {
            save();
        }
        return n;
    }

    private static String normalize(String string) {
        if (string == null) {
            return null;
        }
        String string2 = string.replace("\u00a7", "").trim();
        return string2.isEmpty() ? null : string2;
    }
}
