package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class JoinProcedureQueueGoal extends GoalSelector {
  private final Passenger passenger;
  private boolean initiated;

  public JoinProcedureQueueGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    PassengerTask task = this.passenger.task();
    return task != null && task.state() == State.JOIN_QUEUE;
  }

  @Override
  public void start() {
  }

  @Override
  public void tick(long l) {
    if (this.initiated) {
      return;
    }

    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    PassengerQueue passengerQueue = procedure.passengerQueue();
    if (passengerQueue != null) {
      WaitingSlot waitingSlot = passengerQueue.occupyNextSlot(this.passenger);
      if (waitingSlot != null) {
        this.passenger.setPathTo(waitingSlot.position());
        this.initiated = true;
      }
    }
  }

  @Override
  public boolean shouldEnd() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    PassengerQueue passengerQueue = procedure.passengerQueue();
    if (passengerQueue == null) {
      return true;
    }

    return passengerQueue.findWaitingSlot(this.passenger).isPresent() && this.passenger.entityUnsafe().isPathComplete();
  }

  @Override
  public void end() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    if (procedure.passengerQueue() == null) {
      task.state(State.MOVE_TO_WORK_POS);
    } else {
      task.state(State.WAIT_IN_QUEUE);
    }

    this.initiated = false;
  }
}
