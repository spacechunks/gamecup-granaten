package space.chunks.gamecup.dgr.map.object.upgradable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Groupable;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author Nico_ND1
 */
public final class UpgradeHolderRegistry {
  private final Map<String, UpgradeHolder> upgradeHolders;

  public UpgradeHolderRegistry() {
    this.upgradeHolders = new HashMap<>();
  }

  public @NotNull UpgradeHolder holder(@NotNull Groupable groupable, @NotNull UpgradableConfig config) {
    if (config.levelPerks() != null) {
      return this.upgradeHolders.computeIfAbsent(groupable.group(), s -> new UpgradeHolder(config.levelPerks()));
    } else {
      return this.upgradeHolders.get(groupable.group());
    }
  }

  public @Nullable UpgradeHolder holder(@NotNull String group) {
    return this.upgradeHolders.get(group);
  }

  public @NotNull Set<String> keys() {
    return this.upgradeHolders.keySet();
  }
}
