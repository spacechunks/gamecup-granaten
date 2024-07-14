package space.chunks.gamecup.dgr.map.object.impl.procedure.incident;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;


/**
 * Creates new {@link Incident incidents} for a map.
 *
 * @author Nico_ND1
 */
public interface TroubleMaker {
  @NotNull
  Map parent();

  /**
   * Will be called periodically to create new {@link Incident incidents}.
   */
  void makeTrouble();
}
