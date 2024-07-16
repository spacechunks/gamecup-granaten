package space.chunks.gamecup.dgr.map.object.impl.animation;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public abstract class AbstractAnimation<C extends MapObjectConfigEntry> extends AbstractBindableMapObject<C> implements Animation {
  private final UUID contextId;

  public AbstractAnimation() {
    this.contextId = UUID.randomUUID();
  }

  public AbstractAnimation(@NotNull UUID contextId) {
    this.contextId = contextId;
  }

  @Override
  public @NotNull UUID contextId() {
    return this.contextId;
  }
}
