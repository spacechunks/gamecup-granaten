package space.chunks.gamecup.dgr.map;

import com.google.common.collect.Queues;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.event.MapObjectPostRegisterEvent;
import space.chunks.gamecup.dgr.map.event.MapObjectPreRegisterEvent;
import space.chunks.gamecup.dgr.map.event.MapObjectUnregisterEvent;
import space.chunks.gamecup.dgr.map.incident.Incident;
import space.chunks.gamecup.dgr.map.incident.TroubleMaker;
import space.chunks.gamecup.dgr.map.object.Bindable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.MapObject.UnregisterReason;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.Ticking.TickResult;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.team.Team;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Random;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public class MapImpl implements Map {
  private static final Random RANDOM = new Random();
  private static final int TROUBLE_DELAY_MIN = Tick.SERVER_TICKS.fromDuration(Duration.ofSeconds(30));
  private static final int TROUBLE_DELAY_MAX = Tick.SERVER_TICKS.fromDuration(Duration.ofSeconds(60));
  private static final int MAX_CONCURRENT_INCIDENTS = 3;

  private final Team owner;
  private final TroubleMaker troubleMaker;

  private final MapObjectRegistry objects;
  private final Queue<MapObject> objectAddQueue;
  private final Queue<MapObjectToUnregister> objectRemoveQueue;

  private int lastTroubleTick;

  @AssistedInject
  public MapImpl(
      @Assisted Team owner,
      GameFactory factory
  ) {
    this.owner = owner;
    this.objects = factory.createMapObjectRegistry(this);
    this.objectAddQueue = Queues.newSynchronousQueue();
    this.objectRemoveQueue = Queues.newSynchronousQueue();

    this.troubleMaker = factory.createTroubleMaker(this);
  }

  @Override
  public void tick(int currentTick) {
    tickObjectOperations(currentTick);
    tickObjects(currentTick);

    testTroubleMaker(currentTick);
  }

  private void tickObjects(int currentTick) {
    List<MapObject> mapObjects = objects().all().stream()
        .sorted(Comparator.comparingInt(value -> value instanceof Ticking ticking ? ticking.priority() : Ticking.defaultPriority()))
        .toList();

    for (MapObject mapObject : mapObjects) {
      if (mapObject instanceof Ticking ticking) {
        TickResult tickResult = ticking.tick(currentTick);

        if (tickResult == TickResult.UNREGISTER) {
          queueMapObjectUnregister(mapObject);
        }
      }
    }
  }

  private void tickObjectOperations(int currentTick) {
    while (!this.objectAddQueue.isEmpty()) {
      MapObject mapObjectToAdd = this.objectAddQueue.poll();
      if (handlePreAdd(mapObjectToAdd) && objects().add(mapObjectToAdd)) {
        mapObjectToAdd.handleRegister(this);
        handlePostAdd(mapObjectToAdd);
      }
    }

    while (!this.objectRemoveQueue.isEmpty()) {
      MapObjectToUnregister mapObjectToRemove = this.objectRemoveQueue.poll();
      MapObject mapObject = mapObjectToRemove.mapObject();
      if (objects().remove(mapObject)) {
        mapObject.handleUnregister(this);
        handlePostRemove(mapObjectToRemove);
      }
    }
  }

  private boolean handlePreAdd(@NotNull MapObject mapObjectToAdd) {
    MapObjectPreRegisterEvent preRegisterEvent = new MapObjectPreRegisterEvent(mapObjectToAdd);
    MinecraftServer.getGlobalEventHandler().call(preRegisterEvent);
    return !preRegisterEvent.isCancelled();
  }

  // Still safe to add further map objects add this point
  private void handlePostAdd(@NotNull MapObject addedMapObject) {
    if (addedMapObject instanceof Bindable bindable) {
      MapObject target = bindable.boundTarget();
      if (target != null && objects().add(target)) {
        target.handleRegister(this);
        bindable.handleTargetRegister(this);
      }
    }

    MapObjectPostRegisterEvent registerEvent = new MapObjectPostRegisterEvent(addedMapObject);
    MinecraftServer.getGlobalEventHandler().call(registerEvent);
  }

  private void handlePostRemove(@NotNull MapObjectToUnregister removedMapObject) {
    MapObject mapObject = removedMapObject.mapObject();
    if (mapObject instanceof Bindable bindable) {
      MapObject target = bindable.boundTarget();
      if (target != null && objects().remove(target)) {
        target.handleRegister(this);
        bindable.handleTargetUnregister(this);
      }
    }

    MapObjectUnregisterEvent unregisterEvent = new MapObjectUnregisterEvent(mapObject, removedMapObject.reason());
    MinecraftServer.getGlobalEventHandler().call(unregisterEvent);
  }

  private void testTroubleMaker(int currentTick) {
    int incidents = objects().allOf(Incident.class).size();
    if (incidents >= MAX_CONCURRENT_INCIDENTS) {
      return;
    }

    int delay = TROUBLE_DELAY_MIN+RANDOM.nextInt(TROUBLE_DELAY_MAX-TROUBLE_DELAY_MIN);
    if (this.lastTroubleTick+delay > currentTick) {
      return;
    }

    this.lastTroubleTick = currentTick;
    this.troubleMaker.makeTrouble();
  }

  @Override
  public void queueMapObjectRegister(@NotNull MapObject mapObject) {
    this.objectAddQueue.offer(mapObject);
  }

  @Override
  public void queueMapObjectUnregister(@NotNull MapObject mapObject, @Nullable UnregisterReason reason) {
    if (reason == null) {
      reason = UnregisterReason.UNKNOWN;
    }
    this.objectRemoveQueue.offer(new MapObjectToUnregister(mapObject, reason));
  }

  private record MapObjectToUnregister(@NotNull MapObject mapObject, @NotNull UnregisterReason reason) {
  }
}
