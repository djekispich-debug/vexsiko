package rtx.vexsiko.api.modules.impl.Visuals.emotions;
import rtx.vexsiko.api.modules.impl.Visuals.emotions.Emotion;

public interface EmotionStateHolder {
    public float vexsiko_getEmotionTime();

    public void vexsiko_setEmotion(Emotion var1, float var2, float var3);

    public Emotion vexsiko_getEmotion();

    public float vexsiko_getEmotionWeight();
}

