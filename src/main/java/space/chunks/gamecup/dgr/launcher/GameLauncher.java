package space.chunks.gamecup.dgr.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.GameModule;


/**
 * @author Nico_ND1
 */
public final class GameLauncher {
  public static void main(String[] args) {
    launchServer();

    Injector injector = Guice.createInjector(
        new GameModule()
    );

    Game game = injector.getInstance(Game.class);
    game.launch();
  }

  private static void launchServer() {
    MinecraftServer minecraftServer = MinecraftServer.init();

    String onlineModeEnv = System.getenv("OFFLINE_MODE");
    if (onlineModeEnv == null || onlineModeEnv.equals("false")) {
      MojangAuth.init();
    }

    minecraftServer.start("0.0.0.0", 25565);
  }
}
