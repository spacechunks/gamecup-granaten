package space.chunks.gamecup.dgr.passenger;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.extern.log4j.Log4j2;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.thread.Acquirable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.procedure.Procedure;
import space.chunks.gamecup.dgr.minestom.npc.NPCEntity;
import space.chunks.gamecup.dgr.minestom.pathfinding.SimpleGroundNodeFollower;
import space.chunks.gamecup.dgr.minestom.pathfinding.SimpleGroundNodeGenerator;
import space.chunks.gamecup.dgr.passenger.goal.FindNextProcedureOrLeaveGoal;
import space.chunks.gamecup.dgr.passenger.goal.JoinProcedureQueueGoal;
import space.chunks.gamecup.dgr.passenger.goal.MoveToWorkPosGoal;
import space.chunks.gamecup.dgr.passenger.goal.ProceedGoal;
import space.chunks.gamecup.dgr.passenger.goal.WaitInProcedureQueueGoal;
import space.chunks.gamecup.dgr.passenger.goal.WorkGoal;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
@Log4j2
public class PassengerImpl implements Passenger {
  private static final String[] RANDOM_NAMES = {"Olaf", "Bernhard", "Marlon", "Hans", "Klaus", "Günther", "Hans"};

  private final PassengerConfig config;
  private final Game game;
  private final NPCEntity npc;
  private final Pos spawnPosition;
  private final Destination destination;
  private final String name;
  private final Queue<PassengerTask> taskQueue;

  private Map map;
  private PassengerTask task;
  private int patience;

  @AssistedInject
  public PassengerImpl(
      Game game,
      @Assisted PassengerConfig config
  ) {
    this.config = config;
    this.game = game;
    this.name = RANDOM_NAMES[(int) (Math.random() * RANDOM_NAMES.length)];
    this.spawnPosition = config.spawnPosition();
    this.npc = new NPCEntity(UUID.randomUUID(), this.name, new PlayerSkin(
        "ewogICJ0aW1lc3RhbXAiIDogMTcyMDkwODE0OTgzOCwKICAicHJvZmlsZUlkIiA6ICI0NmNhODkyZTY4ODA0YThmYjFkYzkwYjg0ZTY5ZjVmZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPbG8xNjA2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMTI2MzZiN2JkZDk3MTcxOTdlY2EwNmM2ZmExNWY1ZGY1Mzg5YjAxMTVkZmYxZjFhYTc5NWZhM2I2Y2Y5YjciCiAgICB9CiAgfQp9",
        "mICpZGBww7LAsWApc/YHeR46/bqe7xGOPBF07+nLJF0PuupKpGftogtncuchnWSV6yakF3fQy1JVKGwJEQCjzUpUHzIzph8zmN2zZJkSKpWB3MJF5BxTrItSKtYPwoOz27XMtnkPdqsjmcQrjkaZtRJ4JJpIqntwTf9wb8+lNh9YqdwH5xkBFUwJjaAKQrmmOJLrwt04uOt/Vb78EgwXUpF4Ij4wo0b/ATVeyXVx8pOOs5ChaDKzncXv+wXgyoT16A6NikyPthpRgTw5nQX6Y/m28irf4YMqZUlQGitRoQ/zm31JdLe12zplpaeKUvTjX96MkwZxMyA7FaFhVF1Mko7Qwafxh+sXAh3SDnlbRdK7qimYz8XcUWf3KxI6R25Iv42sp8IbxTioiLq4U4Jxg8fdFPzXmwqnZO42WXFAXD5F841+MAEJ5BieCMolpMuC9R2JMHXSSmrU2j3OlsOs5F6YZK7g+mmImItPElDOxY+eekY4IZnVwXQR9e138cqZPjPEDqMMMZgOriZ9ErvsNYl/BxOT3DpFUeYOepMD+lGbr308e/aln1ERgr8O73OJ9idLVMIXD7Vghc6UAZ7eGfipD+ODvRegoYkZ+tP0sl34QZ3Jv0IPFjgT2+s4UMJ0q10IAcbMu84WETkct04RUEE68C2bG/nCOLYi0ZWoKJk="
    ));
    this.npc.getNavigator().setNodeGenerator(SimpleGroundNodeGenerator::new);
    this.npc.getNavigator().setNodeFollower(() -> new SimpleGroundNodeFollower(this.npc));

    this.destination = config.destination();
    this.taskQueue = new ArrayDeque<>(config.procedures().length);
    queueTasks(config);

    log.info("Created passenger {} with config: {}", this.name, config);
  }

  public void queueTasks(@NotNull PassengerConfig config) {
    for (String procedureName : config.procedures()) {
      PassengerTask task = new PassengerTask(procedureName);
      this.taskQueue.add(task);
    }
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
  }

  @Override
  public @NotNull PassengerConfig config() {
    return this.config;
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
    Procedure procedure = (Procedure) map().objects().find(this.task.procedureName()).orElseThrow(() -> new NullPointerException("Procedure not found: "+this.task.procedureName()));
    this.task.procedure(procedure);
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    this.map = parent;

    this.npc.setInstance(parent.instance(), this.spawnPosition);
    this.npc.addAIGroup(
        List.of(
            new FindNextProcedureOrLeaveGoal(this),
            new JoinProcedureQueueGoal(this),
            new WaitInProcedureQueueGoal(this),
            new MoveToWorkPosGoal(this),
            new WorkGoal(this),
            new ProceedGoal(this)
        ),
        List.of()
    );

    log.info("Registered passenger {} at {}", this.name, this.spawnPosition);
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    this.npc.remove();
    log.info("Unregistered passenger {} because {}", this.name, reason);
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }
}
