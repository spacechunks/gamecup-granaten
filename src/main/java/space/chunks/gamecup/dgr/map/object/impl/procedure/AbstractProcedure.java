package space.chunks.gamecup.dgr.map.object.impl.procedure;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.Groupable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident.SolutionType;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolderRegistry;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueRegistry;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public abstract class AbstractProcedure<C extends ProcedureConfig> extends AbstractBindableMapObject<C> implements Procedure, Groupable {
  @Inject
  private GameFactory factory;
  @Inject
  protected PassengerQueueRegistry passengerQueueRegistry;
  @Inject
  protected UpgradeHolderRegistry upgradeHolderRegistry;

  protected Map parent;
  private PassengerQueue passengerQueue;
  private Pos workPos;
  private Pos exitPos;
  @Setter
  protected Animation animation;
  private Incident currentIncident;
  private UpgradeHolder upgradeHolder;

  private final Lock editLock = new ReentrantLock();

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
    upgradeHolder();
  }

  @Override
  public synchronized @Nullable PassengerQueue passengerQueue() {
    try {
      this.editLock.lock();
      if (this.passengerQueue == null) {
        this.passengerQueue = createPassengerQueue();
      }
      return this.passengerQueue;
    } finally {
      this.editLock.unlock();
    }
  }

  protected @Nullable PassengerQueue createPassengerQueue() {
    if (this.config.queue() == null) {
      return null;
    }
    return this.passengerQueueRegistry.get(this.parent, this.config.queue());
  }

  @Override
  public void reportIncident(@NotNull Incident incident) {
    try {
      this.editLock.lock();

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
    } finally {
      this.editLock.unlock();
    }
  }

  @Override
  public void handleIncidentResolved(@NotNull SolutionType solution) {
    try {
      this.editLock.lock();

      this.currentIncident = null;

      if (this.animation != null) {
        this.parent.queueMapObjectUnregister(this.animation, UnregisterReason.INCIDENT_RESOLVED);
      }
    } finally {
      this.editLock.unlock();
    }
  }

  @Override
  public @NotNull UpgradeHolder upgradeHolder() {
    try {
      this.editLock.lock();
      if (this.upgradeHolder == null) {
        this.upgradeHolder = this.upgradeHolderRegistry.holder(this, this.config);
      }
      return this.upgradeHolder;
    } finally {
      this.editLock.unlock();
    }
  }
}
