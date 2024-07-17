package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;

import java.util.Map;


/**
 * @author Nico_ND1
 */
public interface Upgradable extends MapObject {
  @NotNull
  Map<String, Double[]> levelPerks();

  default double getCurrentPerkValue(@NotNull String key, double defaultValue) {
    Double[] values = levelPerks().get(key);
    if (values == null) {
      return defaultValue;
    }
    return values[currentLevel()];
  }

  int currentLevel();

  int maxLevel();

  /**
   * Returns false, if the current level is the maximum level.
   */
  boolean upgrade();
}
