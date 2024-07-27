package space.chunks.gamecup.dgr.map.object;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public abstract class AbstractMapObject<C extends MapObjectConfigEntry> implements MapObject, StateAware {
  protected @Nullable C config;
  private String name;
  private final List<EventListener<?>> listeners;
  private boolean registered;

  public AbstractMapObject(@NotNull String name) {
    this.name = name;
    this.listeners = new ArrayList<>();
  }

  public AbstractMapObject() {
    this.listeners = new ArrayList<>();
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
    if (!configClass().isInstance(config)) {
      throw new IllegalArgumentException("Invalid config type: "+config.getClass().getName()+" (expected: "+configClass().getName()+")");
    }

    this.config = (C) config;
    this.name = config.name();
  }

  @Override
  public @NotNull String name() {
    if (this.name == null) {
      return "unnamed:"+this.getClass().getSimpleName();
    }
    return this.name;
  }

  protected void addListener(@NotNull EventListener<?> listener) {
    this.listeners.add(listener);

    if (isRegistered()) {
      MinecraftServer.getGlobalEventHandler().addListener(listener);
    }
  }

  protected void registerListeners() {
    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().addListener(listener);
    }
  }

  protected void unregisterListeners() {
    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().removeListener(listener);
    }
  }

  public abstract @NotNull Class<C> configClass();

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    this.registered = true;
    registerListeners();
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    this.registered = false;
    unregisterListeners();
  }

  @Override
  public synchronized boolean isRegistered() {
    return this.registered;
  }
}
