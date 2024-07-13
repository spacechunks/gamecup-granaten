package space.chunks.gamecup.dgr.minestom.listener;

import com.google.inject.Inject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public final class ListenerHelper implements DynamicListener {
  private final List<EventListener<?>> listeners;
  private boolean registered;

  @Inject
  public ListenerHelper() {
    this.listeners = new ArrayList<>();
  }

  @Override
  public synchronized void addListener(@NotNull EventListener<?> listener) {
    this.listeners.add(listener);
  }

  @Override
  public synchronized void registerListener() {
    if (this.registered) {
      throw new IllegalStateException("Listener already registered");
    }

    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().addListener(listener);
    }
    this.registered = true;
  }

  @Override
  public synchronized void unregisterListener() {
    if (!this.registered) {
      throw new IllegalStateException("Listener not registered");
    }

    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().removeListener(listener);
    }
    this.registered = false;
  }
}
