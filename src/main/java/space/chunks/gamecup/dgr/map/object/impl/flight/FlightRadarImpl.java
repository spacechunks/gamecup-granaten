package space.chunks.gamecup.dgr.map.object.impl.flight;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.GameTickTask;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
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
  protected final List<Flight> flights;
  private final Lock flightsLock;

  private final Map<Destination, Integer> destinationDelays;

  @Inject
  private GameFactory factory;
  @Inject
  private GameTickTask tickTask;

  protected space.chunks.gamecup.dgr.map.Map parent;

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
  @NotNull
  public Class<FlightRadarConfig> configClass() {
    return FlightRadarConfig.class;
  }

  @Override
  public synchronized void handleRegister(space.chunks.gamecup.dgr.map.@NotNull Map parent) {
    super.handleRegister(parent);
    this.parent = parent;
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
  public void forceCreate(@NotNull Destination destination) {
    DestinationConfig destinationConfig = this.config.destinations().stream()
        .filter(dc -> dc.destination() == destination)
        .findAny()
        .orElseThrow(() -> new NullPointerException("Destination not found: "+destination));

    int currentTick = this.tickTask.currentTick();
    int randomDelay = destinationConfig.randomDelay();
    int passengerCount = destinationConfig.randomPassengers();
    Flight flight = createFlight(destinationConfig, passengerCount, currentTick, randomDelay / 3);
    this.flights.add(flight);
    this.destinationDelays.put(destinationConfig.destination(), currentTick+randomDelay);
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

  protected void tickFlightDeletor(int currentTick) {
    this.flights.removeIf(flight -> flight.targetFinishTick() == currentTick);
  }

  protected void tickFlightCreator(int currentTick) {
    for (DestinationConfig destinationConfig : this.config.destinations()) {
      int delay = this.destinationDelays.getOrDefault(destinationConfig.destination(), 0);
      if (delay > currentTick) {
        continue;
      }

      int randomDelay = destinationConfig.randomDelay();
      int passengerCount = destinationConfig.randomPassengers();
      Flight flight = createFlight(destinationConfig, passengerCount, currentTick, randomDelay / 3);
      this.flights.add(flight);
      this.destinationDelays.put(destinationConfig.destination(), currentTick+randomDelay);
    }
  }

  protected void tickFlights(@NotNull space.chunks.gamecup.dgr.map.Map map, int currentTick) {
    for (Flight flight : this.flights) {
      int passengersToSpawn = flight.testPassengerSpawn(currentTick == flight.targetFinishTick());

      for (int i = 0; i < passengersToSpawn; i++) {
        DestinationConfig config = flight.config();
        Passenger passenger = this.factory.createPassenger(flight, map, new PassengerConfig(
            config.randomSpawnPosition(),
            config.randomLeavePosition(),
            config.destination(),
            0.75,
            30,
            0.9,
            0.1,
            0.3,
            10,
            20,
            1,
            45 * 20,
            -3,
            75 * 20
        ));
        flight.addPassenger(passenger);

        map.queueMapObjectRegister(passenger);
      }
    }
  }

  protected @NotNull Flight createFlight(@NotNull DestinationConfig config, int passengerGoal, int currentTick, int delay) {
    return new FlightImpl(config, passengerGoal, currentTick, delay);
  }
}
