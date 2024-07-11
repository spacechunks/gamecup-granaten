package space.chunks.gamecup.dgr.map.machine;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * Represents {@link MapObject map objects} that can be used by passengers.
 *
 * @author Nico_ND1
 */
public interface Machine extends MapObject, Named {
  @NotNull
  PassengerQueue passengerQueue();
}
