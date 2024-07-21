package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import com.google.inject.Inject;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class SeatProcedure extends AbstractProcedure<SeatConfig> implements Procedure {
  protected final Entity seatModel;
  protected final Entity seat;

  @Inject
  public SeatProcedure() {
    this.seat = new Entity(EntityType.ARMOR_STAND);
    this.seat.editEntityMeta(ArmorStandMeta.class, meta -> {
      meta.setInvisible(true);
      meta.setHasNoGravity(true);
    });

    this.seatModel = new Entity(EntityType.ITEM_DISPLAY);
    this.seatModel.setNoGravity(true);
    this.seatModel.editEntityMeta(ItemDisplayMeta.class, meta -> {
      meta.setItemStack(ItemStack.of(Material.PAPER).withCustomModelData(8));
      //meta.setBrightnessOverride(15);
    });
  }

  @Override
  protected @NotNull Class<SeatConfig> configClass() {
    return SeatConfig.class;
  }

  @Override
  public @Nullable Animation createAnimation(@NotNull Passenger passenger) {
    if (this.animation instanceof SeatSitAnimation sitAnimation) {
      this.parent.queueMapObjectUnregister(this.animation);

      SeatKickAnimation animation = new SeatKickAnimation(this, sitAnimation.passenger, passenger);
      animation.config(this.config);
      bind(animation);

      this.parent.queueMapObjectRegister(animation);
      this.animation = animation;
    } else if (this.animation instanceof SeatKickAnimation) {
      PassengerTask task = passenger.task();
      if (task != null) {
        task.state(State.PROCEED);
      }
    } else {
      SeatSitAnimation animation = new SeatSitWaitForBoardingAnimation(this, passenger);
      animation.config(this.config);
      bind(animation);

      this.parent.queueMapObjectRegister(animation);
      this.animation = animation;
    }
    return this.animation;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.seat.setInstance(parent.instance(), this.config.seatPos().add(0, 0.1, 0));
    parent.instance().setBlock(this.config.seatPos().sub(0.5, -1.5, 0.5), Block.BARRIER);

    Pos seatModelPos = this.config.seatPos().add(0, 2, 0);
    seatModelPos = seatModelPos.withYaw(switch (this.config.direction()) {
      case NORTH -> 180;
      case EAST -> 270;
      case SOUTH -> 0;
      case WEST -> 90;
      default -> throw new IllegalStateException("Unexpected value: "+this.config.direction());
    });
    seatModelPos = seatModelPos.add(this.config.direction().normalX() * 0.1, 0, this.config.direction().normalZ() * 0.1);
    this.seatModel.setInstance(parent.instance(), seatModelPos);
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
    super.handleTargetUnregister(parent);
    this.animation = null;
  }

  @Override
  public @NotNull String group() {
    return Procedure.SEAT;
  }
}
