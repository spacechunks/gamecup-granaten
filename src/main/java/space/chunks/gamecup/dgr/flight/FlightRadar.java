package space.chunks.gamecup.dgr.flight;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;

import java.util.List;


/**
 * @author Nico_ND1
 */
public interface FlightRadar extends MapObject, Ticking {
  @NotNull
  List<Flight> flights();
}
