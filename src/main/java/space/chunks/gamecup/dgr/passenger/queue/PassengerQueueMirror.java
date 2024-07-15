package space.chunks.gamecup.dgr.passenger.queue;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.List;
import java.util.function.Supplier;


/**
 * @author Nico_ND1
 */
public final class PassengerQueueMirror implements PassengerQueue {
  private final Supplier<PassengerQueue> queueSupplier;

  public PassengerQueueMirror(@NotNull Supplier<PassengerQueue> queueSupplier) {
    this.queueSupplier = queueSupplier;
  }

  @Override
  public @NotNull Pos startingPosition() {
    return this.queueSupplier.get().startingPosition();
  }

  @Override
  public @NotNull List<WaitingSlot> waitingSlots() {
    return this.queueSupplier.get().waitingSlots();
  }

  @Override
  public @NotNull WaitingSlot createSlot(@NotNull Pos position, @Nullable WaitingSlot leadingSlot) {
    return this.queueSupplier.get().createSlot(position, leadingSlot);
  }

  @Override
  public boolean isFull() {
    return this.queueSupplier.get().isFull();
  }

  @Override
  public @NotNull WaitingSlot occupyNextSlot(@NotNull Passenger passenger) {
    return this.queueSupplier.get().occupyNextSlot(passenger);
  }

  @Override
  public @NotNull WaitingSlot findWaitingSlot(@NotNull Passenger passenger) {
    return this.queueSupplier.get().findWaitingSlot(passenger);
  }

  @Override
  public @NotNull String name() {
    return this.queueSupplier.get().name();
  }
}
