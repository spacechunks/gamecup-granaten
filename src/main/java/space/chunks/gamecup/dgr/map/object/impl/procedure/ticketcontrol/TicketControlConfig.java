package space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ProcedureConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;


/**
 * @author Nico_ND1
 */
public record TicketControlConfig(
    @NotNull String name,
    @NotNull Pos workPos,
    @NotNull Pos exitPos,
    @NotNull PassengerQueueConfig queue,
    @NotNull Pos workerPos,
    @NotNull Pos computerPos
) implements ProcedureConfig {
}
