package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;


/**
 * @author Nico_ND1
 */
public interface Ticking {
  @NotNull
  TickResult tick(@NotNull Map map, int currentTick);

  default int priority() {
    return defaultPriority();
  }

  static int defaultPriority() {
    return 0;
  }

  enum TickResult {
    CONTINUE,
    UNREGISTER
  }
}
