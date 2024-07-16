package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SeatSitAnimation extends AbstractBindableMapObject<SeatConfig> implements Animation {
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
    if (this.animationTick++ > 200) {
      this.seat.entity.removePassenger(this.passenger.entityUnsafe());
      this.passenger.entityUnsafe().teleport(this.config.workPos());
      return TickResult.UNREGISTER;
    }
    return TickResult.CONTINUE;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.seat.entity.addPassenger(this.passenger.entityUnsafe());
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
