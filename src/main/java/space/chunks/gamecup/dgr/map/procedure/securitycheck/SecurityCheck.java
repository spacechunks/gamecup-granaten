package space.chunks.gamecup.dgr.map.procedure.securitycheck;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheck extends AbstractProcedure<SecurityCheckConfig> implements Procedure {
  @Override
  protected @NotNull Class<SecurityCheckConfig> configClass() {
    return SecurityCheckConfig.class;
  }

  @Override
  public void createAnimation(@NotNull Passenger passenger) {
    SecurityCheckAnimation animation = new SecurityCheckAnimation(passenger);
    animation.config(this.config);
    bind(animation);

    this.parent.queueMapObjectRegister(animation);
    this.animation = animation;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
    super.handleTargetUnregister(parent);
    this.animation = null;
  }
}
