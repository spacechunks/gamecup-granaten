package space.chunks.gamecup.dgr.team;

import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.event.MapObjectUnregisterEvent;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.MapObject.UnregisterReason;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard.ForceUpdateEvent;

import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
public class TeamMapObjectUnregisterListener implements Consumer<MapObjectUnregisterEvent> {
  private final Team team;

  public TeamMapObjectUnregisterListener(@NotNull Team team) {
    this.team = team;
  }

  @Override
  public void accept(MapObjectUnregisterEvent event) {
    Map map = event.map();
    if (map != this.team.map()) {
      return;
    }

    MapObject mapObject = event.mapObject();
    if (!(mapObject instanceof Passenger passenger)) {
      return;
    }

    if (event.reason() == UnregisterReason.PASSENGER_LEFT_HAPPY) {
      this.team.addPassengerMoved();
      this.team.addMoney(10); // TODO: maybe give money depending on patience. Base value could be 10 and depending on how much patience they lost it will go up until it's doubled

      MinecraftServer.getGlobalEventHandler().call(new ForceUpdateEvent(this.team));
    }
  }
}
