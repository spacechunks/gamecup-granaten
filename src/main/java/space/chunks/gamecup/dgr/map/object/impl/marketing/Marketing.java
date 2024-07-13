package space.chunks.gamecup.dgr.map.object.impl.marketing;

import net.minestom.server.entity.Player.Hand;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class Marketing extends AbstractMapObject<MarketingConfigEntry> {
  private NPCEntity npc;

  public Marketing() {
    addListener(EventListener.of(PlayerEntityInteractEvent.class, this::handleEntityInteract));
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    this.npc = new NPCEntity(UUID.randomUUID(), "Marketing", this.config.skin());
    this.npc.setInstance(parent.instance(), this.config.spawnPosition());

    registerListeners();
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    unregisterListeners();

    this.npc.remove();
  }

  @Override
  protected @NotNull Class<MarketingConfigEntry> configClass() {
    return MarketingConfigEntry.class;
  }

  private void handleEntityInteract(PlayerEntityInteractEvent event) {
    if (event.getHand() == Hand.MAIN && event.getTarget().equals(this.npc)) {
      event.getPlayer().sendMessage("Interacted with Marketing NPC"); // TODO: open manage inventory
    }
  }
}
