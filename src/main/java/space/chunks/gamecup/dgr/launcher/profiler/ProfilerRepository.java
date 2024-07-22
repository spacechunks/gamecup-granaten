package space.chunks.gamecup.dgr.launcher.profiler;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;


/**
 * @author Nico_ND1
 */
public interface ProfilerRepository {
  @NotNull
  List<SessionProfiler> sessionProfilers();

  @NotNull
  Optional<SessionProfiler> findFor(@NotNull Player player);
}
