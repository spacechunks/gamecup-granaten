package space.chunks.gamecup.dgr.passenger;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;


/**
 * @author Nico_ND1
 */
public record PassengerConfig(
    @NotNull Pos spawnPosition,
    @NotNull Pos leavePosition,
    @NotNull Destination destination,
    @NotNull String[] procedures
) {
}
