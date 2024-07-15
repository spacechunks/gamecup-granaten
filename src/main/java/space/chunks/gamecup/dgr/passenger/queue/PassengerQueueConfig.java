package space.chunks.gamecup.dgr.passenger.queue;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author Nico_ND1
 */
public record PassengerQueueConfig(
    @Nullable String ref, // if not null: reference to another queue which will be used, rather than creating a new one
    @NotNull String name,
    @NotNull Pos startingPosition,
    @NotNull List<Slot> slots,
    @NotNull SlotOccupyStrategy slotOccupyStrategy
) {
  public record Slot(@NotNull Pos position) {
  }

  public enum SlotOccupyStrategy {
    RANDOM,
    LAST_EMPTY
  }
}
