package space.chunks.gamecup.dgr.map.object.impl.flight.monitor;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * @author Nico_ND1
 */
public record FlightMonitorConfig(
    @NotNull String name,
    @NotNull Pos spawnPos
) implements MapObjectConfigEntry {
}
