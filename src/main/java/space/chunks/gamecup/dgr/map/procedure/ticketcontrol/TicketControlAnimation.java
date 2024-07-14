package space.chunks.gamecup.dgr.map.procedure.ticketcontrol;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.procedure.incident.Incident;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class TicketControlAnimation extends AbstractBindableMapObject<TicketControlConfig> implements Animation {
  protected final TicketControl ticketControl;
  protected final Passenger passenger;
  private int animationTick;

  private ItemEntity itemEntity;

  public TicketControlAnimation(@NotNull TicketControl ticketControl, @NotNull Passenger passenger) {
    this.ticketControl = ticketControl;
    this.passenger = passenger;
  }

  @Override
  protected @NotNull Class<TicketControlConfig> configClass() {
    return TicketControlConfig.class;
  }

  @Override
  public @NotNull TickResult tick(int currentTick) {
    if (this.animationTick < 28 || this.animationTick > 64) {
      this.ticketControl.worker.lookAt(this.passenger.entityUnsafe());
    } else {
      this.ticketControl.worker.lookAt(this.config.computerPos());
    }

    switch (this.animationTick) {
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
          Map map = this.passenger.map();
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
    this.itemEntity = new ItemEntity(ItemStack.of(Material.PAPER));
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
