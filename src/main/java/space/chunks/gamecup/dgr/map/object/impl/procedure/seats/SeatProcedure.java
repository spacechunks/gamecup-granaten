package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SeatProcedure extends AbstractProcedure<SeatProcedureConfig> implements Procedure {
  @Override
  protected @NotNull Class<SeatProcedureConfig> configClass() {
    return null;
  }

  @Override
  public void createAnimation(@NotNull Passenger passenger) {

  }
}
