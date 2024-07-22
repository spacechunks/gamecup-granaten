package space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheckAnimation extends AbstractAnimation<SecurityCheckConfig> implements Animation {
  protected final SecurityCheckProcedure securityCheck;
  protected final Passenger passenger;
  private int animationTick;

  public SecurityCheckAnimation(@NotNull SecurityCheckProcedure securityCheck, @NotNull Passenger passenger) {
    this.securityCheck = securityCheck;
    this.passenger = passenger;
  }

  @Override
  protected @NotNull Class<SecurityCheckConfig> configClass() {
    return SecurityCheckConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.animationTick < 20) {
      this.securityCheck.worker.lookAt(this.passenger.entityUnsafe());
    }

    switch (this.animationTick) {
      case 10 -> {
      }
      case 30 -> {
      }
      case 35 -> {
        if (Math.random() > this.securityCheck.config().baseSuccessRate() * this.securityCheck.getCurrentPerkValue("success_rate", 1.0D)) {
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
