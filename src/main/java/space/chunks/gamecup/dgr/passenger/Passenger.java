package space.chunks.gamecup.dgr.passenger;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.item.ItemStack;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentity;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;


/**
 * @author Nico_ND1
 */
public interface Passenger extends MapObject, Ticking {
  @NotNull
  PassengerConfig config();

  @NotNull
  PassengerIdentity identity();

  @NotNull
  Map map();

  @NotNull
  Acquirable<? extends EntityCreature> entity();

  @NotNull
  NPCEntity entityUnsafe();

  default boolean setPathTo(@NotNull Point point) {
    return entityUnsafe().setPathTo(point);
  }

  int patience();

  void losePatience();

  @NotNull
  Destination destination();

  @Nullable
  PassengerTask task();

  void findNextTask();

  @Nullable
  ItemStack baggage();

  enum Destination {
    ARRIVING,
    LEAVING
  }
}
