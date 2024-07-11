package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Nico_ND1
 */
public abstract class AbstractBindableMapObject extends AbstractMapObject implements MapObject, Bindable {
  private MapObject target;

  public AbstractBindableMapObject(@NotNull String name) {
    super(name);
  }

  public AbstractBindableMapObject() {
    super();
  }

  @Override
  public synchronized void bind(@Nullable MapObject target) {
    this.target = target;
  }

  @Override
  public synchronized @Nullable MapObject boundTarget() {
    return this.target;
  }
}
