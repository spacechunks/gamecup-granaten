package space.chunks.gamecup.dgr.map.procedure;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;


/**
 * Represents {@link MapObject map objects} that can be used by passengers.
 *
 * @author Nico_ND1
 */
public interface Procedure extends MapObject, Named {
  @NotNull
  PassengerQueue passengerQueue();

  @NotNull
  Pos workPos();

  @NotNull
  Pos exitPos();

  @Nullable
  Animation animation();

  void createAnimation(@NotNull Passenger passenger);
}
