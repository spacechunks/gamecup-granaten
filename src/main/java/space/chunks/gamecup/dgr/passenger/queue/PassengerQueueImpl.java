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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Nico_ND1
 */
public class PassengerQueueImpl implements PassengerQueue {
  private final Pos startingPosition;
  private final List<WaitingSlot> waitingSlots;
  private final String name;

  private final Lock slotLock;

  @AssistedInject
  public PassengerQueueImpl(@Assisted PassengerQueueConfig config) {
    this.startingPosition = config.startingPosition();
    this.waitingSlots = createSlots(config);
    this.name = config.name();
    this.slotLock = new ReentrantLock();
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
    try {
      this.slotLock.lock();

      WaitingSlot availableSlot = findNextNotOccupied(this.waitingSlots.getLast());
      if (availableSlot != null && availableSlot.tryOccupy(passenger)) {
        return availableSlot;
      }
    } finally {
      this.slotLock.unlock();
    }

    return null;
  }

  private @Nullable WaitingSlot findNextNotOccupied(@NotNull WaitingSlot current) {
    WaitingSlot leadingSlot = current.leadingSlot();
    if (leadingSlot == null) {
      if (current.isOccupied()) {
        return null;
      }
      return current;
    }

    if (leadingSlot.isOccupied()) {
      if (current.isOccupied()) {
        return null;
      }
      return current;
    }
    return findNextNotOccupied(leadingSlot);
  }

  @Override
  public @NotNull WaitingSlot findWaitingSlot(@NotNull Passenger passenger) {
    try {
      this.slotLock.lock();

      return this.waitingSlots.stream()
          .filter(slot -> passenger.equals(slot.passenger()))
          .findAny()
          .orElseThrow(() -> new IllegalStateException("Passenger is not waiting in this queue"));
    } finally {
      this.slotLock.unlock();
    }
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }

  @Getter
  @Accessors(fluent=true)
  public class WaitingSlotImpl implements WaitingSlot {
    private final Pos position;
    private final WaitingSlot leadingSlot;
    private Passenger passenger;

    WaitingSlotImpl(Pos position, WaitingSlot leadingSlot) {
      this.position = position;
      this.leadingSlot = leadingSlot;
    }

    @Override
    public boolean isOccupied() {
      try {
        PassengerQueueImpl.this.slotLock.lock();
        return this.passenger != null;
      } finally {
        PassengerQueueImpl.this.slotLock.unlock();
      }
    }

    @Override
    public boolean tryOccupy(@NotNull Passenger passenger) {
      try {
        PassengerQueueImpl.this.slotLock.lock();
        if (this.passenger == null) {
          this.passenger = passenger;
          return true;
        }
        return false;
      } finally {
        PassengerQueueImpl.this.slotLock.unlock();
      }
    }

    @Override
    public void free() {
      try {
        PassengerQueueImpl.this.slotLock.lock();
        this.passenger = null;
      } finally {
        PassengerQueueImpl.this.slotLock.unlock();
      }
    }
  }
}
