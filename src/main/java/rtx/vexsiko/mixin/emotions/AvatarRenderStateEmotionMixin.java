package rtx.vexsiko.mixin.emotions;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.vexsiko.api.modules.impl.Visuals.emotions.Emotion;
import rtx.vexsiko.api.modules.impl.Visuals.emotions.EmotionStateHolder;

@Mixin(net.minecraft.client.render.entity.state.PlayerEntityRenderState.class)

public abstract class AvatarRenderStateEmotionMixin
implements EmotionStateHolder {
    @Unique
    private Emotion vexsiko_emotion;
    @Unique
    private float vexsiko_emotionTime;
    @Unique
    private float vexsiko_emotionWeight;

    @Override
    public Emotion vexsiko_getEmotion() {
        return this.vexsiko_emotion;
    }

    @Override
    public float vexsiko_getEmotionTime() {
        return this.vexsiko_emotionTime;
    }

    @Override
    public float vexsiko_getEmotionWeight() {
        return this.vexsiko_emotionWeight;
    }

    @Override
    public void vexsiko_setEmotion(Emotion emotion, float time, float weight) {
        this.vexsiko_emotion = emotion;
        this.vexsiko_emotionTime = time;
        this.vexsiko_emotionWeight = weight;
    }
}

