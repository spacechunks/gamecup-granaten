package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SeatSitAnimation extends AbstractAnimation<SeatConfig> implements Animation {
  protected final SeatProcedure seat;
  protected final Passenger passenger;
  private int animationTick;

  public SeatSitAnimation(@NotNull SeatProcedure seat, @NotNull Passenger passenger) {
    this.seat = seat;
    this.passenger = passenger;
  }

  @Override
  protected @NotNull Class<SeatConfig> configClass() {
    return SeatConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.animationTick <= 30 && (this.animationTick % 10) == 0) {
      this.passenger.entityUnsafe().lookAt(this.config.workPos().withY(y -> y+1.1));
    }

    if (this.animationTick++ == 200) {
      this.seat.seat.removePassenger(this.passenger.entityUnsafe());
      this.passenger.entityUnsafe().teleport(this.config.workPos());
      return TickResult.UNREGISTER;
    }
    return TickResult.CONTINUE;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.seat.seat.addPassenger(this.passenger.entityUnsafe());
    this.passenger.entityUnsafe().lookAt(this.config.workPos().withY(y -> y+1.1));
  }

  @Override
  public void handleTargetRegister(@NotNull Map parent) {
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
  }

  @Override
  public @NotNull String name() {
    return super.name()+"_animation";
  }
}
