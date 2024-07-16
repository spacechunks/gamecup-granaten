package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject.UnregisterReason;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class FindNextProcedureOrLeaveGoal extends GoalSelector {
  private final Passenger passenger;
  private int tickDelay;

  public FindNextProcedureOrLeaveGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    return this.passenger.task() == null;
  }

  @Override
  public void start() {
    this.passenger.findNextTask();

    PassengerTask task = this.passenger.task();
    if (task == null) {
      this.passenger.setPathTo(this.passenger.config().leavePosition());
    } else {
      Procedure procedure = task.procedure();
      PassengerQueue passengerQueue = procedure.passengerQueue();
      if (passengerQueue != null) {
        this.passenger.setPathTo(passengerQueue.startingPosition());
      } else {
        task.state(State.MOVE_TO_WORK_POS);
      }
    }

    this.tickDelay = 5;
  }

  @Override
  public void tick(long l) {
    if (this.tickDelay > 0) {
      this.tickDelay--;
    }
  }

  @Override
  public boolean shouldEnd() {
    if (this.tickDelay > 0) {
      return false;
    }
    return this.passenger.task() != null || this.passenger.entityUnsafe().isPathComplete();
  }

  @Override
  public void end() {
    if (this.passenger.task() == null) {
      this.passenger.map().queueMapObjectUnregister(this.passenger, UnregisterReason.PASSENGER_NO_PROCEDURE_TARGET);
    }
  }
}
