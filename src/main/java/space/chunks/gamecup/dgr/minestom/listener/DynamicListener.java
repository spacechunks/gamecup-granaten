package space.chunks.gamecup.dgr.minestom.listener;

import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;


/**
 * Handles {@link net.minestom.server.event.EventListener event listeners} that may be registered or unregistered dynamically.
 *
 * @author Nico_ND1
 */
public interface DynamicListener {
  void addListener(@NotNull EventListener<?> listener);

  void registerListener();

  void unregisterListener();
}
