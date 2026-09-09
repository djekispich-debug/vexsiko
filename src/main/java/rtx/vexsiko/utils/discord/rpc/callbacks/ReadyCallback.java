package rtx.vexsiko.utils.discord.rpc.callbacks;
import com.sun.jna.Callback;
import rtx.vexsiko.utils.discord.rpc.utils.DiscordUser;

public interface ReadyCallback
extends Callback {
    public void apply(DiscordUser var1);
}

