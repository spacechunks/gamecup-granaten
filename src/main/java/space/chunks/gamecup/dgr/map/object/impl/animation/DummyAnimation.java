package space.chunks.gamecup.dgr.map.object.impl.animation;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntryDefault;


/**
 * @author Nico_ND1
 */
public final class DummyAnimation extends AbstractAnimation<MapObjectConfigEntryDefault> implements Animation {
  @Override
  protected @NotNull Class<MapObjectConfigEntryDefault> configClass() {
    return MapObjectConfigEntryDefault.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    return TickResult.CONTINUE;
  }
}
