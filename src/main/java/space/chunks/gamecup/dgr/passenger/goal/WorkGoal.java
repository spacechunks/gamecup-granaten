package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;


/**
 * @author Nico_ND1
 */
public class WorkGoal extends GoalSelector {
  private final Passenger passenger;

  public WorkGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    PassengerTask task = this.passenger.task();
    return task != null && task.state() == PassengerTask.State.WORK;
  }

  @Override
  public void start() {
    System.out.println("Start WorkGoal");
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();

    procedure.createAnimation(this.passenger);
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    return procedure.animation() == null;
  }

  @Override
  public void end() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    task.state(PassengerTask.State.PROCEED);
  }
}
