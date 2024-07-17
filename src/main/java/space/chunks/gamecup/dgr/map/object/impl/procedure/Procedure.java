package space.chunks.gamecup.dgr.map.object.impl.procedure;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.Groupable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.StateAware;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident.SolutionType;
import space.chunks.gamecup.dgr.map.object.upgradable.Upgradable;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;


/**
 * Represents {@link MapObject map objects} that can be used by passengers.
 *
 * @author Nico_ND1
 */
public interface Procedure extends MapObject, Upgradable, StateAware, Named, Groupable {
  @Nullable
  PassengerQueue passengerQueue();

  @NotNull
  Pos workPos();

  @NotNull
  Pos exitPos();

  @Nullable
  Animation animation();

  @NotNull
  Procedure animation(@Nullable Animation animation);

  // TODO: it's the start point when a passenger enters the procedure, maybe rename it accordingly
  @Nullable
  Animation createAnimation(@NotNull Passenger passenger);

  @Nullable
  Incident currentIncident();

  void reportIncident(@NotNull Incident incident);

  void handleIncidentResolved(@NotNull SolutionType solution);

  default boolean allowQueueToMoveUp() {
    return true;
  }

  String SECURITY_CHECK = "security_check";
  String TICKET_CONTROL = "ticket_control";
  String LUGGAGE_CLAIM = "luggage_claim";
  String PASS_CONTROL = "pass_control";
  String SEAT = "seat";
}
