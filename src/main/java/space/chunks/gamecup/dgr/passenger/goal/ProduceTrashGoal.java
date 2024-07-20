package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.impl.trash.TrashConfig;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;

import java.util.concurrent.TimeUnit;


/**
 * @author Nico_ND1
 */
public class ProduceTrashGoal extends GoalSelector {
  private static final double PATIENCE_THRESHOLD = 0.8;
  private static final ItemStack[] HAND_ITEMS = {
      ItemStack.of(Material.COOKED_SALMON), ItemStack.of(Material.COOKED_BEEF), ItemStack.of(Material.COOKED_PORKCHOP),
      ItemStack.of(Material.APPLE), ItemStack.of(Material.MELON_SLICE), ItemStack.of(Material.CARROT), ItemStack.of(Material.BAKED_POTATO),
      ItemStack.of(Material.BEETROOT_SOUP), ItemStack.of(Material.MUSHROOM_STEW), ItemStack.of(Material.RABBIT_STEW), ItemStack.of(Material.MILK_BUCKET)
  };

  private static long globalCooldown;

  private final Passenger passenger;
  private ItemStack previousItemInHand;
  private int animationTicks;

  public ProduceTrashGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    if (globalCooldown > System.currentTimeMillis()) {
      return false;
    }

    if (this.passenger.patiencePercentage() > PATIENCE_THRESHOLD && Math.random() > 0.2) { // mostly it should depend on the patience, but sometimes it can be randomly activated
      return false;
    }

    PassengerTask task = this.passenger.task();
    if (task != null) {
      if ((task.state() == State.WAIT_IN_QUEUE || task.state() == State.JOIN_QUEUE) || (task.state() == State.WORK && task.procedureGroup().equals(Procedure.SEAT))) {
        return Math.random() > 0.6D;
      }
    }
    return false;
  }

  @Override
  public void start() {
    this.previousItemInHand = getEntityCreature().getItemInMainHand();

    getEntityCreature().setItemInMainHand(HAND_ITEMS[(int) (Math.random() * HAND_ITEMS.length)]);

    globalCooldown = System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(8);
  }

  @Override
  public void tick(long l) {
    if (this.animationTicks == 5) {
      getEntityCreature().editEntityMeta(PlayerMeta.class, meta -> {
        meta.setHandActive(true);
      });
    } else if (this.animationTicks == 28) {
      getEntityCreature().editEntityMeta(PlayerMeta.class, meta -> {
        meta.setHandActive(false);
      });
    } else if (this.animationTicks == 30) {
      Map map = this.passenger.map();
      Pos targetPos = getEntityCreature().getPosition().add(getEntityCreature().getPosition().direction()).withY(getEntityCreature().getPosition().y());
      targetPos = new Pos(targetPos.blockX(), targetPos.blockY(), targetPos.blockZ());

      if (map.instance().getBlock(targetPos) == Block.AIR) {
        MapObject mapObject = map.objectTypes().create("trash", new TrashConfig("trash_"+Math.random(), Block.NETHER_SPROUTS, targetPos));
        map.queueMapObjectRegister(mapObject);
      }
    }

    this.animationTicks++;
  }

  @Override
  public boolean shouldEnd() {
    return this.animationTicks >= 40;
  }

  @Override
  public void end() {
    getEntityCreature().setItemInMainHand(this.previousItemInHand);
  }
}
