package space.chunks.gamecup.dgr.map.object.upgradable.upgrader;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
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
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolderRegistry;
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
  @Inject
  private UpgradeHolderRegistry upgradeHolderRegistry;

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
      meta.setSeeThrough(true);
      meta.setBillboardRenderConstraints(BillboardConstraints.CENTER);
    });

    addListener(EventListener.of(PlayerEntityInteractEvent.class, this::handleInteract));
  }

  public @Nullable UpgradeHolder upgradeHolder() {
    return this.upgradeHolderRegistry.holder(this.config.targetGroup());
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

            tick(team.map(), 0);
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
        meta.setText(Component.text("Level: "+upgradeHolder.currentLevel()));
      });
    }

    if (currentLevel+1 > upgradeHolder.maxLevel()) {
      return TickResult.CONTINUE;
    }

    int cost = getCost(currentLevel+1);
    boolean canPurchaseNextLevel = map.owner().money() >= cost;
    dance(canPurchaseNextLevel);
    return TickResult.CONTINUE;
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
  protected @NotNull Class<UpgraderConfig> configClass() {
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
