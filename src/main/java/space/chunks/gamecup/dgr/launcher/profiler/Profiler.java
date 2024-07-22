package space.chunks.gamecup.dgr.launcher.profiler;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;


/**
 * @author Nico_ND1
 */
public interface Profiler<T> {
  Class<T> type();

  void feed(@NotNull T type);

  @NotNull ProfilingSnapshot snapshot(@NotNull Duration duration);
}
