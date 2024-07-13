package space.chunks.gamecup.dgr.passenger.queue;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;

import java.util.function.Supplier;


/**
 * @author Nico_ND1
 */
public class PassengerQueueSupplier implements Supplier<PassengerQueue> {
  private final PassengerQueueRegistry registry;
  private final Map parent;
  private final String queueName;
  private PassengerQueue queue;

  public PassengerQueueSupplier(@NotNull PassengerQueueRegistry registry, @NotNull Map parent, @NotNull String queueName) {
    this.registry = registry;
    this.parent = parent;
    this.queueName = queueName;
  }

  @Override
  public PassengerQueue get() {
    if(this.queue != null) {
      return this.queue;
    }


    return null;
  }
}
