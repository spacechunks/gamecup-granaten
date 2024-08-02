package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.utils.Direction;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimAnimation.Luggage;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;


/**
 * @author Nico_ND1
 */
@Data
@Accessors(fluent=true)
@AllArgsConstructor
public class LuggageClaimLineEntry {
  private Pos pos;
  private Direction direction;
  private WaitingSlot waitingSlot;
  private Luggage luggage; // the luggage FOR this entry, on the way to this position
  private Entity model;
}
