package space.chunks.gamecup.dgr.map.procedure.incident;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.object.Bindable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.StateAware;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.procedure.Procedure;

import java.util.Set;


/**
 * @author Nico_ND1
 */
public interface Incident extends MapObject, Bindable, StateAware, Named {
  @NotNull
  Set<Procedure> blockedProcedures();

  @Nullable
  Animation replaceProcedureAnimation(@Nullable Animation currentAnimation);

  void resolve(@NotNull SolutionType solution);

  enum SolutionType {
    UNKNOWN,
    RESOLVED,
    FAILED_TIME,
    FAILED
  }
}
