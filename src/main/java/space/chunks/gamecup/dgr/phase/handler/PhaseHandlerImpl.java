package space.chunks.gamecup.dgr.phase.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.phase.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author Nico_ND1
 */
public final class PhaseHandlerImpl implements PhaseHandler {
  private final List<Phase> phases;
  private Phase currentPhase;

  @Inject
  public PhaseHandlerImpl(
      @NotNull Set<Provider<Phase>> phases
  ) {
    this.phases = new ArrayList<>(phases.stream().map(Provider::get).toList());
    this.currentPhase = null;
  }

  @Override
  public @NotNull Phase currentPhase() {
    return this.currentPhase;
  }

  @Override
  public void registerPhase(@NotNull Phase phase) {
    this.phases.add(phase);
  }

  @Override
  public void enterPhase(@NotNull String name) {
    Phase phase = this.phases.stream().filter(p -> p.name().equals(name)).findAny().orElseThrow(() -> new NullPointerException("Phase not found for "+name));
    enterPhase(phase);
  }

  @Override
  public <T extends Phase> void enterPhase(@NotNull Class<T> clazz) {
    Phase phase = this.phases.stream().filter(clazz::isInstance).findAny().orElseThrow(() -> new NullPointerException("Phase not found for "+clazz.getName()));
    enterPhase(phase);
  }

  private void enterPhase(@NotNull Phase phase) {
    Phase previousPhase = null;
    if (this.currentPhase != null) {
      this.currentPhase.quit(phase);
      previousPhase = this.currentPhase;
    }
    this.currentPhase = phase;
    this.currentPhase.enter(previousPhase);
  }

  @Override
  public void tick(int currentTick) {
    if (this.currentPhase != null) {
      this.currentPhase.tick(currentTick);
    }
  }
}
