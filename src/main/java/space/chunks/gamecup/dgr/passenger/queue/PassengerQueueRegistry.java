package space.chunks.gamecup.dgr.passenger.queue;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.GameFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Nico_ND1
 */
public final class PassengerQueueRegistry {
  private final Map<space.chunks.gamecup.dgr.map.Map, List<PassengerQueue>> allQueues;

  private final GameFactory factory;

  @Inject
  public PassengerQueueRegistry(GameFactory factory) {
    this.factory = factory;
    this.allQueues = new HashMap<>();
  }

  public @NotNull PassengerQueue find(@NotNull space.chunks.gamecup.dgr.map.Map parent, @NotNull String name) {
    return getQueues(parent).stream()
        .filter(queue -> queue.name().equals(name))
        .findFirst()
        .orElseThrow();
  }

  public @NotNull PassengerQueue get(@NotNull space.chunks.gamecup.dgr.map.Map parent, @NotNull PassengerQueueConfig config) {
    if (config.ref() == null) {
      PassengerQueue queue = this.factory.createPassengerQueue(config);
      this.allQueues.computeIfAbsent(parent, k -> new ArrayList<>()).add(queue);
      return queue;
    } else {
      return getQueues(parent).stream()
          .filter(queue -> queue.name().equals(config.ref()))
          .findFirst()
          .orElseGet(() -> supplyQueue(parent, config));
    }
  }

  private @NotNull PassengerQueue supplyQueue(@NotNull space.chunks.gamecup.dgr.map.Map parent, @NotNull PassengerQueueConfig config) {
    assert config.ref() != null;
    return new PassengerQueueMirror(new PassengerQueueSupplier(this, parent, config.ref()));
  }

  private @NotNull List<PassengerQueue> getQueues(@NotNull space.chunks.gamecup.dgr.map.Map parent) {
    return this.allQueues.computeIfAbsent(parent, k -> new ArrayList<>());
  }

}
