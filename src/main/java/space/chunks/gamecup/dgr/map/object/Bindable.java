package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;


/**
 * Binds {@link MapObject map objects} to other map objects. When the target is unregistered or changed in any way, the bound object will be notified of their changes.
 *
 * @author Nico_ND1
 */
public interface Bindable {
  void bind(@Nullable MapObject target);

  @Nullable
  MapObject boundTarget();

  void handleTargetRegister(@NotNull Map parent);

  void handleTargetUnregister(@NotNull Map parent);
}
