package rtx.vexsiko.stolen.noise;

public class NoiseAnimation {

    private float progress = 1f;
    private float linear = 1f;
    private long lastUpdate = System.nanoTime();

    public void update(float speed, boolean toShow) {
        long now = System.nanoTime();
        float dt = (now - lastUpdate) / 1_000_000_000f;
        lastUpdate = now;
        if (dt <= 0f) return;
        dt = Math.min(dt, 0.25f);

        float target = toShow ? 0f : 1f;

        if (Noise.animType.is("Default")) {
            float duration = Math.max(0.05f, (0.23f - speed) * 5f);
            float step = dt / duration;
            linear = target > linear ? Math.min(target, linear + step) : Math.max(target, linear - step);
            progress = easeInOutQuad(linear);
        } else {
            float factor = 1f - (float) Math.pow(1f - clamp(speed), dt * 60f);
            progress += (target - progress) * factor;
            linear = progress;
        }

        if (target == 1f && progress > 0.9980392f) progress = 1f;
        else if (target == 0f && progress < 0.003921569f) progress = 0f;
    }

    public float getReveal() {
        float shown = 1f - progress;
        return clamp(shown * shown / (REVEAL_HEADROOM * REVEAL_HEADROOM));
    }

    private static final float REVEAL_HEADROOM = 0.88f;

    public float getNoiseProgress() {
        return progress;
    }

    public boolean hasFinished() {
        return progress == 0f || progress == 1f;
    }

    public void reset() {
        progress = 1f;
        linear = 1f;
        lastUpdate = System.nanoTime();
    }

    private static float easeInOutQuad(float t) {
        return t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f;
    }

    private static float clamp(float v) {
        return v < 0f ? 0f : Math.min(v, 1f);
    }
}
