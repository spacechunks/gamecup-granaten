package space.chunks.gamecup.dgr.map.object;

/**
 * {@link MapObject Map objects} implementing this interface won't be removed when the {@link space.chunks.gamecup.dgr.phase.ShoppingPhase} begins. All other map objects will automatically be
 * unregistered. The implementation has to pause and resume any actions while the shopping phase is active.
 *
 * @author Nico_ND1
 */
public interface ShoppingPhasePersistent {
  void pause();

  void resume();
}
