package space.chunks.gamecup.dgr.map.object.registry;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Groupable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Nico_ND1
 */
public interface MapObjectRegistry {
  @NotNull
  Map parent();

  /**
   * See {@link Set#add(Object)}
   */
  boolean add(@NotNull MapObject object);

  /**
   * See {@link Set#remove(Object)}
   */
  boolean remove(@NotNull MapObject object);

  @NotNull
  Set<MapObject> all();

  @NotNull
  default <T extends MapObject> Set<T> allOfType(@NotNull Class<T> clazz) {
    return (Set<T>) all().stream().filter(clazz::isInstance).collect(Collectors.toSet());
  }

  @NotNull
  default Optional<MapObject> find(@NotNull String name) {
    return all().stream().filter(object -> object.name().equals(name)).findAny();
  }

  @NotNull
  default Set<MapObject> allOfGroup(@NotNull String group) {
    return all().stream().filter(object -> object instanceof Groupable g && g.group().equals(group)).collect(Collectors.toSet());
  }
}
