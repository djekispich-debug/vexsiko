package rtx.vexsiko.api.modules.restrict;
import rtx.vexsiko.api.modules.restrict.Server;

public @interface ServerRule {
    public ServerRule.Mode mode();

    public Server[] servers();


    public static enum Mode {
        ONLY,
        BLOCK,
        HIDE;
    
    }
}

