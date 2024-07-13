package space.chunks.gamecup.dgr.passenger;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;


/**
 * @author Nico_ND1
 */
public interface Passenger extends MapObject {
  @NotNull
  PassengerConfig config();

  @NotNull
  Map map();

  @NotNull
  Acquirable<? extends EntityCreature> entity();

  @NotNull
  NPCEntity entityUnsafe();

  int patience();

  @NotNull
  Destination destination();

  @Nullable
  PassengerTask task();

  void findNextTask();

  // TODO: hold baggage

  enum Destination {
    ARRIVING,
    LEAVING
  }
}
