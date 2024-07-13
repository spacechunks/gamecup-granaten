package space.chunks.gamecup.dgr.passenger.queue;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig.Slot;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class PassengerQueueImpl implements PassengerQueue {
  private final Pos startingPosition;
  private final List<WaitingSlot> waitingSlots;
  private final String name;

  @AssistedInject
  public PassengerQueueImpl(@Assisted PassengerQueueConfig config) {
    this.startingPosition = config.startingPosition();
    this.waitingSlots = createSlots(config);
    this.name = config.name();
  }

  private @NotNull List<WaitingSlot> createSlots(@NotNull PassengerQueueConfig config) {
    List<Slot> configSlots = config.slots();
    List<WaitingSlot> slots = new ArrayList<>(configSlots.size());
    WaitingSlot leadingSlot = null;
    for (Slot configSlot : configSlots) {
      WaitingSlot waitingSlot = new WaitingSlotImpl(configSlot.position(), leadingSlot);
      leadingSlot = waitingSlot;
      slots.add(waitingSlot);
    }
    return slots;
  }

  @Override
  public @NotNull Pos startingPosition() {
    return this.startingPosition;
  }

  @Override
  public @NotNull List<WaitingSlot> waitingSlots() {
    return this.waitingSlots;
  }

  @Override
  public boolean isFull() {
    return this.waitingSlots.stream().allMatch(WaitingSlot::isOccupied);
  }

  @Override
  public @Nullable WaitingSlot occupyNextSlot(@NotNull Passenger passenger) {
    for (WaitingSlot waitingSlot : this.waitingSlots) {
      if (waitingSlot.isOccupied()) {
        continue;
      }

      waitingSlot.occupy(passenger);
      return waitingSlot;
    }
    return null;
  }

  @Override
  public @NotNull WaitingSlot findWaitingSlot(@NotNull Passenger passenger) {
    return this.waitingSlots.stream()
        .filter(slot -> passenger.equals(slot.passenger()))
        .findAny()
        .orElseThrow(() -> new IllegalStateException("Passenger is not waiting in this queue"));
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }

  @Getter
  @Accessors(fluent=true)
  public static class WaitingSlotImpl implements WaitingSlot {
    private final Pos position;
    private final WaitingSlot leadingSlot;
    private Passenger passenger;

    WaitingSlotImpl(Pos position, WaitingSlot leadingSlot) {
      this.position = position;
      this.leadingSlot = leadingSlot;
    }

    @Override
    public void occupy(@NotNull Passenger passenger) {
      this.passenger = passenger;
    }

    @Override
    public boolean isOccupied() {
      return this.passenger != null;
    }

    @Override
    public void free() {
      this.passenger = null;
    }
  }
}
