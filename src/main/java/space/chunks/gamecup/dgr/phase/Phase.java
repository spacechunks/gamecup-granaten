package space.chunks.gamecup.dgr.phase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.Ticking;


/**
 * @author Nico_ND1
 */
public interface Phase extends Named, Ticking {
  boolean isActive();

  void enter(@Nullable Phase previousPhase);

  void quit(@NotNull Phase followingPhase);
}
