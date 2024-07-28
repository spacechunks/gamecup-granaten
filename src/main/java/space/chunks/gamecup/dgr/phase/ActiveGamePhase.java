package space.chunks.gamecup.dgr.phase;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.entity.ai.EntityAIGroup;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.pathfinding.Navigator;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityPotionAddEvent;
import net.minestom.server.event.entity.EntityPotionRemoveEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game.EndReason;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.minestom.listener.PotionAddListener;
import space.chunks.gamecup.dgr.minestom.listener.PotionRemoveListener;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard.ForceUpdateEvent;

import java.util.Objects;


/**
 * @author Nico_ND1
 */
public class ActiveGamePhase extends AbstractPhase {
  public ActiveGamePhase() {
    addListener(EventListener.of(EntityPotionAddEvent.class, new PotionAddListener()));
    addListener(EventListener.of(EntityPotionRemoveEvent.class, new PotionRemoveListener()));

    addListener(EventListener.of(PlayerEntityInteractEvent.class, event -> {
      if (event.getHand() != Hand.MAIN) {
        return;
      }

      for (Map map : this.game.maps()) {
        for (Passenger passenger : map.objects().allOfType(Passenger.class)) {
          NPCEntity passengerEntity = passenger.entityUnsafe();
          if (passengerEntity.getUuid().equals(event.getTarget().getUuid())) {
            Player player = event.getPlayer();
            player.sendMessage(Component.newline().append(Component.text("Passenger: ").color(NamedTextColor.GRAY)).append(Component.text(passenger.name()).color(NamedTextColor.BLUE)));
            player.sendMessage(Component.text("Patience: ").color(NamedTextColor.GRAY).append(Component.text(passenger.patience()).color(NamedTextColor.BLUE)));

            PassengerTask task = passenger.task();
            if (task != null) {
              player.sendMessage(Component.text("Task: ").color(NamedTextColor.GRAY)
                  .append(Component.text(task.state().name()).color(NamedTextColor.YELLOW))
                  .append(Component.text(" for ").color(NamedTextColor.GRAY))
                  .append(Component.text(task.procedureGroup()).color(NamedTextColor.BLUE)));
            }
            player.sendMessage(Component.text("Destination: ").color(NamedTextColor.GRAY).append(Component.text(passenger.destination().name()).color(NamedTextColor.BLUE)));

            ItemStack baggage = passenger.baggage();
            player.sendMessage(Component.text("Baggage: ").color(NamedTextColor.GRAY)
                .append(Component.text(baggage == null ? "null" : baggage.material().name()).color(NamedTextColor.BLUE)));

            Navigator navigator = passengerEntity.getNavigator();
            player.sendMessage(Component.text("Navigator State: ").color(NamedTextColor.GRAY)
                .append(Component.text(navigator.getState().name()).color(NamedTextColor.YELLOW)));
            player.sendMessage(Component.text("Navigator Goal: ").color(NamedTextColor.GRAY)
                .append(Component.text(Objects.toString(navigator.getGoalPosition(), "/")).color(NamedTextColor.BLUE)));

            int groupCount = 0;
            for (EntityAIGroup aiGroup : passengerEntity.getAIGroups()) {
              GoalSelector currentGoalSelector = aiGroup.getCurrentGoalSelector();
              if (currentGoalSelector != null) {
                player.sendMessage(Component.text("Goal selector ("+(++groupCount)+"): ").color(NamedTextColor.GRAY)
                    .append(Component.text(currentGoalSelector.getClass().getName()).color(NamedTextColor.BLUE)));
              } else {
                player.sendMessage(Component.text("Goal selector: ").color(NamedTextColor.GRAY)
                    .append(Component.text("/").color(NamedTextColor.BLUE)));
              }
            }
            return;
          }
        }
      }
    }));
  }

  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {
    for (Team team : this.game.teams()) {
      for (Member member : team.members()) {
        Player player = member.player();
        player.sendMessage("Game is starting...");
        player.setInstance(team.map().instance(), new Pos(-47.5, -56.0, -10.5, -90, 0));
        player.setGameMode(GameMode.ADVENTURE);

        team.map().queueMapObjectRegister(new MemberScoreboard(this.game, member));

        this.game.goal().showTitle(player);
      }

      team.map().queueMapObjectRegister(team);
    }
  }

  @Override
  protected void handleQuit_(@NotNull Phase followingPhase) {

  }

  @Override
  public @NotNull String name() {
    return "active-game";
  }

  @Override
  public void tick(int currentTick) {
    for (Map map : this.game.maps()) {
      map.tick(currentTick);
    }

    this.game.goal().tick(currentTick);

    if (currentTick % 10 == 0) {
      for (Team team : this.game.teams()) {
        MinecraftServer.getGlobalEventHandler().call(new ForceUpdateEvent(team));
      }
    }

    if (MinecraftServer.getConnectionManager().getOnlinePlayerCount() == 0) {
      this.game.end(null, EndReason.OTHER);
    }
  }
}
