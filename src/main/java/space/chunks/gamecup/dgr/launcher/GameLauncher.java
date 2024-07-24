package space.chunks.gamecup.dgr.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.GameModule;
import space.chunks.gamecup.dgr.launcher.profiler.ApplicationProfiler;
import space.chunks.gamecup.dgr.launcher.profiler.ProfilerModule;


/**
 * @author Nico_ND1
 */
public final class GameLauncher {
  public static void main(String[] args) {
    launchServer();

    Injector injector = Guice.createInjector(
        new GameModule(),
        new ProfilerModule()
    );

    ApplicationProfiler applicationProfiler = injector.getInstance(ApplicationProfiler.class);
    applicationProfiler.register();

    Game game = injector.getInstance(Game.class);
    game.launch();
  }

  private static void launchServer() {
    MinecraftServer minecraftServer = MinecraftServer.init();

    String onlineModeEnv = System.getenv("ONLINE_MODE");
    if (onlineModeEnv != null && onlineModeEnv.equals("true")) {
      MojangAuth.init();
    }

    String velocitySecret = System.getenv("VELOCITY_SECRET");
    if (velocitySecret != null) {
      VelocityProxy.enable(velocitySecret);
    }

    minecraftServer.start("0.0.0.0", 25565);
  }
}
