package space.chunks.gamecup.dgr.map.event;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
public record MapObjectPostRegisterEvent(
    @NotNull MapObject mapObject
) implements MapObjectEvent {
}
