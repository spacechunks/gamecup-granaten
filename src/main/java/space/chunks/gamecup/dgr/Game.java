package space.chunks.gamecup.dgr;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.goal.GameGoal;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandler;
import space.chunks.gamecup.dgr.team.Team;

import java.util.Collection;
import java.util.Optional;


/**
 * The main game class which handles phases, players, teams and maps.
 *
 * @author Nico_ND1
 */
public interface Game extends Ticking {
  void launch();

  @NotNull
  PhaseHandler phases();

  @NotNull
  Collection<Map> maps();

  @NotNull
  Collection<Team> teams();

  default @NotNull Optional<Team> findTeam(@NotNull Player player) {
    return teams().stream().filter(team -> team.members().stream().anyMatch(member -> member.uuid().equals(player.getUuid()))).findAny();
  }

  @NotNull
  GameGoal goal();

  void end(@Nullable Team winnerTeam, @NotNull EndReason reason);

  enum EndReason {
    GOAL_REACHED,
    TIME_OVER,
    OTHER
  }
}
