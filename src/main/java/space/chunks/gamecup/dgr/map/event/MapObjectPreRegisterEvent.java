package space.chunks.gamecup.dgr.map.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minestom.server.event.trait.CancellableEvent;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;


/**
 * @author Nico_ND1
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(fluent=true)
public class MapObjectPreRegisterEvent implements MapObjectEvent, CancellableEvent {
  private final Map map;
  private final MapObject mapObject;
  private boolean cancelled;

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}
