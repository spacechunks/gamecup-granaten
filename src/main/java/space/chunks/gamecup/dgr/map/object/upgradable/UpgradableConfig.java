package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public interface UpgradableConfig extends MapObjectConfigEntry {
  int maxLevel();

  @NotNull
  Map<String, Double[]> levelPerks();
}
