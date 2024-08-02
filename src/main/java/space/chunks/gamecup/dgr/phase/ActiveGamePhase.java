package space.chunks.gamecup.dgr.phase;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityPotionAddEvent;
import net.minestom.server.event.entity.EntityPotionRemoveEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game.EndReason;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.minestom.listener.PotionAddListener;
import space.chunks.gamecup.dgr.minestom.listener.PotionRemoveListener;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard.ForceUpdateEvent;


/**
 * @author Nico_ND1
 */
public class ActiveGamePhase extends AbstractPhase {
  public ActiveGamePhase() {
    addListener(EventListener.of(EntityPotionAddEvent.class, new PotionAddListener()));
    addListener(EventListener.of(EntityPotionRemoveEvent.class, new PotionRemoveListener()));
  }

  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {
    for (Team team : this.game.teams()) {
      for (Member member : team.members()) {
        Player player = member.player();
        player.sendMessage(Component.text("Game is starting...").color(NamedTextColor.GRAY));
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
