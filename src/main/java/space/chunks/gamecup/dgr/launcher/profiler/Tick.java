package space.chunks.gamecup.dgr.launcher.profiler;

import org.jetbrains.annotations.Nullable;


/**
 * @author Nico_ND1
 */
public interface Tick {
  long duration();

  @Nullable Long acquisitionTime();
}
