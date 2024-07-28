package space.chunks.gamecup.dgr.map.object.impl.marketing;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.upgradable.Upgradable;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolderRegistry;
import space.chunks.gamecup.dgr.minestom.inventory.Item;
import space.chunks.gamecup.dgr.minestom.inventory.SinglePageGui;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class MarketingGui extends SinglePageGui {
  private final Map map;
  private final MarketingConfigEntry config;

  public MarketingGui(@NotNull Player viewer, @NotNull Map map, @NotNull MarketingConfigEntry config) {
    super(viewer, new Inventory(InventoryType.CHEST_1_ROW, Component.text("Marketing")));
    this.map = map;
    this.config = config;
  }

  @Override
  public void redraw() {
    UpgradeHolderRegistry upgradeHolderRegistry = this.map.upgradeRegistry();
    UpgradeHolder holder = upgradeHolderRegistry.holder(Procedure.MARKETING);
    if (holder != null) {
      int currentLevel = holder.currentLevel();
      List<Component> lore = new ArrayList<>();
      lore.add(Component.text("Current: ").color(NamedTextColor.GRAY).append(Component.text(holder.getCurrentPerkValue(Upgradable.FLIGHT_RADAR_SPAWN_SPEED))));
      if (currentLevel < holder.maxLevel()) {
        lore.add(Component.text("Next: ").color(NamedTextColor.GRAY).append(Component.text(holder.getPerkValue(Upgradable.FLIGHT_RADAR_SPAWN_SPEED, currentLevel+1))));
        lore.add(Component.empty());
        lore.add(Component.text("Cost: ").color(NamedTextColor.YELLOW).append(Component.text(getCost(currentLevel+1))));
      } else {
        lore.add(Component.empty());
        lore.add(Component.text("Max level reached").style(Style.style(NamedTextColor.GREEN, TextDecoration.ITALIC)));
      }

      setItem(0, Item.of(ItemStack.of(Material.RECOVERY_COMPASS)
              .withCustomName(Component.text("Flight spawn speed (Lv. "+currentLevel+")"))
              .withLore(lore))
          .addAllClickListener(event -> {

          }));
    } else {
      System.out.println("???");
    }
  }

  public int getCost(int level) {
    if (level == 0) {
      return 0;
    }
    if (level > this.config.costs().length) {
      return this.config.costs()[this.config.costs().length-1];
    }
    return this.config.costs()[level-1];
  }
}
