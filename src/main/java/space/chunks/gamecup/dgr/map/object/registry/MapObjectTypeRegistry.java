package space.chunks.gamecup.dgr.map.object.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * Type registry to create new {@link MapObject map objects} by their type, which are defined in {@link space.chunks.gamecup.dgr.map.MapModule}.
 *
 * @author Nico_ND1
 */
public interface MapObjectTypeRegistry {
  @NotNull
  MapObject create(@NotNull String type);

  @NotNull
  MapObject create(@NotNull String type, @Nullable MapObjectConfigEntry config);
}
