package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class MoveToWorkPosGoal extends GoalSelector {
  private final Passenger passenger;

  public MoveToWorkPosGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    PassengerTask task = this.passenger.task();
    return task != null && task.state() == State.MOVE_TO_WORK_POS;
  }

  @Override
  public void start() {
    System.out.println("Start MoveToWorkPosGoal");
    PassengerTask task = this.passenger.task();
    assert task != null;

    Procedure procedure = task.procedure();
    getEntityCreature().getNavigator().setPathTo(procedure.workPos());
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    return getEntityCreature().getNavigator().isComplete();
  }

  @Override
  public void end() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    task.state(State.WORK);
  }
}
