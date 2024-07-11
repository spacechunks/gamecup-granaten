package space.chunks.gamecup.dgr.phase.handler;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Ticking;
import space.chunks.gamecup.dgr.phase.Phase;


/**
 * @author Nico_ND1
 */
public interface PhaseHandler extends Ticking {
  @NotNull
  Phase currentPhase();

  default boolean isCurrentPhase(@NotNull Class<? extends Phase> clazz) {
    return clazz.isInstance(currentPhase());
  }

  /**
   * Registers a new unique {@link Phase} (identified by {@link Phase#name()}) which can then be enabled by {@link PhaseHandler#enterPhase(String)}.
   *
   * @param phase The phase to register
   */
  void registerPhase(@NotNull Phase phase);

  /**
   * Enters the {@link Phase} bound to the given {@code name}, throws {@link NullPointerException} if none is found. The {@link PhaseHandler#currentPhase()} will be quit before entering the new
   * phase.
   *
   * @param name The {@link Phase#name()} of a previously {@link PhaseHandler#registerPhase(Phase) registered} {@link Phase}.
   */
  void enterPhase(@NotNull String name);

  /**
   * Enables the given {@link Phase} class, throws {@link NullPointerException} if none is found. The {@link PhaseHandler#currentPhase()} will be quit before entering the new phase.
   *
   * @param clazz The class of the {@link Phase} to enable.
   * @param <T>   The type of the {@link Phase}.
   */
  <T extends Phase> void enterPhase(@NotNull Class<T> clazz);
}
