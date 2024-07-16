package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ProcedureConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;


/**
 * @author Nico_ND1
 */
public record LuggageClaimConfig(
    @NotNull String name,
    @NotNull Pos exitPos,
    @NotNull PassengerQueueConfig queue,
    @NotNull Pos lineStartPos,
    @NotNull Direction lineStartDiscoverInitDirection,
    @NotNull Direction lineStartWaitingDirection
) implements ProcedureConfig {
  @Override
  public @NotNull Pos workPos() {
    return Pos.ZERO;
  }
}
