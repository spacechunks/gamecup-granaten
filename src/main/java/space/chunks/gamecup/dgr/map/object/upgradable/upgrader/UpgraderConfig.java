package space.chunks.gamecup.dgr.map.object.upgradable.upgrader;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * @author Nico_ND1
 */
public record UpgraderConfig(
    @NotNull String name,
    @NotNull String targetGroup,
    @NotNull Pos spawnPosition,
    int[] costs
) implements MapObjectConfigEntry {
}
