package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;

import java.util.List;


/**
 * Binds {@link MapObject map objects} to other map objects. When the target is unregistered or changed in any way, the bound object will be notified of their changes.
 *
 * @author Nico_ND1
 */
public interface Bindable extends MapObject {
  void bind(@Nullable MapObject target);

  @Nullable
  MapObject boundTarget();

  /**
   * Returns a {@link List} of all {@link Bindable} that are bound to this object.
   */
  @NotNull
  List<Bindable> boundObjects();

  void handleTargetRegister(@NotNull Map parent);

  void handleTargetUnregister(@NotNull Map parent);
}
