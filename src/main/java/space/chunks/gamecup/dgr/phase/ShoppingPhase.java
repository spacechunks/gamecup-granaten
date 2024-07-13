package space.chunks.gamecup.dgr.phase;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Nico_ND1
 */
public class ShoppingPhase extends AbstractPhase {
  @Override
  protected void handleEnter_(@Nullable Phase previousPhase) {

  }

  @Override
  protected void handleQuit_(@NotNull Phase followingPhase) {

  }

  @Override
  public @NotNull String name() {
    return "shopping";
  }

  @Override
  public void tick(int currentTick) {

  }
}
