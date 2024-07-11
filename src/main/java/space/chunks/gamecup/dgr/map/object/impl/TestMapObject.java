package space.chunks.gamecup.dgr.map.object.impl;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;


/**
 * @author Nico_ND1
 */
public final class TestMapObject extends AbstractMapObject implements MapObject, Ticking {
  @Override
  public void handleRegister(@NotNull Map parent) {
    System.out.println("TestMapObject registered");
  }

  @Override
  public void handleUnregister(@NotNull Map parent) {
    System.out.println("TestMapObject unregistered");
  }

  @Override
  public @NotNull TickResult tick(int currentTick) {
    System.out.println("TestMapObject ticked @ "+currentTick);
    return TickResult.CONTINUE;
  }
}
