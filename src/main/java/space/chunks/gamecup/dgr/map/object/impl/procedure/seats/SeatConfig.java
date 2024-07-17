package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ProcedureConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public record SeatConfig(
    @NotNull String name,
    @Nullable Map<String, Double[]> levelPerks,
    @NotNull Pos workPos,
    @NotNull Pos seatPos,
    @Nullable PassengerQueueConfig queue
) implements ProcedureConfig {
  @Override
  public @NotNull Pos exitPos() {
    return this.workPos;
  }
}
