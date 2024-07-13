package space.chunks.gamecup.dgr.map.procedure;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
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
  protected Animation animation;

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
    super.config(config);

    ProcedureConfig procedureConfig = (ProcedureConfig) config;
    this.passengerQueue = this.passengerQueueRegistry.get(this.parent, procedureConfig.queue());
    this.workPos = procedureConfig.workPos();
    this.exitPos = procedureConfig.exitPos();
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    this.parent = parent;
  }

  @Override
  public @NotNull PassengerQueue passengerQueue() {
    if (this.passengerQueue == null) {
      this.passengerQueue = this.passengerQueueRegistry.get(this.parent, this.config.queue());
    }
    return this.passengerQueue;
  }
}
