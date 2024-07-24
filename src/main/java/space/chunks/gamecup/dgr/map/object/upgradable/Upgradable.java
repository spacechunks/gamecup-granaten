package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public interface Upgradable extends MapObject {
  @NotNull
  UpgradeHolder upgradeHolder();

  default double getCurrentPerkValue(@NotNull String key) {
    return upgradeHolder().getCurrentPerkValue(key);
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

  String PROCEDURES_AMOUNT = "procedures_amount";
  String SECURITY_CHECK_SUCCESS_RATE = "security_check_success_rate";
  String LUGGAGE_CLAIM_SPEED = "luggage_claim_speed";
  String FLIGHT_RADAR_SPAWN_SPEED = "flight_radar_spawn_speed";
}
