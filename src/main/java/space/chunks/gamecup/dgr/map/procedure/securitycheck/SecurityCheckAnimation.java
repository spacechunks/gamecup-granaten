package space.chunks.gamecup.dgr.map.procedure.securitycheck;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheckAnimation extends AbstractBindableMapObject<SecurityCheckConfig> implements Animation {
  private final Passenger passenger;
  private int animationTick;

  public SecurityCheckAnimation(@NotNull Passenger passenger) {
    this.passenger = passenger;
  }

  @Override
  protected @NotNull Class<SecurityCheckConfig> configClass() {
    return SecurityCheckConfig.class;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
  }

  @Override
  public @NotNull TickResult tick(int currentTick) {
    switch (this.animationTick) {
      case 1 -> {
        this.passenger.entityUnsafe().lookAt(this.config.workPos().add(0, 1.8, 0));
      }
      case 15 -> {
        this.passenger.entityUnsafe().swingMainHand();
      }
      case 30 -> {
        this.passenger.entityUnsafe().lookAt(this.config.workPos().add(0, 2.0, 0));
      }
      case 32 -> {
        this.passenger.entityUnsafe().lookAt(this.config.workPos().add(0, 1.6, 0));
      }
      case 35 -> {
        this.passenger.entityUnsafe().lookAt(this.config.workPos().add(0, 1.8, 0));
      }
    }

    if (++this.animationTick == 40) {
      return TickResult.UNREGISTER;
    }
    return TickResult.CONTINUE;
  }

  @Override
  public void handleTargetRegister(@NotNull Map parent) {
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
  }
}
