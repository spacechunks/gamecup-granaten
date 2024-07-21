package space.chunks.gamecup.dgr.launcher.profiler;

import com.google.inject.Inject;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Game;

import java.util.List;
import java.util.Optional;


/**
 * @author Nico_ND1
 */
public class ProfilerRepositoryImpl implements ProfilerRepository {
  private final Game game;

  @Inject
  public ProfilerRepositoryImpl(Game game) {
    this.game = game;
  }

  @Override
  public @NotNull List<SessionProfiler> sessionProfilers() {
    return this.game.teams().stream().map(team -> team.map().profiler()).toList();
  }

  @Override
  public @NotNull Optional<SessionProfiler> findFor(@NotNull Player player) {
    return this.game.findTeam(player).map(team -> team.map().profiler());
  }
}
