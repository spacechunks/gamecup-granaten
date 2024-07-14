package space.chunks.gamecup.dgr.map.procedure.incident;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.procedure.Procedure;

import java.util.Collections;
import java.util.Set;


/**
 * @author Nico_ND1
 */
public abstract class AbstractIncident<C extends MapObjectConfigEntry> extends AbstractBindableMapObject<C> implements Incident {
  @Override
  public @NotNull Set<Procedure> blockedProcedures() {
    if (this.boundTarget() instanceof Procedure procedure) {
      return Set.of(procedure);
    }
    return Collections.emptySet();
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    for (Procedure blockedProcedure : blockedProcedures()) {
      blockedProcedure.reportIncident(this);
    }
  }

  @Override
  public void resolve(@NotNull SolutionType solution) {
    for (Procedure blockedProcedure : blockedProcedures()) {
      blockedProcedure.handleIncidentResolved(solution);
    }
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    if (reason != UnregisterReason.INCIDENT_RESOLVED) {
      for (Procedure blockedProcedure : blockedProcedures()) {
        blockedProcedure.handleIncidentResolved(SolutionType.UNKNOWN);
      }
    }

    super.handleUnregister(parent, reason);
  }
}
