package space.chunks.gamecup.dgr.phase;

import com.google.inject.Inject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
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
  private Instance spawningInstance;
  private int startTicks;

  @Inject
  public WaitingPhase(@NotNull GameFactory factory) {
    addListener(EventListener.of(AsyncPlayerConfigurationEvent.class, event -> {
      event.setSpawningInstance(this.spawningInstance);

      Member member = factory.createMember(event.getPlayer());
      Team team = this.game.teams().stream().min(Comparator.comparingInt(team2 -> team2.members().size())).orElseThrow();
      member.assignTeam(team);

      if (this.startTicks == 0) {
        this.startTicks = 16;
      }
    }));

    addListener(EventListener.of(PlayerDisconnectEvent.class, event -> {
      this.game.findTeam(event.getPlayer()).ifPresent(team -> {
        team.removeMember(event.getPlayer().getUuid());
      });
    }));
  }

  @Override
  public void tick(int currentTick) {
    if (currentTick % 20 != 0) {
      return;
    }

    if (this.startTicks > 0) {
      this.startTicks--;

      if (this.startTicks == 0) {
        this.game.phases().enterPhase(ActiveGamePhase.class);
      }
    }
  }

  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {
    this.spawningInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
  }

  @Override
  protected void handleQuit_(@NotNull Phase followingPhase) {
    MinecraftServer.getSchedulerManager().scheduleTask(() -> {
      MinecraftServer.getInstanceManager().unregisterInstance(this.spawningInstance);
    }, TaskSchedule.tick(50), TaskSchedule.stop());
  }

  @Override
  public @NotNull String name() {
    return "waiting";
  }
}
