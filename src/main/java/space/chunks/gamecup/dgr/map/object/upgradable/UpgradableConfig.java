package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public interface UpgradableConfig extends MapObjectConfigEntry {
  @Nullable
  Map<String, Double[]> levelPerks();

  @Nullable
  Integer minLevel();
}
