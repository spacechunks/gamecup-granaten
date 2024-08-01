package space.chunks.gamecup.dgr.map.object.upgradable;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public final class UpgradeHolder {
  private final Map<String, Double[]> levelPerks;
  private int currentLevel;

  public UpgradeHolder(Map<String, Double[]> levelPerks) {
    this.levelPerks = levelPerks;
  }

  public double getCurrentPerkValue(@NotNull String key) {
    return getPerkValue(key, this.currentLevel);
  }

  public double getPerkValue(@NotNull String key, int level) {
    Double[] values = this.levelPerks.get(key);
    if (values == null) {
      return 0.0;
    }

    if (level >= values.length) {
      return values[values.length-1];
    }
    return values[level];
  }

  public int maxLevel() {
    return this.levelPerks.values().stream()
        .max(Comparator.comparingInt(value -> value.length))
        .map(doubles -> doubles.length)
        .orElse(0);
  }

  public boolean upgrade() {
    if (this.currentLevel >= maxLevel()) {
      return false;
    }
    this.currentLevel++;
    return true;
  }
}
