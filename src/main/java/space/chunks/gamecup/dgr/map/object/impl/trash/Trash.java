package space.chunks.gamecup.dgr.map.object.impl.trash;

import com.google.inject.Inject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.team.member.Member;


/**
 * @author Nico_ND1
 */
public class Trash extends AbstractMapObject<TrashConfig> implements MapObject, Ticking {
  private Map parent;
  private boolean cleaned;

  @Inject
  private Game game;

  public Trash() {
    addListener(EventListener.of(PlayerBlockInteractEvent.class, this::handleInteract));
  }

  @Override
  protected @NotNull Class<TrashConfig> configClass() {
    return TrashConfig.class;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.parent = parent;
    parent.instance().setBlock(this.config.spawnPos(), this.config.trashBlock());
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.parent = null;

    parent.instance().setBlock(this.config.spawnPos(), Block.AIR);
  }

  private void handleInteract(PlayerBlockInteractEvent event) {
    if (this.parent == null || this.cleaned) {
      return;
    }

    if (event.getInstance() != this.parent.instance()) {
      return;
    }

    if (event.getBlockPosition().sameBlock(this.config.spawnPos())) {
      event.setCancelled(true);
      this.parent.queueMapObjectUnregister(this);
      this.cleaned = true;
      event.getPlayer().playSound(Sound.sound(Key.key("entity.parrot.imitate.slime"), Source.AMBIENT, 1F, 0.25F));
    }
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    for (Passenger passenger : map.objects().allOfType(Passenger.class)) {
      testSlow(passenger.entityUnsafe());
    }
    for (Member member : map.owner().members()) {
      //testSlow(member.player());
      // TODO: see PotionAddListener
    }
    return TickResult.CONTINUE;
  }

  private void testSlow(@NotNull Entity entity) {
    Pos position = entity.getPosition();
    if (position.withY(0).distanceSquared(this.config.spawnPos().withY(0)) <= 1.2) {
      entity.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 0, 70));
    }
  }
}
