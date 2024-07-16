package space.chunks.gamecup.dgr.map.object.impl.animation;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.Bindable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public interface Animation extends MapObject, Bindable, Ticking {
  @NotNull
  UUID contextId();
}
