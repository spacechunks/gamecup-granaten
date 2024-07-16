package space.chunks.gamecup.dgr.map.object.impl.flight;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;
import space.chunks.gamecup.dgr.passenger.PassengerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Nico_ND1
 */
public class FlightRadarImpl extends AbstractMapObject<FlightRadarConfig> implements FlightRadar {
  private final List<Flight> flights;
  private final Lock flightsLock;

  private final Map<Destination, Integer> destinationDelays;

  @Inject
  private GameFactory factory;

  @Inject
  public FlightRadarImpl() {
    this.flights = new ArrayList<>();
    this.flightsLock = new ReentrantLock();
    this.destinationDelays = new HashMap<>();
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
    super.config(config);

    if (config instanceof FlightRadarConfig frConfig) {
      for (DestinationConfig destination : frConfig.destinations()) {
        this.destinationDelays.put(destination.destination(), destination.randomDelay());
      }
    }
  }

  @Override
  protected @NotNull Class<FlightRadarConfig> configClass() {
    return FlightRadarConfig.class;
  }

  @Override
  public @NotNull List<Flight> flights() {
    try {
      this.flightsLock.lock();
      return this.flights;
    } finally {
      this.flightsLock.unlock();
    }
  }

  @Override
  public @NotNull TickResult tick(space.chunks.gamecup.dgr.map.@NotNull Map map, int currentTick) {
    try {
      this.flightsLock.lock();
      tickFlightCreator(currentTick);
      tickFlightDeletor(currentTick);
      tickFlights(map, currentTick);
    } finally {
      this.flightsLock.unlock();
    }
    return TickResult.CONTINUE;
  }

  private void tickFlightDeletor(int currentTick) {
    this.flights.removeIf(flight -> flight.targetFinishTick() == currentTick);
  }

  private void tickFlightCreator(int currentTick) {
    for (DestinationConfig destinationConfig : this.config.destinations()) {
      int delay = this.destinationDelays.getOrDefault(destinationConfig.destination(), 0);
      if (delay > currentTick) {
        continue;
      }

      int randomDelay = destinationConfig.randomDelay();
      int passengerCount = destinationConfig.randomPassengers();
      Flight flight = new FlightImpl(destinationConfig, passengerCount, currentTick, randomDelay / 3);
      this.flights.add(flight);
      this.destinationDelays.put(destinationConfig.destination(), currentTick+randomDelay);
    }
  }

  private void tickFlights(@NotNull space.chunks.gamecup.dgr.map.Map map, int currentTick) {
    for (Flight flight : this.flights) {
      int passengersToSpawn = flight.testPassengerSpawn(currentTick == flight.targetFinishTick());

      for (int i = 0; i < passengersToSpawn; i++) {
        DestinationConfig config = flight.config();
        Passenger passenger = this.factory.createPassenger(new PassengerConfig(
            config.randomSpawnPosition(),
            config.randomLeavePosition(),
            config.destination(),
            config.procedures()
        ));

        map.queueMapObjectRegister(passenger);
      }
    }
  }
}
