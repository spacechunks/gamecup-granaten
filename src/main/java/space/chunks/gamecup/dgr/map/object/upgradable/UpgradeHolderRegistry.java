package space.chunks.gamecup.dgr.map.object.upgradable;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Nico_ND1
 */
public final class UpgradeHolderRegistry {
  private final Map<String, UpgradeHolder> upgradeHolders;

  @Inject
  public UpgradeHolderRegistry() {
    this.upgradeHolders = new HashMap<>();
  }

  public @NotNull UpgradeHolder holder(@NotNull Procedure procedure, @NotNull UpgradableConfig config) {
    if (config.levelPerks() != null) {
      return this.upgradeHolders.computeIfAbsent(procedure.group(), s -> new UpgradeHolder(config.levelPerks()));
    } else {
      return this.upgradeHolders.get(procedure.group());
    }
  }

  public @Nullable UpgradeHolder holder(@NotNull String group) {
    return this.upgradeHolders.get(group);
  }
}
