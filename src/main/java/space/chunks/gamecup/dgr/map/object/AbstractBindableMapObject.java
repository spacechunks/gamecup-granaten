package space.chunks.gamecup.dgr.map.object;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.Collections;
import java.util.List;


/**
 * @author Nico_ND1
 */
public abstract class AbstractBindableMapObject<C extends MapObjectConfigEntry> extends AbstractMapObject<C> implements MapObject, Bindable {
  private MapObject target;
  private List<Bindable> boundObjects;

  public AbstractBindableMapObject(@NotNull String name) {
    super(name);
    this.boundObjects = Collections.synchronizedList(Lists.newCopyOnWriteArrayList());
  }

  public AbstractBindableMapObject() {
    super();
    this.boundObjects = Collections.synchronizedList(Lists.newCopyOnWriteArrayList());
  }

  @Override
  public synchronized void bind(@Nullable MapObject target) {
    this.target = target;

    if (target instanceof Bindable bindableTarget) {
      bindableTarget.boundObjects().add(this);
    }
  }

  @Override
  public synchronized @Nullable MapObject boundTarget() {
    return this.target;
  }

  @Override
  public @NotNull List<Bindable> boundObjects() {
    return this.boundObjects;
  }

  @Override
  public void handleTargetRegister(@NotNull Map parent) {
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
    this.target = null;
  }
}
