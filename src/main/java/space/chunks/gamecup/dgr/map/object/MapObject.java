package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * Interface for all objects that can be present in the map. This may include entities, blocks, markers, etc.
 * <p>
 * A map object can be registered in {@link space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry}.
 * <p>
 * All map objects that don't implement {@link ShoppingPhasePersistent} will be unregistered whenever the {@link space.chunks.gamecup.dgr.phase.ShoppingPhase} starts.
 * </p>
 *
 * @author Nico_ND1
 */
public interface MapObject extends Named {
  void config(@NotNull MapObjectConfigEntry config);

  void handleRegister(@NotNull Map parent);

  void handleUnregister(@NotNull Map parent);

  enum UnregisterReason {
    UNKNOWN,
    TICK,
    BINDING,
    INCIDENT_REMOVE_SUCCESS,
    INCIDENT_REMOVE_FAIL_TIME,
    INCIDENT_REMOVE_FAIL_OTHER
  }
}
