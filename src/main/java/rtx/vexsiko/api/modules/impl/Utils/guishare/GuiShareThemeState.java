package rtx.vexsiko.api.modules.impl.Utils.guishare;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import rtx.vexsiko.api.modules.impl.Interface.InterfaceModule;
import rtx.vexsiko.api.ui.theme.Theme;
import rtx.vexsiko.api.ui.theme.ThemeManager;

public record GuiShareThemeState(
    int mode,
    int[] shades,
    int[] palette,
    boolean movement,
    boolean usesSecond,
    int gradientStyleId,
    float rainbowSpeed,
    float rainbowSpread,
    float rainbowSaturation,
    float cornerRadius,
    float backdropBlur,
    float refraction,
    float edgeStrength,
    float edgeSharpness,
    boolean glow,
    float glowIntensity,
    float glowRadius,
    float gradientSweep,
    float gradientPrevStyle
) {
    public static final GuiShareThemeState DEFAULTS = new GuiShareThemeState(
        0, new int[]{0xFF8A5CF6, 0xFF7C3AED, 0xFFDDD6FE, 0xFFE9D5FF, 0xFFA78BFA, 0xFF8B5CF6, 0xFF6D28D9},
        new int[]{0xFF8A5CF6, 0xFF7C3AED}, false, false, 0,
        1.0f, 1.0f, 1.0f, 8.0f, 10.0f, 0.0f, 1.0f, 1.0f, false, 1.0f, 8.0f, 0.0f, 0.0f
    );

    public static GuiShareThemeState capture() {
        try {
            InterfaceModule im = InterfaceModule.getInstance();
            Theme cur = ThemeManager.current();
            int[] shades = cur != null ? cur.shades().clone() : DEFAULTS.shades.clone();
            int[] palette = cur != null ? cur.palette().clone() : DEFAULTS.palette.clone();
            if (im != null) {
                try { palette = im.clientPalette().clone(); } catch (Exception ignored) {}
                // shades from ClientAccent blended if custom/rainbow — fallback to theme shades
            }
            float cr = 8.0f, bb = 10.0f, refr = 0.0f, es = 1.0f, eh = 1.0f, gi = 1.0f, gr = 8.0f;
            boolean glow = false;
            int gradId = 0;
            float rs = 1.0f, rp = 1.0f, sat = 1.0f;
            boolean mov = false, usesSecond = false;
            if (im != null) {
                try { cr = im.rectCornerRadius.getFloat(); } catch (Exception ignored) {}
                try { bb = im.rectBackdropBlur.getFloat(); } catch (Exception ignored) {}
                try { refr = im.rectRefractionStrength.getFloat(); } catch (Exception ignored) {}
                try { es = im.rectEdgeStrength.getFloat(); } catch (Exception ignored) {}
                try { eh = im.rectEdgeSharpness.getFloat(); } catch (Exception ignored) {}
                try { glow = im.rectGlow.getValue(); } catch (Exception ignored) {}
                try { gi = im.rectGlowIntensity.getFloat(); } catch (Exception ignored) {}
                try { gr = im.rectGlowRadius.getFloat(); } catch (Exception ignored) {}
                try { gradId = im.gradientStyleId(); } catch (Exception ignored) {}
                try { mov = im.clientColorMovement(); } catch (Exception ignored) {}
                try { usesSecond = im.usesSecondClientColor(); } catch (Exception ignored) {}
                // rainbow fields available via im.rainbowSpeed etc if exists — try reflectively
                try { rs = ((Number)im.getClass().getField("rainbowSpeed").get(im)).floatValue(); } catch (Exception ignored) {}
                try { rp = ((Number)im.getClass().getField("rainbowSpread").get(im)).floatValue(); } catch (Exception ignored) {}
                try { sat = ((Number)im.getClass().getField("rainbowSaturation").get(im)).floatValue(); } catch (Exception ignored) {}
            }
            int mode = 0;
            if (im != null) {
                try { if (im.isThemeClientColor()) mode = 0; else if (im.isRainbowClientColor()) mode = 2; else if (im.isCustomClientColor()) mode = 1; } catch (Exception ignored) {}
            }
            return new GuiShareThemeState(mode, shades, palette, mov, usesSecond, gradId, rs, rp, sat, cr, bb, refr, es, eh, glow, gi, gr, 0.0f, 0.0f);
        } catch (Exception e) {
            return DEFAULTS;
        }
    }

    public static GuiShareThemeState fromJson(JsonObject json) {
        if (json == null || json.entrySet().isEmpty()) return DEFAULTS;
        try {
            int mode = json.has("mode") ? json.get("mode").getAsInt() : 0;
            int[] shades = readIntArray(json, "sh", DEFAULTS.shades);
            int[] palette = readIntArray(json, "pa", DEFAULTS.palette);
            boolean mov = json.has("mov") ? json.get("mov").getAsBoolean() : false;
            boolean usesSecond = json.has("us") ? json.get("us").getAsBoolean() : false;
            int gradId = json.has("gs") ? json.get("gs").getAsInt() : 0;
            float rs = json.has("rs") ? json.get("rs").getAsFloat() : 1.0f;
            float rp = json.has("rp") ? json.get("rp").getAsFloat() : 1.0f;
            float sat = json.has("sa") ? json.get("sa").getAsFloat() : 1.0f;
            float cr = json.has("cr") ? json.get("cr").getAsFloat() : 8.0f;
            float bb = json.has("bb") ? json.get("bb").getAsFloat() : 10.0f;
            float refr = json.has("re") ? json.get("re").getAsFloat() : 0.0f;
            float es = json.has("es") ? json.get("es").getAsFloat() : 1.0f;
            float eh = json.has("eh") ? json.get("eh").getAsFloat() : 1.0f;
            boolean glow = json.has("gw") ? json.get("gw").getAsBoolean() : false;
            float gi = json.has("gi") ? json.get("gi").getAsFloat() : 1.0f;
            float gr = json.has("gr") ? json.get("gr").getAsFloat() : 8.0f;
            return new GuiShareThemeState(mode, shades, palette, mov, usesSecond, gradId, rs, rp, sat, cr, bb, refr, es, eh, glow, gi, gr, 0, 0);
        } catch (Exception e) {
            return DEFAULTS;
        }
    }

    public void writeTo(JsonObject json) {
        if (json == null) return;
        json.addProperty("mode", mode);
        json.add("sh", toArray(shades));
        json.add("pa", toArray(palette));
        json.addProperty("mov", movement);
        json.addProperty("us", usesSecond);
        json.addProperty("gs", gradientStyleId);
        json.addProperty("rs", rainbowSpeed);
        json.addProperty("rp", rainbowSpread);
        json.addProperty("sa", rainbowSaturation);
        json.addProperty("cr", cornerRadius);
        json.addProperty("bb", backdropBlur);
        json.addProperty("re", refraction);
        json.addProperty("es", edgeStrength);
        json.addProperty("eh", edgeSharpness);
        json.addProperty("gw", glow);
        json.addProperty("gi", glowIntensity);
        json.addProperty("gr", glowRadius);
    }

    private static JsonArray toArray(int[] arr) {
        JsonArray a = new JsonArray();
        if (arr != null) for (int v : arr) a.add(v);
        return a;
    }
    private static int[] readIntArray(JsonObject json, String key, int[] fallback) {
        if (!json.has(key) || !json.get(key).isJsonArray()) return fallback;
        JsonArray arr = json.getAsJsonArray(key);
        int[] out = new int[arr.size()];
        for (int i=0;i<arr.size();i++) {
            JsonElement el = arr.get(i);
            try { out[i] = el.getAsInt(); } catch (Exception e) { out[i]=0; }
        }
        return out.length==0 ? fallback : out;
    }
}
