package space.chunks.gamecup.dgr.map;

import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Ticking;
import space.chunks.gamecup.dgr.launcher.profiler.SessionProfiler;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolderRegistry;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * We will need multiple maps. Each team has their own map where they play. When any player joins a map they will be assigned their scoreboard and will view their chat messages, bossbar (tracking
 * progress), ...
 *
 * @author Nico_ND1
 */
public interface Map extends Ticking {
  @NotNull
  Team owner();

  /**
   * Executes the given {@link Consumer} for each member that is on this {@link Map}. This includes all {@link Member} of the {@link #owner() owning team} and spectators that may be here.
   */
  void executeForMembers(@NotNull Consumer<Member> consumer);

  /**
   * Returns the {@link Instance} for this map. If it's not loaded yet, it will be loaded synchronously.
   */
  @NotNull
  Instance instance();

  @NotNull
  SessionProfiler profiler();

  void load();

  @NotNull
  UpgradeHolderRegistry upgradeRegistry();

  @NotNull
  MapObjectRegistry objects();

  @NotNull
  MapObjectTypeRegistry objectTypes();

  /**
   * Adds the given {@code mapObject} to a queue to be registered in the next tick.
   *
   * @param mapObject Map object to queue
   */
  default void queueMapObjectRegister(@NotNull MapObject mapObject) {
    queueMapObjectRegister(mapObject, null);
  }

  void queueMapObjectRegister(@NotNull MapObject mapObject, @Nullable Supplier<Boolean> condition);

  /**
   * Adds the given {@code mapObject} to a queue to be unregistered in the next tick.
   *
   * @param mapObject Map object to queue
   */
  void queueMapObjectUnregister(@NotNull MapObject mapObject, @Nullable MapObject.UnregisterReason reason);

  default void queueMapObjectUnregister(@NotNull MapObject mapObject) {
    queueMapObjectUnregister(mapObject, MapObject.UnregisterReason.UNKNOWN);
  }
}
