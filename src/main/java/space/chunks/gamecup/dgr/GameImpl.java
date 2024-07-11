package space.chunks.gamecup.dgr;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandler;
import space.chunks.gamecup.dgr.team.Team;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public final class GameImpl implements Game {
  private final PhaseHandler phases;
  private final List<Map> maps;
  private final List<Team> teams;

  @Inject
  public GameImpl(@NotNull PhaseHandler phases, @NotNull GameConfig config, @NotNull GameFactory factory) {
    this.phases = phases;
    this.maps = new ArrayList<>();
    this.teams = createTeams(config, factory);
  }

  private @NotNull List<Team> createTeams(@NotNull GameConfig config, @NotNull GameFactory factory) {
    List<Team> teams = new ArrayList<>();
    for (int i = 0; i < config.teams(); i++) {
      Team team = factory.createTeam();
      teams.add(team);
    }
    return teams;
  }

  @Override
  public void launch() {

  }

  @Override
  public void tick(int currentTick) {
    this.phases.tick(currentTick);
  }
}
