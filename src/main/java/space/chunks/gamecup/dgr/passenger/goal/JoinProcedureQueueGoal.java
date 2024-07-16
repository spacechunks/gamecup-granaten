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
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    PassengerQueue passengerQueue = procedure.passengerQueue();
    WaitingSlot waitingSlot = passengerQueue.occupyNextSlot(this.passenger);
    if (waitingSlot == null) {
      this.passenger.map().queueMapObjectUnregister(this.passenger);
      return;
    }

    this.passenger.setPathTo(waitingSlot.position());
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
    PassengerTask task = this.passenger.task();
    assert task != null;
    task.state(State.WAIT_IN_QUEUE);
  }
}
