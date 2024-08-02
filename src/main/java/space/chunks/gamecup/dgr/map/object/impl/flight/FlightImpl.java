package space.chunks.gamecup.dgr.map.object.impl.flight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class FlightImpl implements Flight {
  private final DestinationConfig config;
  private final int passengerGoal;
  private final int targetFinishTick;
  private final double passengersPerTick;

  private int currentPassengers;
  private double spawnTickChance;

  public FlightImpl(@NotNull DestinationConfig config, int passengerGoal, int currentTick, int delay) {
    this.config = config;
    this.passengerGoal = passengerGoal;
    this.targetFinishTick = currentTick+delay;
    this.passengersPerTick = (double) passengerGoal / (double) delay;
  }

  @Override
  public @Nullable String airportName() {
    return null;
  }

  @Override
  public @NotNull DestinationConfig config() {
    return this.config;
  }

  @Override
  public int passengerGoal() {
    return this.passengerGoal;
  }

  @Override
  public int spawnedPassengers() {
    return 0;
  }

  @Override
  public int currentPassengers() {
    return this.currentPassengers;
  }

  @Override
  public void addPassenger(@NotNull Passenger passenger) {
    this.currentPassengers++;
  }

  @Override
  public int targetFinishTick() {
    return this.targetFinishTick;
  }

  @Override
  public double passengersPerTick() {
    return this.passengersPerTick;
  }

  @Override
  public int testPassengerSpawn(boolean endTick) {
    if (this.currentPassengers >= passengerGoal) {
      return 0;
    }

    int passengers = 0;
    this.spawnTickChance += this.passengersPerTick;
    while (this.spawnTickChance >= 1) {
      this.spawnTickChance -= 1;
      passengers++;
    }

    if (endTick && this.spawnTickChance > 0) {
      passengers++;
    }
    this.currentPassengers += passengers;
    return passengers;
  }

  @Override
  public double progress() {
    return (double) this.currentPassengers / this.passengerGoal;
  }

  @Override
  public Boolean isBoarding() {
    return null;
  }

  @Override
  public boolean isValid() {
    return true;
  }
}
