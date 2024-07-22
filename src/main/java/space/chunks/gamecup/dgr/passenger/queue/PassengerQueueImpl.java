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
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig.SlotOccupyStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Nico_ND1
 */
public class PassengerQueueImpl implements PassengerQueue {
  private final Pos startingPosition;
  private final List<WaitingSlot> waitingSlots;
  private final String name;
  private final SlotOccupyStrategy slotOccupyStrategy;

  private final Lock slotLock;

  @AssistedInject
  public PassengerQueueImpl(@Assisted PassengerQueueConfig config) {
    this.startingPosition = config.startingPosition();
    this.waitingSlots = createSlots(config);
    this.name = config.name();
    this.slotOccupyStrategy = config.slotOccupyStrategy();

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
  public @NotNull WaitingSlot createSlot(@NotNull Pos position, @Nullable WaitingSlot leadingSlot) {
    WaitingSlot waitingSlot = new WaitingSlotImpl(position, leadingSlot);
    this.waitingSlots.add(waitingSlot);
    return waitingSlot;
  }

  @Override
  public boolean isFull() {
    return this.waitingSlots.stream().allMatch(WaitingSlot::isOccupied);
  }

  @Override
  public @Nullable WaitingSlot occupyNextSlot(@NotNull Passenger passenger) {
    try {
      this.slotLock.lock();

      switch (this.slotOccupyStrategy) {
        case RANDOM -> {
          List<WaitingSlot> slotsCopy = new ArrayList<>(this.waitingSlots);
          Collections.shuffle(slotsCopy);

          for (WaitingSlot waitingSlot : slotsCopy) {
            if (waitingSlot.tryOccupy(passenger)) {
              return waitingSlot;
            }
          }
        }
        case LAST_EMPTY -> {
          WaitingSlot lastSlot = this.waitingSlots.getLast();
          WaitingSlot availableSlot = findNextNotOccupied(false, lastSlot, lastSlot);
          if (availableSlot != null && availableSlot.tryOccupy(passenger)) {
            return availableSlot;
          }
        }
      }
    } finally {
      this.slotLock.unlock();
    }

    return null;
  }

  private @Nullable WaitingSlot findNextNotOccupied(boolean initiated, @NotNull WaitingSlot initiatingSlot, @NotNull WaitingSlot current) {
    if (initiated && current == initiatingSlot) {
      if (current.isOccupied()) {
        return null;
      }
      return current;
    }

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
    return findNextNotOccupied(true, initiatingSlot, leadingSlot);
  }

  @Override
  public @NotNull Optional<WaitingSlot> findWaitingSlot(@NotNull Passenger passenger) {
    try {
      this.slotLock.lock();

      return this.waitingSlots.stream()
          .filter(slot -> passenger.equals(slot.occupant()))
          .findAny();
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
    private WaitingSlot leadingSlot;
    private Passenger occupant;

    WaitingSlotImpl(@NotNull Pos position, @Nullable WaitingSlot leadingSlot) {
      this.position = position;
      this.leadingSlot = leadingSlot;
    }

    @Override
    public boolean isOccupied() {
      try {
        PassengerQueueImpl.this.slotLock.lock();
        return this.occupant != null && this.occupant.isValid();
      } finally {
        PassengerQueueImpl.this.slotLock.unlock();
      }
    }

    @Override
    public boolean tryOccupy(@NotNull Passenger passenger) {
      try {
        PassengerQueueImpl.this.slotLock.lock();
        if (this.occupant == null || !this.occupant.isValid()) {
          this.occupant = passenger;
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
        this.occupant = null;
      } finally {
        PassengerQueueImpl.this.slotLock.unlock();
      }
    }

    @Override
    public @NotNull WaitingSlot leadingSlot(@NotNull WaitingSlot leadingSlot) {
      this.leadingSlot = leadingSlot;
      return this;
    }
  }
}
