package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.flight.Flight;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SeatSitWaitForBoardingAnimation extends SeatSitAnimation {
  public SeatSitWaitForBoardingAnimation(@NotNull SeatProcedure seat, @NotNull Passenger passenger) {
    super(seat, passenger);
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    NPCEntity passengerEntity = this.passenger.entityUnsafe();
    lookAround(passengerEntity);

    Flight flight = this.passenger.flight();
    if (Boolean.TRUE.equals(flight.isBoarding())) {
      this.seat.seat.removePassenger(passengerEntity);

      if (!passengerEntity.isRemoved()) {
        passengerEntity.teleport(this.config.workPos());
      }
      return TickResult.UNREGISTER;
    }
    return TickResult.CONTINUE;
  }
}
