package space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class SecurityCheckFailedAnimation extends AbstractAnimation<SecurityCheckConfig> implements Animation {
  protected final SecurityCheckProcedure securityCheck;
  protected final Passenger passenger;
  private int animationTick;

  public SecurityCheckFailedAnimation(@NotNull UUID contextId, @NotNull SecurityCheckProcedure securityCheck, @NotNull Passenger passenger) {
    super(contextId);
    this.securityCheck = securityCheck;
    this.passenger = passenger;
  }

  @Override
  @NotNull
  public Class<SecurityCheckConfig> configClass() {
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
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    return TickResult.CONTINUE;
  }

  @Override
  public @NotNull String name() {
    return super.name()+"_failed_animation";
  }
}
