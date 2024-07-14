package space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.villager.VillagerMeta;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Level;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Profession;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Type;
import net.minestom.server.entity.metadata.villager.VillagerMeta.VillagerData;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class TicketControl extends AbstractProcedure<TicketControlConfig> implements Procedure {
  protected EntityCreature worker;

  public TicketControl() {
    this.worker = new EntityCreature(EntityType.VILLAGER);
    this.worker.editEntityMeta(VillagerMeta.class, meta -> {
      meta.setVillagerData(new VillagerData(Type.PLAINS, Profession.CARTOGRAPHER, Level.MASTER));
    });
  }

  @Override
  protected @NotNull Class<TicketControlConfig> configClass() {
    return TicketControlConfig.class;
  }

  @Override
  public void createAnimation(@NotNull Passenger passenger) {
    TicketControlAnimation animation = new TicketControlAnimation(this, passenger);
    animation.config(this.config);
    bind(animation);

    this.parent.queueMapObjectRegister(animation);
    this.animation = animation;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.worker.setInstance(parent.instance(), this.config.workerPos());
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.worker.remove();
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
    super.handleTargetUnregister(parent);
    this.animation = null;
  }
}
