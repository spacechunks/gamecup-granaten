package space.chunks.gamecup.dgr.map.object.impl.trash;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;


/**
 * @author Nico_ND1
 */
public record TrashConfig(
    @NotNull String name,
    @NotNull Block trashBlock,
    @NotNull Pos spawnPos
) implements MapObjectConfigEntry {
}
