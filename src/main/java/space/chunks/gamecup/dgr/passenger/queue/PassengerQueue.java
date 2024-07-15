package space.chunks.gamecup.dgr.passenger.queue;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.List;


/**
 * @author Nico_ND1
 */
public interface PassengerQueue extends Named {
  @NotNull
  Pos startingPosition();

  /**
   * Returns the {@link WaitingSlot slots} for the {@link space.chunks.gamecup.dgr.passenger.Passenger passengers} to wait at. The first {@link WaitingSlot} in the list is the first position where a
   * passenger should wait.
   */
  @NotNull
  List<WaitingSlot> waitingSlots();

  boolean isFull();

  @Nullable
  WaitingSlot occupyNextSlot(@NotNull Passenger passenger);

  @NotNull
  WaitingSlot findWaitingSlot(@NotNull Passenger passenger);

  interface WaitingSlot {
    @NotNull
    Pos position();

    @Nullable
    Passenger passenger();

    boolean isOccupied();

    boolean tryOccupy(@NotNull Passenger passenger);

    void free();

    @Nullable
    WaitingSlot leadingSlot();
  }
}
