package space.chunks.gamecup.dgr.map.object.impl.procedure;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident.SolutionType;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueRegistry;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public abstract class AbstractProcedure<C extends ProcedureConfig> extends AbstractBindableMapObject<C> implements Procedure {
  @Inject
  private GameFactory factory;
  @Inject
  private PassengerQueueRegistry passengerQueueRegistry;

  protected Map parent;
  private PassengerQueue passengerQueue;
  private Pos workPos;
  private Pos exitPos;
  @Setter
  protected Animation animation;
  private Incident currentIncident;

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
    super.config(config);

    ProcedureConfig procedureConfig = (ProcedureConfig) config;
    this.workPos = procedureConfig.workPos();
    this.exitPos = procedureConfig.exitPos();
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    this.parent = parent;
  }

  @Override
  public @NotNull PassengerQueue passengerQueue() {
    if (this.passengerQueue == null) {
      this.passengerQueue = this.passengerQueueRegistry.get(this.parent, this.config.queue());
    }
    return this.passengerQueue;
  }

  @Override
  public void reportIncident(@NotNull Incident incident) {
    this.currentIncident = incident;

    if (this.animation != null) {
      Animation newAnimation = incident.replaceProcedureAnimation(this.animation);

      if (newAnimation != null) {
        this.parent.queueMapObjectUnregister(this.animation, UnregisterReason.INCIDENT_NEW_ANIMATION);

        bind(newAnimation);

        this.parent.queueMapObjectRegister(newAnimation);
        this.animation = newAnimation;
      }
    }
  }

  @Override
  public void handleIncidentResolved(@NotNull SolutionType solution) {
    this.currentIncident = null;

    if (this.animation != null) {
      this.parent.queueMapObjectUnregister(this.animation, UnregisterReason.INCIDENT_RESOLVED);
    }
  }
}
