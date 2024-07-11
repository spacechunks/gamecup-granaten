package space.chunks.gamecup.dgr.map.object.registry;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public final class MapObjectTypeRegistryImpl implements MapObjectTypeRegistry {
  private final Map<String, Provider<MapObject>> knownObjectTypes;

  @Inject
  public MapObjectTypeRegistryImpl(@NotNull Map<String, Provider<MapObject>> knownObjectTypes) {
    this.knownObjectTypes = knownObjectTypes;
  }

  @Override
  public @NotNull MapObject create(@NotNull String type) {
    Provider<MapObject> mapObjectProvider = this.knownObjectTypes.get(type);
    if (mapObjectProvider == null) {
      throw new NullPointerException("Unknown object type: "+type);
    }
    return mapObjectProvider.get();
  }

  @Override
  public @NotNull MapObject create(@NotNull String type, @Nullable MapObjectConfigEntry config) {
    if (config == null) {
      config = MapObjectConfigEntry.EMPTY;
    }

    MapObject mapObject = create(type);
    mapObject.config(config);
    return mapObject;
  }
}
