package space.chunks.gamecup.dgr.map.object.config;

import org.jetbrains.annotations.NotNull;


/**
 * @author Nico_ND1
 */
public record MapObjectConfigEntryDefault(
    @NotNull String name,
    @NotNull String type
) implements MapObjectConfigEntry {
}
