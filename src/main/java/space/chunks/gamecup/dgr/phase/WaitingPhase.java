package space.chunks.gamecup.dgr.phase;

import com.google.inject.Inject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;

import java.util.Comparator;


/**
 * @author Nico_ND1
 */
public class WaitingPhase extends AbstractPhase {
  @Inject
  public WaitingPhase(@NotNull GameFactory factory) {
    addListener(EventListener.of(AsyncPlayerConfigurationEvent.class, event -> {
      event.setSpawningInstance(MinecraftServer.getInstanceManager().createInstanceContainer());

      Member member = factory.createMember(event.getPlayer());
      Team team = this.game.teams().stream().min(Comparator.comparingInt(team2 -> team2.members().size())).orElseThrow();
      member.assignTeam(team);
    }));
  }

  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {

  }

  @Override
  protected void handleQuit_(@NotNull Phase followingPhase) {

  }

  @Override
  public @NotNull String name() {
    return "waiting";
  }

  @Override
  public void tick(int currentTick) {
  }
}
