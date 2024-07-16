package space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;


/**
 * @author Nico_ND1
 */
public class PassControlProcedure extends TicketControlProcedure implements Procedure {
  @Override
  public @NotNull String group() {
    return Procedure.PASS_CONTROL;
  }
}
