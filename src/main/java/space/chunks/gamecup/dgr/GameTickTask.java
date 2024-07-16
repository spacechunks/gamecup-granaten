package space.chunks.gamecup.dgr;

import com.google.inject.Inject;


/**
 * @author Nico_ND1
 */
public final class GameTickTask implements Runnable {
  private final Game game;
  private int currentTick;

  @Inject
  public GameTickTask(Game game) {
    this.game = game;
  }

  @Override
  public synchronized void run() {
    this.game.tick(this.currentTick++);
  }

  public synchronized int currentTick() {
    return this.currentTick;
  }
}
