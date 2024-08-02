package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ProcedureConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public record LuggageClaimConfig(
    @NotNull String name,
    @Nullable Map<String, Double[]> levelPerks,
    @Nullable Integer minLevel,
    @NotNull Pos exitPos,
    @NotNull PassengerQueueConfig queue,
    @NotNull Pos lineStartPos,
    @NotNull Direction lineStartDiscoverInitDirection,
    @NotNull Direction lineStartWaitingDirection,
    double baseFailRate
) implements ProcedureConfig {
  @Override
  public @NotNull Pos workPos() {
    return Pos.ZERO;
  }
}
