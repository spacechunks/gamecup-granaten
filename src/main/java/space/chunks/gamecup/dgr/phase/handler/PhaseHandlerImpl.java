package space.chunks.gamecup.dgr.phase.handler;

import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.phase.Phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Nico_ND1
 */
@Log4j2
public final class PhaseHandlerImpl implements PhaseHandler {
  private final List<Phase> phases;
  private Phase currentPhase;

  @Inject
  public PhaseHandlerImpl(
      @NotNull Set<Phase> phases
  ) {
    //this.phases = new ArrayList<>(phases.stream().map(Provider::get).toList());
    this.phases = new ArrayList<>(phases);
    this.currentPhase = null;

    log.info("Registered phases: {}", phases.stream().map(Named::name).collect(Collectors.joining(", ")));
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
  public @NotNull Collection<Phase> registeredPhases() {
    return this.phases;
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
      log.info("Quit phase: {}", this.currentPhase.name());
    }
    this.currentPhase = phase;
    this.currentPhase.enter(previousPhase);

    log.info("Entered phase: {}", phase.name());
  }

  @Override
  public void tick(int currentTick) {
    if (this.currentPhase != null) {
      this.currentPhase.tick(currentTick);
    }
  }
}
