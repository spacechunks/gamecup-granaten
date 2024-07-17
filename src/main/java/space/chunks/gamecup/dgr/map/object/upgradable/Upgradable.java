package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public interface Upgradable extends MapObject {
  @NotNull
  UpgradeHolder upgradeHolder();

  default double getCurrentPerkValue(@NotNull String key, double defaultValue) {
    return upgradeHolder().getCurrentPerkValue(key, defaultValue);
  }

  default int currentLevel() {
    return upgradeHolder().currentLevel();
  }

  default int maxLevel() {
    return upgradeHolder().maxLevel();
  }

  /**
   * Returns false, if the current level is the maximum level.
   */
  default boolean upgrade() {
    return upgradeHolder().upgrade();
  }
}
