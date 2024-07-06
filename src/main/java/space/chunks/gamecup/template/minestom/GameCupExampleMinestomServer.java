package space.chunks.gamecup.template.minestom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;

public class GameCupExampleMinestomServer {

    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        String onlineModeEnv = System.getenv("OFFLINE_MODE");
        if (onlineModeEnv == null || onlineModeEnv.equals("false")) {
            MojangAuth.init();
        }

        minecraftServer.start("0.0.0.0", 25565);
    }

}
