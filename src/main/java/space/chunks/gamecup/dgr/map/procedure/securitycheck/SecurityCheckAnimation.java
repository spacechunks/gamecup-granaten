package space.chunks.gamecup.dgr.map.procedure.securitycheck;

import net.minestom.server.entity.Entity.Pose;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.procedure.incident.Incident;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheckAnimation extends AbstractBindableMapObject<SecurityCheckConfig> implements Animation {
  protected final SecurityCheck securityCheck;
  protected final Passenger passenger;
  private int animationTick;

  public SecurityCheckAnimation(@NotNull SecurityCheck securityCheck, @NotNull Passenger passenger) {
    this.securityCheck = securityCheck;
    this.passenger = passenger;
  }

  @Override
  protected @NotNull Class<SecurityCheckConfig> configClass() {
    return SecurityCheckConfig.class;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
  }

  @Override
  public @NotNull TickResult tick(int currentTick) {
    if (this.animationTick < 20) {
      this.securityCheck.worker.lookAt(this.passenger.entityUnsafe());
    }

    switch (this.animationTick) {
      case 10 -> {
        this.passenger.entityUnsafe().setPose(Pose.SWIMMING);
      }
      case 30 -> {
        this.passenger.entityUnsafe().setPose(Pose.STANDING);
      }
      case 35 -> {
        if (Math.random() > 0.8D) {
          Map map = this.passenger.map();
          Incident incident = (Incident) map.objectTypes().create("security_check_failed_incident");
          incident.bind(this.securityCheck);
          map.queueMapObjectRegister(incident);
        }
      }
    }

    if (++this.animationTick == 50) {
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

  @Override
  public @NotNull String name() {
    return super.name()+"_animation";
  }
}
