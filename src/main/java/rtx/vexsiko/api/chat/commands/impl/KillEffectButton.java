package rtx.vexsiko.api.chat.commands.impl;

import rtx.vexsiko.api.chat.commands.Command;
import rtx.vexsiko.api.modules.impl.Visuals.KillEffect;

public class KillEffectButton extends Command {
    public KillEffectButton() {
        super("killeffectbtn", "Вручную запустить эффект убийства (кнопка)");
    }

    @Override
    public void execute(String var1, String[] var2) {
        KillEffect effect = KillEffect.getInstanceIfReady();
        if (effect == null || !effect.isEnabled()) {
            this.logDirect("KillEffect выключен или не загружен.");
            return;
        }
        effect.triggerTestEffect();
        this.logDirect("Эффект убийства запущен вручную.");
    }
}
