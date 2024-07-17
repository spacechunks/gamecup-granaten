package space.chunks.gamecup.dgr.map.object.upgradable.upgrader;

import com.google.inject.Inject;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.AllayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolderRegistry;


/**
 * @author Nico_ND1
 */
public final class Upgrader extends AbstractMapObject<UpgraderConfig> implements MapObject, Ticking {
  private final EntityCreature entity;
  private boolean dancing;

  @Inject
  private UpgradeHolderRegistry upgradeHolderRegistry;

  @Inject
  public Upgrader() {
    this.entity = new EntityCreature(EntityType.ALLAY);
    this.entity.setNoGravity(true);
    this.entity.editEntityMeta(AllayMeta.class, meta -> {
      meta.setCanDuplicate(false);
    });

    addListener(EventListener.of(PlayerEntityInteractEvent.class, this::handleInteract));
  }

  public @Nullable UpgradeHolder upgradeHolder() {
    return this.upgradeHolderRegistry.holder(this.config.targetGroup());
  }

  private void handleInteract(PlayerEntityInteractEvent event) {
    if (event.getTarget() != this.entity) {
      return;
    }


  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    UpgradeHolder upgradeHolder = upgradeHolder();
    if (upgradeHolder == null) {
      return TickResult.CONTINUE;
    }

    int currentLevel = upgradeHolder.currentLevel();
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
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.entity.remove();
  }

  @Override
  protected @NotNull Class<UpgraderConfig> configClass() {
    return UpgraderConfig.class;
  }

  public int getCost(int level) {
    return this.config.costs()[level];
  }
}
