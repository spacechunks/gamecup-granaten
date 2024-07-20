package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SeatKickAnimation extends AbstractAnimation<SeatConfig> implements Animation {
  private final SeatProcedure seat;
  private final Passenger sittingPassenger;
  private final Passenger kickingPassenger;
  private int animationTick;

  public SeatKickAnimation(SeatProcedure seat, Passenger sittingPassenger, Passenger kickingPassenger) {
    this.seat = seat;
    this.sittingPassenger = sittingPassenger;
    this.kickingPassenger = kickingPassenger;
  }

  @Override
  protected @NotNull Class<SeatConfig> configClass() {
    return SeatConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (!this.sittingPassenger.isValid() || !this.kickingPassenger.isValid()) {
      return TickResult.UNREGISTER;
    }

    switch (this.animationTick) {
      case 3, 5 -> {
        this.kickingPassenger.entityUnsafe().lookAt(this.sittingPassenger.entityUnsafe());
      }
      case 10, 12, 14, 16, 17, 20 -> {
        this.kickingPassenger.entityUnsafe().lookAt(fuzzleHeadPosition());
      }
      case 13, 15 -> {
        this.sittingPassenger.entityUnsafe().lookAt(this.kickingPassenger.entityUnsafe());
      }
      case 30 -> {
        NPCEntity kickingPassenger = this.kickingPassenger.entityUnsafe();
        Pos pos = kickingPassenger.getPosition().add(0, kickingPassenger.getEyeHeight(), 0);
        this.sittingPassenger.entityUnsafe().lookAt(pos.withYaw(yaw -> yaw-60));
      }
      case 35 -> {
        NPCEntity kickingPassenger = this.kickingPassenger.entityUnsafe();
        Pos pos = kickingPassenger.getPosition().add(0, kickingPassenger.getEyeHeight(), 0);
        this.sittingPassenger.entityUnsafe().lookAt(pos.withYaw(yaw -> yaw+20));
      }
      case 40, 42 -> {
        this.sittingPassenger.entityUnsafe().lookAt(this.kickingPassenger.entityUnsafe());
      }
      case 48 -> {
        this.kickingPassenger.entityUnsafe().swingMainHand();
      }
      case 50 -> {
        this.seat.seat.removePassenger(this.sittingPassenger.entityUnsafe());
        //this.sittingPassenger.entityUnsafe().setVelocity(this.kickingPassenger.entityUnsafe().getPosition().direction().mul(13).withY(y -> y * 0.65D));
      }
      case 51 -> {
        this.sittingPassenger.entityUnsafe().teleport(this.config.workPos());
      }
      case 64 -> {
        map.queueMapObjectUnregister(this);
        this.seat.animation(null);
        this.seat.createAnimation(this.kickingPassenger);
      }
    }

    this.animationTick++;
    return TickResult.CONTINUE;
  }

  private Pos fuzzleHeadPosition() {
    NPCEntity sittingPassenger = this.sittingPassenger.entityUnsafe();
    Pos pos = sittingPassenger.getPosition().add(0, sittingPassenger.getEyeHeight(), 0);
    return pos.withY(y -> y+(0.5-Math.random()));
  }
}
