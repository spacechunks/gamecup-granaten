package space.chunks.gamecup.dgr.map.event;

import net.minestom.server.event.Event;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public interface MapObjectEvent extends Event {
  @NotNull
  MapObject mapObject();
}
