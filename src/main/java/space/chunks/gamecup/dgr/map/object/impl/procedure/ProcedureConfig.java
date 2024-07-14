package space.chunks.gamecup.dgr.map.object.impl.procedure;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;


/**
 * @author Nico_ND1
 */
public interface ProcedureConfig extends MapObjectConfigEntry {
  @NotNull
  Pos workPos();

  @NotNull
  Pos exitPos();

  @NotNull
  PassengerQueueConfig queue();
}
