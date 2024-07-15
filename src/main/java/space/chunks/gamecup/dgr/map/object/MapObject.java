package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * Interface for all objects that can be present in the map. This may include entities, blocks, markers, etc.
 * <p>
 * A map object can be registered in {@link space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry}.
 * </p>
 *
 * @author Nico_ND1
 */
public interface MapObject extends Named {
  void config(@NotNull MapObjectConfigEntry config);

  void handleRegister(@NotNull Map parent);

  void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason);

  enum UnregisterReason {
    UNKNOWN,
    TICK,
    BINDING,
    INCIDENT_RESOLVED,
    INCIDENT_NEW_ANIMATION,
    PASSENGER_NO_PROCEDURE_TARGET,
  }
}
