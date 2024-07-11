package space.chunks.gamecup.dgr.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Ticking;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.team.Team;


/**
 * We will need multiple maps. Each team has their own map where they play. When any player joins a map they will be assigned their scoreboard and will view their chat messages, bossbar (tracking
 * progress), ...
 *
 * @author Nico_ND1
 */
public interface Map extends Ticking {
  @NotNull
  Team owner();

  @NotNull
  MapObjectRegistry objects();

  /**
   * Adds the given {@code mapObject} to a queue to be registered in the next tick.
   *
   * @param mapObject Map object to queue
   */
  void queueMapObjectRegister(@NotNull MapObject mapObject);

  /**
   * Adds the given {@code mapObject} to a queue to be unregistered in the next tick.
   *
   * @param mapObject Map object to queue
   */
  void queueMapObjectUnregister(@NotNull MapObject mapObject, @Nullable MapObject.UnregisterReason reason);

  default void queueMapObjectUnregister(@NotNull MapObject mapObject) {
    queueMapObjectUnregister(mapObject, MapObject.UnregisterReason.UNKNOWN);
  }
}
