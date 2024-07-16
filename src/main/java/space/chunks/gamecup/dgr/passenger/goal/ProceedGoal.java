package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;


/**
 * @author Nico_ND1
 */
public class ProceedGoal extends GoalSelector {
  private final Passenger passenger;

  public ProceedGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    PassengerTask task = this.passenger.task();
    return task != null && task.state() == PassengerTask.State.PROCEED;
  }

  @Override
  public void start() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();

    this.passenger.setPathTo(procedure.exitPos());
    System.out.println("move to exit pos: " + procedure.exitPos());
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    return this.passenger.entityUnsafe().isPathComplete();
  }

  @Override
  public void end() {
    this.passenger.findNextTask();
  }
}
