package space.chunks.gamecup.dgr.map.object.impl;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntryDefault;


/**
 * @author Nico_ND1
 */
public final class TestMapObject extends AbstractMapObject<MapObjectConfigEntryDefault> implements MapObject, Ticking {
  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    System.out.println("TestMapObject registered");
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    System.out.println("TestMapObject unregistered");
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    System.out.println("TestMapObject ticked @ "+currentTick);
    return TickResult.CONTINUE;
  }

  @Override
  @NotNull
  public Class<MapObjectConfigEntryDefault> configClass() {
    return MapObjectConfigEntryDefault.class;
  }
}
