package space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.Incident;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class TicketControlAnimation extends AbstractAnimation<TicketControlConfig> implements Animation {
  protected final TicketControlProcedure ticketControl;
  protected final Passenger passenger;
  private int animationTick;

  private ItemEntity itemEntity;

  public TicketControlAnimation(@NotNull TicketControlProcedure ticketControl, @NotNull Passenger passenger) {
    this.ticketControl = ticketControl;
    this.passenger = passenger;
  }

  @Override
  @NotNull
  public Class<TicketControlConfig> configClass() {
    return TicketControlConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.animationTick < 28 || this.animationTick > 64) {
      this.ticketControl.worker.lookAt(this.passenger.entityUnsafe());
    } else {
      this.ticketControl.worker.lookAt(this.config.computerPos());
    }

    switch (this.animationTick) {
      case 3 -> {
        this.passenger.entityUnsafe().lookAt(this.ticketControl.worker);
      }
      case 10 -> {
        NPCEntity passengerEntity = this.passenger.entityUnsafe();
        passengerEntity.swingMainHand();
        throwTicket(this.passenger.map(), passengerEntity.getPosition());
      }
      case 24 -> {
        this.itemEntity.remove();
        this.itemEntity = null;

        this.ticketControl.worker.setItemInMainHand(ItemStack.of(Material.PAPER));
      }
      case 35, 47, 58 -> {
        this.ticketControl.worker.swingOffHand();
      }
      case 65 -> {
        this.ticketControl.worker.setItemInMainHand(ItemStack.of(Material.AIR));
        throwTicket(this.passenger.map(), this.ticketControl.worker.getPosition());
      }
      case 73 -> {
        this.itemEntity.remove();
        this.itemEntity = null;
      }
      case 200 -> {
        if (Math.random() > 0.8D) {
          Incident incident = (Incident) map.objectTypes().create("ticket_control_failed_incident");
          incident.bind(this.ticketControl);
          map.queueMapObjectRegister(incident);
        }
      }
    }

    if (++this.animationTick == 82) {
      return TickResult.UNREGISTER;
    }
    return TickResult.CONTINUE;
  }

  private void throwTicket(@NotNull Map map, @NotNull Pos startPos) {
    this.itemEntity = new ItemEntity(ItemStack.of(Material.FILLED_MAP));
    this.itemEntity.setPickable(false);
    this.itemEntity.setInstance(map.instance(), startPos.withY(y -> y+1.5));

    Vec velocity = startPos.direction().mul(6);
    this.itemEntity.setVelocity(velocity);
  }

  @Override
  public void handleTargetRegister(@NotNull Map parent) {
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
  }

  @Override
  public @NotNull String name() {
    return super.name()+"_animation";
  }
}
