package rtx.vexsiko.utils.color;

public final class ColorUtil {
    public static final int WHITE = -1;
    public static final int BLACK = -16777216;
    public static final int RED = -65536;
    public static final int GREEN = -16711936;
    public static final int BLUE = -16776961;

    private ColorUtil() {
    }

    private static int red(int n) {
        return n >>> 16 & 0xFF;
    }

    private static int normalize(int n) {
        if ((n & 0xFF000000) == 0 && (n & 0xFFFFFF) != 0) {
            return n | 0xFF000000;
        }
        return n;
    }

    public static int alpha(int n) {
        return n >>> 24 & 0xFF;
    }

    public static int lerpColor(int n, int n2, float f) {
        float f2 = Float.isFinite(f) ? Math.clamp(f, 0.0f, 1.0f) : 0.0f;
        int n3 = Math.round((float)ColorUtil.alpha(ColorUtil.normalize(n)) + (float)(ColorUtil.alpha(ColorUtil.normalize(n2)) - ColorUtil.alpha(ColorUtil.normalize(n))) * f2);
        int n4 = Math.round((float)ColorUtil.red(n) + (float)(ColorUtil.red(n2) - ColorUtil.red(n)) * f2);
        int n5 = Math.round((float)ColorUtil.green(n) + (float)(ColorUtil.green(n2) - ColorUtil.green(n)) * f2);
        int n6 = Math.round((float)ColorUtil.blue(n) + (float)(ColorUtil.blue(n2) - ColorUtil.blue(n)) * f2);
        return ColorUtil.rgba(n4, n5, n6, n3);
    }

    public static int withAlpha(int n, int n2) {
        return n & 0xFFFFFF | (n2 & 0xFF) << 24;
    }

    public static int multAlpha(int n, float f) {
        if (!Float.isFinite(f)) {
            f = 1.0f;
        }
        return ColorUtil.withAlpha(n, Math.clamp((long)Math.round((float)ColorUtil.alpha(ColorUtil.normalize(n)) * f), 0, 255));
    }

    public static int rgba(int n, int n2, int n3, int n4) {
        return (n4 & 0xFF) << 24 | (n & 0xFF) << 16 | (n2 & 0xFF) << 8 | n3 & 0xFF;
    }

    private static int blue(int n) {
        return n & 0xFF;
    }

    private static int green(int n) {
        return n >>> 8 & 0xFF;
    }

    public static int rgb(int n, int n2, int n3) {
        return ColorUtil.rgba(n, n2, n3, 255);
    }

    public static int hsvToRgb(float h, float s, float v, float a) {
        h = (h % 360f + 360f) % 360f;
        s = Math.clamp(s, 0f, 1f);
        v = Math.clamp(v, 0f, 1f);
        a = Math.clamp(a, 0f, 1f);
        int i = (int)(h / 60f);
        float f = h / 60f - i;
        float p = v * (1f - s);
        float q = v * (1f - f * s);
        float t = v * (1f - (1f - f) * s);
        int r, g, b;
        switch (i) {
            case 0: r = (int)(v * 255); g = (int)(t * 255); b = (int)(p * 255); break;
            case 1: r = (int)(q * 255); g = (int)(v * 255); b = (int)(p * 255); break;
            case 2: r = (int)(p * 255); g = (int)(v * 255); b = (int)(t * 255); break;
            case 3: r = (int)(p * 255); g = (int)(q * 255); b = (int)(v * 255); break;
            case 4: r = (int)(t * 255); g = (int)(p * 255); b = (int)(v * 255); break;
            default: r = (int)(v * 255); g = (int)(p * 255); b = (int)(q * 255); break;
        }
        return ColorUtil.rgba(r, g, b, (int)(a * 255));
    }
}

