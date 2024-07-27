package space.chunks.gamecup.dgr.phase;

import com.google.inject.Inject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;


/**
 * @author Nico_ND1
 */
public class EndingPhase extends AbstractPhase {
  private Instance spawningInstance;
  private int startTicks = 10;

  @Inject
  public EndingPhase(@NotNull GameFactory factory) {
  }

  @Override
  public void tick(int currentTick) {
    if (currentTick % 20 != 0) {
      return;
    }

    if (this.startTicks > 0) {
      this.startTicks--;

      if (this.startTicks == 0) {
        MinecraftServer.stopCleanly();
        System.exit(0);
      }
    }
  }

  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {
    this.spawningInstance = MinecraftServer.getInstanceManager().createInstanceContainer();

    for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
      onlinePlayer.setInstance(this.spawningInstance, new Pos(0, 100, 0));
    }
  }

  @Override
  protected void handleQuit_(@NotNull Phase followingPhase) {
  }

  @Override
  public @NotNull String name() {
    return "end";
  }
}
