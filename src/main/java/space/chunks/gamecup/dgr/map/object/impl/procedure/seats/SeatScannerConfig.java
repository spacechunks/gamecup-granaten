package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * @author Nico_ND1
 */
public record SeatScannerConfig(
    @NotNull String name,
    @NotNull Vec min,
    @NotNull Vec max
) implements MapObjectConfigEntry {
}
