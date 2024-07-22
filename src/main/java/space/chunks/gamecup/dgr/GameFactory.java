package space.chunks.gamecup.dgr;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.flight.Flight;
import space.chunks.gamecup.dgr.map.object.impl.procedure.incident.TroubleMaker;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.PassengerConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;


/**
 * @author Nico_ND1
 */
public interface GameFactory {
  @NotNull
  Map createMap(@NotNull Team owner);

  @NotNull
  MapObjectRegistry createMapObjectRegistry(@NotNull Map parent);

  @NotNull
  Member createMember(@NotNull Player player);

  @NotNull
  TroubleMaker createTroubleMaker(@NotNull Map parent);

  @NotNull
  Passenger createPassenger(@NotNull Flight flight, @NotNull Map map, @NotNull PassengerConfig config);

  @NotNull
  PassengerQueue createPassengerQueue(@NotNull PassengerQueueConfig config);
}
