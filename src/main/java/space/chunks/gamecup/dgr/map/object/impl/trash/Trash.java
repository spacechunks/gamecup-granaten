package space.chunks.gamecup.dgr.map.object.impl.trash;

import com.google.inject.Inject;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.instance.Instance;
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
    addListener(EventListener.of(PlayerBlockBreakEvent.class, this::handleBreak));
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
    handleInteract(event.getPlayer(), event.getInstance(), event.getBlockPosition(), event);
  }

  private void handleBreak(PlayerBlockBreakEvent event) {
    handleInteract(event.getPlayer(), event.getInstance(), event.getBlockPosition(), event);
  }

  private void handleInteract(@NotNull Player player, @NotNull Instance instance, @NotNull Point blockPosition, @NotNull CancellableEvent event) {
    if (this.parent == null || this.cleaned) {
      return;
    }

    if (instance != this.parent.instance()) {
      return;
    }

    if (blockPosition.sameBlock(this.config.spawnPos())) {
      event.setCancelled(true);
      this.parent.queueMapObjectUnregister(this);
      this.cleaned = true;
      player.playSound(Sound.sound(Key.key("entity.parrot.imitate.slime"), Source.AMBIENT, 1F, 0.25F));
    }
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    for (Passenger passenger : map.objects().allOfType(Passenger.class)) {
      testSlow(passenger.entityUnsafe(), 1.2, 200);
    }
    for (Member member : map.owner().members()) {
      testSlow(member.player(), 0.4, 80);
    }
    return TickResult.CONTINUE;
  }

  private void testSlow(@NotNull Entity entity, double range, int duration) {
    Pos position = entity.getPosition();
    if (position.withY(0).distanceSquared(this.config.spawnPos().withY(0)) <= range) {
      entity.addEffect(new Potion(PotionEffect.SLOWNESS, (byte) 0, duration));
    }
  }
}
