package space.chunks.gamecup.dgr.map;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.utils.time.Tick;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.event.MapObjectPostRegisterEvent;
import space.chunks.gamecup.dgr.map.event.MapObjectPreRegisterEvent;
import space.chunks.gamecup.dgr.map.event.MapObjectUnregisterEvent;
import space.chunks.gamecup.dgr.map.object.Bindable;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.MapObject.UnregisterReason;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.Ticking.TickResult;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetup;
import space.chunks.gamecup.dgr.map.procedure.incident.Incident;
import space.chunks.gamecup.dgr.map.procedure.incident.TroubleMaker;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
@Log4j2
public class MapImpl implements Map {
  private static final Random RANDOM = new Random();
  private static final int TROUBLE_DELAY_MIN = Tick.SERVER_TICKS.fromDuration(Duration.ofSeconds(30));
  private static final int TROUBLE_DELAY_MAX = Tick.SERVER_TICKS.fromDuration(Duration.ofSeconds(60));
  private static final int MAX_CONCURRENT_INCIDENTS = 3;

  private final Team owner;
  private final TroubleMaker troubleMaker;

  private final MapObjectRegistry objects;
  private final MapObjectTypeRegistry objectTypes;
  private final Queue<MapObject> objectAddQueue;
  private final Queue<MapObjectToUnregister> objectRemoveQueue;

  private int lastTroubleTick;

  private Instance instance;

  @AssistedInject
  public MapImpl(
      @Assisted Team owner,
      GameFactory factory,
      MapObjectDefaultSetup mapObjectDefaultSetup,
      MapObjectTypeRegistry objectTypes
  ) {
    this.owner = owner;
    this.objects = factory.createMapObjectRegistry(this);
    this.objectTypes = objectTypes;
    this.objectAddQueue = new ArrayBlockingQueue<>(100);
    this.objectRemoveQueue = new ArrayBlockingQueue<>(100);

    this.troubleMaker = factory.createTroubleMaker(this);
    mapObjectDefaultSetup.createDefaultObjects(this);
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
        mapObject.handleUnregister(this, mapObjectToRemove.reason());
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
      for (Bindable boundObject : bindable.boundObjects()) {
        boundObject.handleTargetRegister(this);
      }
    }

    MapObjectPostRegisterEvent registerEvent = new MapObjectPostRegisterEvent(addedMapObject);
    MinecraftServer.getGlobalEventHandler().call(registerEvent);
  }

  private void handlePostRemove(@NotNull MapObjectToUnregister removedMapObject) {
    MapObject mapObject = removedMapObject.mapObject();
    if (mapObject instanceof Bindable bindable) {
      for (Bindable boundObject : bindable.boundObjects()) {
        boundObject.handleTargetUnregister(this);
      }
      bindable.boundObjects().clear();
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
  public void executeForMembers(@NotNull Consumer<Member> consumer) {
    for (Member member : owner().members()) {
      consumer.accept(member);
    }
    // TODO: spectators
  }

  @Override
  public @NotNull Instance instance() {
    return this.instance;
  }

  @Override
  public void load() {
    InstanceManager instanceManager = MinecraftServer.getInstanceManager();
    String worldPath = "template/maps/game";
    this.instance = instanceManager.createInstanceContainer(DimensionType.OVERWORLD, new AnvilLoader(worldPath));
    this.instance.enableAutoChunkLoad(true);
    this.instance.loadChunk(0, 0);
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
