package space.chunks.gamecup.dgr.phase;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;


/**
 * @author Nico_ND1
 */
public class ActiveGamePhase extends AbstractPhase {
  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {
    for (Team team : this.game.teams()) {
      for (Member member : team.members()) {
        member.player().sendMessage("Teleporting...");
        member.player().setInstance(team.map().instance(), new Pos(-47.5, -56.0, -10.5));
      }
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
  }
}
