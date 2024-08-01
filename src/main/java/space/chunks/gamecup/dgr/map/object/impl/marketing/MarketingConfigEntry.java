package space.chunks.gamecup.dgr.map.object.impl.marketing;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradableConfig;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public record MarketingConfigEntry(
    @NotNull String name,
    @Nullable Map<String, Double[]> levelPerks,
    @Nullable Integer minLevel,
    @NotNull Pos spawnPosition,
    @Nullable PlayerSkin skin,
    int @NotNull [] costs
) implements MapObjectConfigEntry, UpgradableConfig {
}
