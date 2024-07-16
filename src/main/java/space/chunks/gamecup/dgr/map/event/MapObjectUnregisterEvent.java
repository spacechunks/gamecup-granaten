package space.chunks.gamecup.dgr.map.event;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public record MapObjectUnregisterEvent(
    @NotNull Map map,
    @NotNull MapObject mapObject,
    @NotNull MapObject.UnregisterReason reason
) implements MapObjectEvent {
}
