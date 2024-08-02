package space.chunks.gamecup.dgr.map.object.impl.marketing;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
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
      drawItem(3, Material.RECOVERY_COMPASS, Upgradable.FLIGHT_RADAR_SPAWN_SPEED, holder);
      drawItem(5, Material.LEATHER_BOOTS, Upgradable.PASSENGER_MOVE_SPEED, holder);
    } else {
      close();
    }
  }

  private void drawItem(int slot, @NotNull Material iconType, @NotNull String key, @NotNull UpgradeHolder holder) {
    int currentLevel = holder.currentLevel();
    List<Component> lore = new ArrayList<>();
    lore.add(Component.text("Current: ").color(NamedTextColor.GRAY).append(formatPerkValue(key, holder.getPerkValue(key, currentLevel), Style.style(NamedTextColor.GREEN, TextDecoration.ITALIC))));
    if (currentLevel < holder.maxLevel()) {
      lore.add(Component.text("Next: ").color(NamedTextColor.GRAY).append(formatPerkValue(key, holder.getPerkValue(key, currentLevel+1), Style.style(NamedTextColor.GREEN, TextDecoration.ITALIC))));
      lore.add(Component.empty());
      lore.add(Component.text("Cost: ").color(NamedTextColor.GRAY).append(Component.text(getCost(currentLevel+1)).color(NamedTextColor.GOLD)));
    } else {
      lore.add(Component.empty());
      lore.add(Component.text("Max level reached").style(Style.style(NamedTextColor.GREEN, TextDecoration.ITALIC)));
    }

    setItem(slot, Item.of(ItemStack.of(iconType)
            .withCustomName(formatPerkName(key).color(NamedTextColor.YELLOW).append(Component.text(" (Lv. "+currentLevel+")").color(NamedTextColor.GRAY)))
            .withLore(lore)
            .withoutExtraTooltip())
        .addAllClickListener(event -> {
          boolean upgrade = holder.upgrade();
          if (upgrade) {
            event.getPlayer().playSound(Sound.sound(Key.key("entity.player.levelup"), Source.AMBIENT, 1F, 1F), this.config.spawnPosition());
          }
          redraw();
        }));
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

  private @NotNull Component formatPerkName(@NotNull String key) {
    return switch (key) {
      case Upgradable.LUGGAGE_CLAIM_SPEED -> Component.text("Rotation speed");
      case Upgradable.FLIGHT_RADAR_SPAWN_SPEED -> Component.text("Flight spawn speed");
      case Upgradable.SECURITY_CHECK_SUCCESS_RATE -> Component.text("Success rate");
      case Upgradable.PROCEDURES_AMOUNT -> Component.text("Amount");
      case Upgradable.PASSENGER_MOVE_SPEED -> Component.text("Passenger movement speed");
      default -> throw new IllegalArgumentException("Unknown perk key: "+key);
    };
  }

  private @NotNull Component formatPerkValue(@NotNull String key, double value, @NotNull Style style) {
    int percentage = (int) (value * 100);
    int percentageIncrease = 100-percentage;
    int percentageIncreaseFlipped = percentage-100;

    return switch (key) {
      case Upgradable.SECURITY_CHECK_SUCCESS_RATE -> {
        yield Component.text("+"+percentageIncreaseFlipped).style(style).append(Component.text("%")).style(style);
      }
      case Upgradable.LUGGAGE_CLAIM_SPEED -> {
        yield Component.text("+"+percentageIncrease).style(style).append(Component.text("%")).style(style);
      }
      case Upgradable.FLIGHT_RADAR_SPAWN_SPEED -> Component.text(-percentageIncrease).style(style).append(Component.text("%")).style(style);
      case Upgradable.PASSENGER_MOVE_SPEED -> Component.text("+"+percentageIncreaseFlipped).style(style).append(Component.text("%")).style(style);
      case Upgradable.PROCEDURES_AMOUNT -> Component.text((int) value).style(style);
      default -> throw new IllegalArgumentException("Unknown perk key: "+key);
    };
  }
}
