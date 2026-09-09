package rtx.vexsiko.api.ui.social;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import rtx.vexsiko.api.drags.Position;
import rtx.vexsiko.api.ui.theme.AccentGradient;
import rtx.vexsiko.api.ui.theme.ClientAccent;
import rtx.vexsiko.utils.color.ColorUtil;
import rtx.vexsiko.utils.render.fonts.Fonts;
import rtx.vexsiko.utils.render.others.RectUtil;
import rtx.vexsiko.utils.render.render2d.Render2D;
import rtx.vexsiko.utils.storage.friend.FriendUtils;

public final class SocialRenderer {
    public static final int ONLINE = 0;
    public static final int FRIENDS = 1;
    private static final String FONT = "montserrat-medium";
    private static final float ROW_H = 22.0f;
    private static final int TEXT = -1;
    private static final int DIM = -4144960;
    private static final int FRIEND_COLOR = -10177546;
    private static final float CONTENT_X_OFF = 117.0f;
    private static final float CONTENT_Y_OFF = 5.0f;
    private static final float CONTENT_W_OFF = 122.0f;
    private static final float CONTENT_H = 280.0f;
    private int mode = ONLINE;
    private float scrollOffset;
    private float scrollTarget;
    private final List<Entry> entries = new ArrayList<Entry>();
    private boolean entriesValid;
    private float lastX;
    private float lastY;

    public void open() {
        this.scrollTarget = 0.0f;
        this.scrollOffset = 0.0f;
        this.entriesValid = false;
    }

    public void showEvents() {
        this.setMode(ONLINE);
    }

    public void showMines() {
        this.setMode(FRIENDS);
    }

    public boolean isMines() {
        return this.mode == FRIENDS;
    }

    private void setMode(int n) {
        if (this.mode != n) {
            this.mode = n;
            this.scrollTarget = 0.0f;
            this.scrollOffset = 0.0f;
        }
        this.entriesValid = false;
    }

    private void rebuild() {
        this.entries.clear();
        if (this.mode == ONLINE) {
            MinecraftClient client = MinecraftClient.getInstance();
            try {
                if (client.getNetworkHandler() != null) {
                    for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
                        String name = entry.getProfile().name();
                        if (name == null || name.isEmpty()) continue;
                        this.entries.add(new Entry(name, FriendUtils.isFriend(name), entry.getLatency(), true));
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.entries.sort((a, b) -> {
                if (a.friend != b.friend) {
                    return a.friend ? -1 : 1;
                }
                return a.name.compareToIgnoreCase(b.name);
            });
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            java.util.Set<String> onlineNames = new java.util.HashSet<>();
            try {
                if (client != null && client.getNetworkHandler() != null) {
                    for (PlayerListEntry e : client.getNetworkHandler().getPlayerList()) {
                        String n = e.getProfile().name();
                        if (n != null) onlineNames.add(n.toLowerCase(java.util.Locale.ROOT));
                    }
                }
            } catch (Exception ignored) {}
            for (String name : FriendUtils.getFriendNames()) {
                boolean online = onlineNames.contains(name.toLowerCase(java.util.Locale.ROOT));
                this.entries.add(new Entry(name, true, Integer.MIN_VALUE, online));
            }
            this.entries.sort((a, b) -> {
                if (a.online != b.online) return a.online ? -1 : 1;
                return a.name.compareToIgnoreCase(b.name);
            });
        }
        this.entriesValid = true;
    }

    public void render(DrawContext context, float x, float y, float w, float h, float delta) {
        this.lastX = x;
        this.lastY = y;
        float cx = x + CONTENT_X_OFF;
        float cy = y + CONTENT_Y_OFF;
        float cw = w - CONTENT_W_OFF;
        float ch = CONTENT_H;
        if (!this.entriesValid) {
            this.rebuild();
        }
        int[] palette = ClientAccent.currentPalette();
        int accent = palette != null && palette.length > 0 ? palette[0] | 0xFF000000 : -2234369;
        String iconGlyph = this.mode == ONLINE ? "b" : "m";
        AccentGradient.msdfIcon("vexsiko", iconGlyph, cx + 14.0f, cy + 13.5f, 9.0f, 235.0f, 0.6f);
        String title = this.mode == ONLINE ? "Online players" : "Friends";
        Fonts.MONTSERRAT_MEDIUM.draw(title, cx + 30.0f, cy + 11.0f, 9.0f, -1);
        String hint;
        if (this.mode == ONLINE) {
            hint = this.entries.size() + " online";
        } else {
            long online = this.entries.stream().filter(e -> e.online).count();
            long offline = this.entries.size() - online;
            hint = online + " online \u2022 " + offline + " offline";
        }
        float hintW = Fonts.MONTSERRAT_MEDIUM.width(hint, 6.5f);
        Fonts.MONTSERRAT_MEDIUM.draw(hint, cx + cw - 14.0f - hintW, cy + 12.5f, 6.5f, DIM);
        Render2D.rect(cx + 12.0f, cy + 28.0f, cw - 24.0f, 1.0f, -16777216);
        float listTop = cy + 34.0f;
        float listH = ch - 40.0f;
        float maxScroll = Math.max(0.0f, (float)this.entries.size() * ROW_H - listH);
        this.scrollTarget = Math.max(0.0f, Math.min(this.scrollTarget, maxScroll));
        float f6 = 1.0f - (float)Math.exp(-delta * 14.0f);
        this.scrollOffset += (this.scrollTarget - this.scrollOffset) * f6;
        if (Math.abs(this.scrollTarget - this.scrollOffset) < 0.05f) {
            this.scrollOffset = this.scrollTarget;
        }
        if (this.entries.isEmpty()) {
            String empty = this.mode == ONLINE ? "Not connected to any server" : "No friends yet \u2014 .friend add <nick>";
            float ew = Fonts.MONTSERRAT_MEDIUM.width(empty, 7.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(empty, cx + (cw - ew) * 0.5f, listTop + listH * 0.42f, 7.5f, DIM);
            return;
        }
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        int start = (int)(this.scrollOffset / ROW_H);
        int end = Math.min(this.entries.size(), start + (int)(listH / ROW_H) + 2);
        for (int i = start; i < end; ++i) {
            Entry entry = this.entries.get(i);
            float ry = listTop + (float)i * ROW_H - this.scrollOffset;
            if (ry + ROW_H < listTop || ry > listTop + listH) continue;
            boolean hover = mouseX >= cx + 6.0f && mouseX <= cx + cw - 6.0f && mouseY >= ry && mouseY < ry + ROW_H - 3.0f;
            if (hover) {
                Render2D.rect(cx + 6.0f, ry, cw - 12.0f, ROW_H - 3.0f, 7.0f, ColorUtil.multAlpha(accent, 0.16f));
            } else if (i % 2 == 0) {
                Render2D.rect(cx + 6.0f, ry, cw - 12.0f, ROW_H - 3.0f, 7.0f, 1140850688);
            }
            boolean showDot = entry.friend || (this.mode == FRIENDS && entry.online);
            if (showDot) {
                int dotColor = this.mode == FRIENDS ? (entry.online ? FRIEND_COLOR : 0xFF888888) : FRIEND_COLOR;
                Render2D.circle(cx + 15.0f, ry + ROW_H / 2.0f - 1.5f, 1.4f, dotColor);
            }
            float nameX = cx + (entry.friend ? 23.0f : 15.0f);
            int nameColor = this.mode == FRIENDS ? (entry.online ? TEXT : DIM) : (entry.friend ? FRIEND_COLOR : TEXT);
            Fonts.MONTSERRAT_MEDIUM.draw(entry.name, nameX, ry + 6.5f, 7.5f, nameColor);
            if (this.mode == ONLINE) {
                if (!entry.friend && hover) {
                    String addText = "[+ Friend]";
                    float rx = cx + cw - 18.0f - Fonts.MONTSERRAT_MEDIUM.width(addText, 7.0f);
                    Fonts.MONTSERRAT_MEDIUM.draw(addText, rx, ry + 6.5f, 7.0f, accent);
                } else if (entry.ping != Integer.MIN_VALUE) {
                    String pingText = entry.ping + "ms";
                    int color = entry.ping < 80 ? FRIEND_COLOR : (entry.ping < 200 ? -256 : -65536);
                    Fonts.MONTSERRAT_MEDIUM.draw(pingText, cx + cw - 18.0f - Fonts.MONTSERRAT_MEDIUM.width(pingText, 7.0f), ry + 6.5f, 7.0f, color);
                }
            } else if (this.mode == FRIENDS && hover) {
                String removeText = "[remove]";
                float rx = cx + cw - 18.0f - Fonts.MONTSERRAT_MEDIUM.width(removeText, 7.0f);
                Fonts.MONTSERRAT_MEDIUM.draw(removeText, rx, ry + 6.5f, 7.0f, -65536);
            }
        }
        if (maxScroll > 0.0f) {
            float trackX = cx + cw - 7.0f;
            float barH = Math.max(26.0f, listH * (listH / ((float)this.entries.size() * ROW_H)));
            float barY = listTop + (listH - barH) * (this.scrollOffset / maxScroll);
            Render2D.rect(trackX, barY, 2.0f, barH, 1.0f, ColorUtil.multAlpha(accent, 0.55f));
        }
    }

    public boolean click(float mx, float my) {
        float cx = this.lastX + CONTENT_X_OFF;
        float cy = this.lastY + CONTENT_Y_OFF;
        float cw = 280.0f;
        float listTop = cy + 34.0f;
        if (mx < cx || mx > cx + cw) {
            return false;
        }
        if (my < listTop || my > listTop + CONTENT_H - 40.0f) {
            return false;
        }
        int index = (int)((my - listTop + this.scrollOffset) / ROW_H);
        if (index < 0 || index >= this.entries.size()) {
            return false;
        }
        Entry entry = this.entries.get(index);
        float rowTop = listTop + (float)index * ROW_H - this.scrollOffset;
        if (my < rowTop || my >= rowTop + ROW_H - 3.0f) {
            return false;
        }
        if (this.mode == ONLINE) {
            if (!entry.friend) {
                FriendUtils.add(entry.name);
                this.entriesValid = false;
                return true;
            }
            return false;
        } else {
            FriendUtils.remove(entry.name);
            this.entriesValid = false;
            return true;
        }
    }

    public void scroll(double amount, float unused) {
        this.scrollTarget -= (float)(amount * 18.0);
        float contentH = (float)this.entries.size() * ROW_H;
        float listH = CONTENT_H - 40.0f;
        this.scrollTarget = Math.max(0.0f, Math.min(this.scrollTarget, Math.max(0.0f, contentH - listH)));
    }

    public boolean scrollbarGrab(float f, float f2) {
        return false;
    }

    public void scrollbarRelease() {
    }

    public float currentScroll() {
        return this.scrollOffset;
    }

    public void finishTransition() {
    }

    public boolean isTransitioning() {
        return false;
    }

    private static final class Entry {
        final String name;
        final boolean friend;
        final int ping;
        final boolean online;

        Entry(String name, boolean friend, int ping, boolean online) {
            this.name = name;
            this.friend = friend;
            this.ping = ping;
            this.online = online;
        }
        Entry(String name, boolean friend, int ping) {
            this(name, friend, ping, friend);
        }
    }
}
