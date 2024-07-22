package space.chunks.gamecup.dgr.map.object.upgradable;

import lombok.AllArgsConstructor;
import space.chunks.gamecup.dgr.map.Map;

import java.util.function.Supplier;


/**
 * @author Nico_ND1
 */
@AllArgsConstructor
public final class UpgradableCondition implements Supplier<Boolean> {
  private final Map map;
  private final String group;
  private final int minLevel;

  @Override
  public Boolean get() {
    UpgradeHolder upgradeHolder = this.map.upgradeRegistry().holder(this.group);
    if (upgradeHolder == null) {
      return null;
    }

    return upgradeHolder.currentLevel()+1 >= this.minLevel;
  }
}
