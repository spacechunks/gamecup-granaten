package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import com.google.inject.Inject;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class SeatProcedure extends AbstractProcedure<SeatConfig> implements Procedure {
  protected final Entity entity;

  @Inject
  public SeatProcedure() {
    this.entity = new Entity(EntityType.ARROW);
    this.entity.setNoGravity(true);
    this.entity.setInvisible(true);
  }

  @Override
  protected @NotNull Class<SeatConfig> configClass() {
    return SeatConfig.class;
  }

  @Override
  public void createAnimation(@NotNull Passenger passenger) {
    if (this.animation instanceof SeatSitAnimation sitAnimation) {
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
      SeatSitAnimation animation = new SeatSitAnimation(this, passenger);
      animation.config(this.config);
      bind(animation);

      this.parent.queueMapObjectRegister(animation);
      this.animation = animation;
    }
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.entity.setInstance(parent.instance(), this.config.seatPos());
  }

  @Override
  public @NotNull String group() {
    return Procedure.SEAT;
  }
}
