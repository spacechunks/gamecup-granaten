package space.chunks.gamecup.dgr.phase;

import com.google.inject.Inject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public abstract class AbstractPhase implements Phase {
  private final List<EventListener<?>> listeners;
  private boolean active;

  @Inject
  protected Game game;

  public AbstractPhase() {
    this.listeners = new ArrayList<>();
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public void enter(@Nullable Phase previousPhase) {
    if (this.active) {
      throw new IllegalStateException();
    }
    this.active = true;

    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().addListener(listener);
    }

    handleEnter_(previousPhase);
  }

  protected abstract void handleEnter_(@Nullable Phase previousPhase);

  @Override
  public void quit(@NotNull Phase followingPhase) {
    if (!this.active) {
      throw new IllegalStateException();
    }
    this.active = false;

    for (EventListener<?> listener : this.listeners) {
      MinecraftServer.getGlobalEventHandler().removeListener(listener);
    }

    handleQuit_(followingPhase);
  }

  protected abstract void handleQuit_(@NotNull Phase followingPhase);

  public void addListener(@NotNull EventListener<?> listener) {
    this.listeners.add(listener);

    if (this.active) {
      MinecraftServer.getGlobalEventHandler().addListener(listener);
    }
  }
}
