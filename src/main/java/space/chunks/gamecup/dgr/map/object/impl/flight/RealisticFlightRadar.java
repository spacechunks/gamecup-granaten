package space.chunks.gamecup.dgr.map.object.impl.flight;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class RealisticFlightRadar extends FlightRadarImpl implements FlightRadar {
  private static final String[] AIRPORT_NAMES = {"FRA", "MUC", "BER", "HAM", "DUS", "CGN", "STR", "HAJ", "NUE", "LEJ", "ZRH", "VIE", "AMS", "BRU", "LUX", "PRG", "WAW", "CPH", "GVA"};

  @Override
  protected void tickFlightDeletor(int currentTick) {
    this.flights.removeIf(flight -> {
      RealisticFlight realisticFlight = (RealisticFlight) flight;
      if (realisticFlight.passengers().isEmpty()) {
        return false;
      }
      return realisticFlight.passengers().stream().noneMatch(Passenger::isValid);
    });
  }

  @Override
  protected @NotNull Flight createFlight(@NotNull DestinationConfig config, int passengerGoal, int currentTick, int delay) {
    String randomDestinationName = AIRPORT_NAMES[(int) (Math.random() * AIRPORT_NAMES.length)];
    return new RealisticFlight(randomDestinationName, config, passengerGoal, new ArrayList<>(), currentTick, currentTick+delay);
  }

  @RequiredArgsConstructor
  @Getter
  @Accessors(fluent=true)
  private class RealisticFlight implements Flight {
    private final String airportName;
    private final DestinationConfig config;
    private final int passengerGoal;
    private int spawnedPassengers;
    private final List<Passenger> passengers;
    private final int startTick;
    private final int targetFinishTick;
    private double spawnTickChance;
    private boolean boarding;

    @Override
    public int currentPassengers() {
      return this.passengers.size();
    }

    @Override
    public void addPassenger(@NotNull Passenger passenger) {
      this.passengers.add(passenger);
      this.spawnedPassengers++;
    }

    @Override
    public double passengersPerTick() {
      int tickDiff = this.targetFinishTick-this.startTick;
      return (double) this.passengerGoal / (double) tickDiff;
    }

    @Override
    public int testPassengerSpawn(boolean endTick) {
      if (this.spawnedPassengers >= this.passengerGoal) {
        return 0;
      }

      int passengers = 0;
      this.spawnTickChance += passengersPerTick();
      while (this.spawnTickChance >= 1) {
        this.spawnTickChance -= 1;
        passengers++;
      }

      if (endTick && this.spawnTickChance > 0) {
        passengers++;
        this.spawnTickChance = 0;
      }
      return passengers;
    }

    @Override
    public double progress() {
      int validCount = 0;
      int invalidCount = 0;
      for (Passenger passenger : this.passengers) {
        if (passenger.isValid()) {
          validCount++;
        } else {
          invalidCount++;
        }
      }

      return (double) invalidCount / (double) this.passengerGoal;
    }

    @Override
    public @Nullable Boolean isBoarding() {
      if (this.boarding) {
        return true;
      }

      if (this.config.destination() == Destination.LEAVING) {
        if (this.spawnedPassengers < this.passengerGoal) {
          return false;
        }

        return this.boarding = !this.passengers.isEmpty() && this.passengers.stream().allMatch(passenger -> {
          if (!passenger.isValid()) {
            return true;
          }

          PassengerTask task = passenger.task();
          if (task != null) {
            return task.state() == State.WORK && task.procedureGroup().equals(Procedure.SEAT);
          }
          return false;
        });
      }
      return null;
    }
  }
}
