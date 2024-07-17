package space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ProcedureConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public record TicketControlConfig(
    @NotNull String name,
    @Nullable Map<String, Double[]> levelPerks,
    @NotNull Pos workPos,
    @NotNull Pos exitPos,
    @NotNull PassengerQueueConfig queue,
    @NotNull Pos workerPos,
    @NotNull Pos computerPos
) implements ProcedureConfig {
}
