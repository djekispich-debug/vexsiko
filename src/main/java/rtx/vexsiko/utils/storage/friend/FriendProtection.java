package rtx.vexsiko.utils.storage.friend;
import net.minecraft.entity.player.PlayerEntity;
import rtx.vexsiko.api.events.EventBus;
import rtx.vexsiko.api.events.EventHandler;
import rtx.vexsiko.api.events.impl.player.AttackEntityEvent;

public final class FriendProtection {
    public FriendProtection() {
        EventBus.get().subscribe(this);
    }

    @EventHandler
    private void onAttack(AttackEntityEvent event) {
        if (event.isSynthetic()) {
            return;
        }
        if (event.getTarget() instanceof PlayerEntity player && FriendUtils.isFriend(player.getName().getString())) {
            event.cancel();
        }
    }
}
