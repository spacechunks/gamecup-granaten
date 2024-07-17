package space.chunks.gamecup.dgr.passenger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.minestom.pathfinding.SimpleGroundNodeFollower;
import space.chunks.gamecup.dgr.minestom.pathfinding.SimpleGroundNodeGenerator;
import space.chunks.gamecup.dgr.passenger.goal.FindNextProcedureOrLeaveGoal;
import space.chunks.gamecup.dgr.passenger.goal.JoinProcedureQueueGoal;
import space.chunks.gamecup.dgr.passenger.goal.MoveToWorkPosGoal;
import space.chunks.gamecup.dgr.passenger.goal.ProceedGoal;
import space.chunks.gamecup.dgr.passenger.goal.ProduceTrashGoal;
import space.chunks.gamecup.dgr.passenger.goal.WaitInProcedureQueueGoal;
import space.chunks.gamecup.dgr.passenger.goal.WorkGoal;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentities;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentity;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTaskBuilder;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;


/**
 * @author Nico_ND1
 */
@Log4j2
public class PassengerImpl implements Passenger {
  public static final ItemStack[] BAGGAGE_ITEMS = {
      ItemStack.of(Material.PAPER).withCustomModelData(4),
      ItemStack.of(Material.PAPER).withCustomModelData(5)
  };

  private final PassengerConfig config;
  private final PassengerIdentity identity;
  private final NPCEntity npc;
  private final Pos spawnPosition;
  private final Destination destination;
  private final Queue<PassengerTask> taskQueue;
  private final ItemStack baggage;

  private Map map;
  private PassengerTask task;

  private int patience;
  private int lastShownPatience;

  @AssistedInject
  public PassengerImpl(
      PassengerIdentities identities,
      @Assisted PassengerConfig config
  ) {
    this.config = config;
    this.identity = identities.random(this);
    this.spawnPosition = config.spawnPosition();
    this.npc = new NPCEntity(this.identity.uuid(), this.identity.name(), this.identity.skin());
    this.npc.getNavigator().setNodeGenerator(SimpleGroundNodeGenerator::new);
    this.npc.getNavigator().setNodeFollower(() -> new SimpleGroundNodeFollower(this.npc));
    this.npc.setCustomNameVisible(true);

    if (Math.random() < config.baggageChance()) {
      this.baggage = BAGGAGE_ITEMS[(int) (Math.random() * BAGGAGE_ITEMS.length)];
    } else {
      this.baggage = null;
    }

    this.destination = config.destination();
    this.taskQueue = new ArrayDeque<>();

    this.patience = 10;
  }

  @Inject
  public void queueTasks(@NotNull PassengerTaskBuilder taskBuilder) {
    this.taskQueue.addAll(taskBuilder.createTasks(this));
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
  }

  @Override
  public @NotNull PassengerConfig config() {
    return this.config;
  }

  @Override
  public @NotNull PassengerIdentity identity() {
    return this.identity;
  }

  @Override
  public @NotNull Map map() {
    if (this.map == null) {
      throw new IllegalStateException("MapObject not registered yet");
    }
    return this.map;
  }

  @Override
  public @NotNull Acquirable<? extends EntityCreature> entity() {
    return this.npc.acquirable();
  }

  @Override
  public @NotNull NPCEntity entityUnsafe() {
    return this.npc;
  }

  @Override
  public int patience() {
    return this.patience;
  }

  @Override
  public void losePatience() {
    if (this.patience > 0) {
      this.patience--;
    }
  }

  @Override
  public @NotNull Destination destination() {
    return this.destination;
  }

  @Override
  public @Nullable PassengerTask task() {
    return this.task;
  }

  @Override
  public void findNextTask() {
    if (this.taskQueue.isEmpty()) {
      this.task = null;
      return;
    }

    this.task = this.taskQueue.poll();
  }

  @Override
  public @Nullable ItemStack baggage() {
    return this.baggage;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    this.map = parent;

    this.npc.setInstance(parent.instance(), this.spawnPosition);
    this.npc.addAIGroup(
        List.of(
            new FindNextProcedureOrLeaveGoal(this),
            new JoinProcedureQueueGoal(this),
            new ProduceTrashGoal(this), new WaitInProcedureQueueGoal(this),
            new MoveToWorkPosGoal(this),
            new WorkGoal(this),
            new ProceedGoal(this)
        ),
        List.of()
    );
    if (this.baggage != null && this.destination == Destination.LEAVING) {
      this.npc.setItemInOffHand(this.baggage);
    }
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    this.npc.remove();
  }

  @Override
  public @NotNull String name() {
    return this.identity.name();
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.patience != this.lastShownPatience) {
      this.npc.setCustomName(Component.text("Patience: ").append(Component.text(this.patience)));
      this.lastShownPatience = this.patience;
    }

    if (Math.random() > 0.9) {
      this.patience++;
    }
    return TickResult.CONTINUE;
  }
}
