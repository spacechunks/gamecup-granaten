package space.chunks.gamecup.dgr.phase;

import com.google.inject.Inject;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;

import java.net.URI;
import java.util.Comparator;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class WaitingPhase extends AbstractPhase {
  private static final UUID RESOURCE_PACK_UUID = UUID.fromString("3ADE6EBA-1205-4907-8829-4EB5C522DAB4");

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

      event.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest()
          .prompt(Component.text("Use it!"))
          .callback((uuid, status, audience) -> {
          }).packs(ResourcePackInfo.resourcePackInfo(RESOURCE_PACK_UUID, URI.create("https://filebin.net/d3vr254qadlrt6m4/texture_pack__3_.zip"), "bc9e42b86fbdb7801d558dba88d5195b32af9639"))
          .asResourcePackRequest());
    }));

    addListener(EventListener.of(PlayerSpawnEvent.class, event -> {
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
