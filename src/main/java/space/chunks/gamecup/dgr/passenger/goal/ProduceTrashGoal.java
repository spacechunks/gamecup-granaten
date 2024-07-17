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

    PassengerTask task = this.passenger.task();
    if (task != null) {
      if (task.state() == State.WAIT_IN_QUEUE || (task.state() == State.WORK && task.procedureGroup().equals(Procedure.SEAT))) {
        return Math.random() > 0.6D;
      }
    }
    return false;
  }

  @Override
  public void start() {
    this.previousItemInHand = getEntityCreature().getItemInMainHand();

    getEntityCreature().setItemInMainHand(ItemStack.of(Material.COOKED_SALMON));

    globalCooldown = System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(10);
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
