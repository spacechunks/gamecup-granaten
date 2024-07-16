package space.chunks.gamecup.dgr.map.object.impl.flight;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;


/**
 * @author Nico_ND1
 */
public interface Flight {
  @NotNull
  DestinationConfig config();

  int passengerGoal();

  int currentPassengers();

  void newPassenger();

  int targetFinishTick(); // TODO: targetStartTick, this way we can calculate passengersPerTick

  double passengersPerTick();

  int testPassengerSpawn(boolean endTick);
}
