package space.chunks.gamecup.dgr.map.object.upgradable.upgrader;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta.BillboardConstraints;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.entity.metadata.other.AllayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.upgradable.Upgradable;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.team.Team;

import java.util.Optional;


/**
 * @author Nico_ND1
 */
public final class Upgrader extends AbstractMapObject<UpgraderConfig> implements MapObject, Ticking {
  private final EntityCreature entity;
  private final Entity textEntity;
  private boolean dancing;

  @Inject
  private Game game;

  private Map parent;

  @Inject
  public Upgrader() {
    this.entity = new EntityCreature(EntityType.ALLAY);
    this.entity.setNoGravity(true);
    this.entity.editEntityMeta(AllayMeta.class, meta -> {
      meta.setCanDuplicate(false);
    });

    this.textEntity = new Entity(EntityType.TEXT_DISPLAY);
    this.textEntity.setNoGravity(true);
    this.textEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
      meta.setBillboardRenderConstraints(BillboardConstraints.CENTER);
    });

    addListener(EventListener.of(PlayerEntityInteractEvent.class, this::handleInteract));
  }

  public @Nullable UpgradeHolder upgradeHolder() {
    return this.parent.upgradeRegistry().holder(this.config.targetGroup());
  }

  private void handleInteract(PlayerEntityInteractEvent event) {
    if (event.getHand() != Hand.MAIN) {
      return;
    }

    if (event.getTarget() != this.entity) {
      return;
    }

    Optional<Team> optionalTeam = this.game.findTeam(event.getPlayer());
    if (optionalTeam.isEmpty()) {
      return;
    }

    Team team = optionalTeam.get();
    UpgradeHolder upgradeHolder = upgradeHolder();
    if (upgradeHolder != null) {
      int currentLevel = upgradeHolder.currentLevel();
      if (currentLevel+1 > upgradeHolder.maxLevel()) {
        event.getPlayer().sendMessage("Max level reached.");
      } else {
        int cost = getCost(currentLevel+1);

        if (team.money() >= cost) {
          boolean upgrade = upgradeHolder.upgrade();

          if (upgrade) {
            team.forceRemoveMoney(cost);
            event.getPlayer().sendMessage("Upgraded: "+upgrade);

            tick(team.map(), 0); // force text update
          }
        } else {
          event.getPlayer().sendMessage("Not enough money!");
        }
      }
    }
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    UpgradeHolder upgradeHolder = upgradeHolder();
    if (upgradeHolder == null) {
      return TickResult.CONTINUE;
    }

    int currentLevel = upgradeHolder.currentLevel();

    if (currentTick % 20 == 0) {
      this.textEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
        meta.setText(text(upgradeHolder));
      });
    }

    if (currentLevel+1 > upgradeHolder.maxLevel()) {
      return TickResult.CONTINUE;
    }

    int cost = getCost(currentLevel+1);
    boolean canPurchaseNextLevel = map.owner().money() >= cost && currentLevel < upgradeHolder.maxLevel();
    dance(canPurchaseNextLevel);
    return TickResult.CONTINUE;
  }

  private @NotNull Component text(@NotNull UpgradeHolder upgradeHolder) {
    int currentLevel = upgradeHolder.currentLevel();
    Component text = Component.text("Level: "+(currentLevel+1));
    if (currentLevel < upgradeHolder.maxLevel()) {
      boolean canAfford = this.parent.owner().money() >= getCost(currentLevel+1);
      Style numberStyle = canAfford ? Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD) : Style.style(NamedTextColor.YELLOW, TextDecoration.STRIKETHROUGH);
      text = text.color(NamedTextColor.YELLOW)
          .append(Component.text(" -> ").color(NamedTextColor.WHITE).decorate(TextDecoration.ITALIC))
          .append(Component.text(currentLevel+2).style(numberStyle))
          .append(Component.text(" "))
          .append(Component.text("(Cost: ").color(NamedTextColor.GRAY))
          .append(Component.text(getCost(currentLevel+1)).color(NamedTextColor.GOLD))
          .append(Component.text(")").color(NamedTextColor.GRAY));
    } else {
      text = text.color(NamedTextColor.GREEN);
    }

    for (String levelPerkKey : upgradeHolder.levelPerks().keySet()) {
      Double[] levelPerkValues = upgradeHolder.levelPerks().get(levelPerkKey);
      double currentValue = upgradeHolder.getCurrentPerkValue(levelPerkKey);
      Double nextValue = null;
      if (currentLevel+1 < levelPerkValues.length) {
        nextValue = levelPerkValues[currentLevel+1];
      }

      Component perkText = formatPerkName(levelPerkKey).color(NamedTextColor.WHITE).decorate(TextDecoration.ITALIC)
          .append(Component.text(": ").color(NamedTextColor.GRAY))
          .append(formatPerkValue(levelPerkKey, currentValue, Style.style(nextValue == null ? NamedTextColor.GREEN : NamedTextColor.YELLOW)));
      if (nextValue != null) {
        NamedTextColor color = currentValue+2 < levelPerkValues.length ? NamedTextColor.YELLOW : NamedTextColor.GREEN;
        perkText = perkText.append(Component.text(" -> ").append(formatPerkValue(levelPerkKey, nextValue, Style.style(color, TextDecoration.BOLD))));
      }

      text = text.appendNewline().append(perkText);
    }
    return text;
  }

  private @NotNull Component formatPerkName(@NotNull String key) {
    return switch (key) {
      case Upgradable.LUGGAGE_CLAIM_SPEED -> Component.text("Rotation speed");
      case Upgradable.FLIGHT_RADAR_SPAWN_SPEED -> Component.text("Spawn speed");
      case Upgradable.SECURITY_CHECK_SUCCESS_RATE -> Component.text("Success rate");
      case Upgradable.PROCEDURES_AMOUNT -> Component.text("Amount");
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
      case Upgradable.PROCEDURES_AMOUNT -> Component.text((int) value).style(style);
      default -> throw new IllegalArgumentException("Unknown perk key: "+key);
    };
  }

  private void dance(boolean dance) {
    if (dance != this.dancing) {
      this.dancing = dance;
      this.entity.editEntityMeta(AllayMeta.class, meta -> {
        meta.setDancing(dance);
      });
    }
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.parent = parent;
    this.entity.setInstance(parent.instance(), this.config.spawnPosition());
    this.textEntity.setInstance(parent.instance(), this.config.spawnPosition().add(0, 1, 0));
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.entity.remove();
    this.textEntity.remove();
  }

  @Override
  @NotNull
  public Class<UpgraderConfig> configClass() {
    return UpgraderConfig.class;
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
