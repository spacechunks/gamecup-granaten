package space.chunks.gamecup.dgr.flight;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.flight.FlightRadarConfig.DestinationConfig;


/**
 * @author Nico_ND1
 */
public interface Flight {
  @NotNull
  DestinationConfig config();

  int passengerGoal();

  int currentPassengers();

  void newPassenger();

  int targetFinishTick();

  double passengersPerTick();

  int testPassengerSpawn(boolean endTick);
}
