package space.chunks.gamecup.dgr.map.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * @author Nico_ND1
 */
public abstract class AbstractMapObject implements MapObject {
  protected @Nullable MapObjectConfigEntry config;
  private String name;

  public AbstractMapObject(@NotNull String name) {
    this.name = name;
  }

  public AbstractMapObject() {
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
    this.config = config;
    this.name = config.name();
  }

  @Override
  public @NotNull String name() {
    if (this.name == null) {
      return "unnamed:"+this.getClass().getSimpleName();
    }
    return this.name;
  }
}
