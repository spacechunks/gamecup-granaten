package space.chunks.gamecup.dgr.passenger;

import net.minestom.server.entity.Entity;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public interface Passenger extends MapObject {
  @NotNull
  Acquirable<Entity> entity();

  @NotNull
  Entity entityUnsafe();

  int patience();

  // TODO: hold baggage
}
