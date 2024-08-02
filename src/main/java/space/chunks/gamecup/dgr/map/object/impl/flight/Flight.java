package space.chunks.gamecup.dgr.map.object.impl.flight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public interface Flight {
  @Nullable
  String airportName();

  @NotNull
  DestinationConfig config();

  int passengerGoal();

  int spawnedPassengers();

  int currentPassengers();

  void addPassenger(@NotNull Passenger passenger);

  int targetFinishTick();

  double passengersPerTick();

  int testPassengerSpawn(boolean endTick);

  double progress();

  @Nullable
  Boolean isBoarding();

  boolean isValid();
}
