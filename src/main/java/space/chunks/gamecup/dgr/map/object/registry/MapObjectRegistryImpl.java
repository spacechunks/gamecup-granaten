package space.chunks.gamecup.dgr.map.object.registry;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public final class MapObjectRegistryImpl implements MapObjectRegistry {
  private final Map parent;
  private final Set<MapObject> objects;

  @AssistedInject
  public MapObjectRegistryImpl(@Assisted Map parent) {
    this.parent = parent;
    this.objects = new HashSet<>();
  }

  @Override
  public synchronized boolean add(@NotNull MapObject object) {
    return this.objects.add(object);
  }

  @Override
  public synchronized boolean remove(@NotNull MapObject object) {
    return this.objects.remove(object);
  }

  @Override
  public synchronized @NotNull Set<MapObject> all() {
    return this.objects;
  }
}
