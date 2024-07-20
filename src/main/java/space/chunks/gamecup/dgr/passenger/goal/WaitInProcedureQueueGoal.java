package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.animation.DummyAnimation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;


/**
 * @author Nico_ND1
 */
public class WaitInProcedureQueueGoal extends GoalSelector {
  private final Passenger passenger;
  private WaitingSlot waitingSlot;
  private boolean firstTickDone; // we skip the randomizer in the first tick, to prevent standing still

  public WaitInProcedureQueueGoal(@NotNull Passenger passenger) {
    super(passenger.entityUnsafe());
    this.passenger = passenger;
  }

  @Override
  public boolean shouldStart() {
    PassengerTask task = this.passenger.task();
    return task != null && task.state() == State.WAIT_IN_QUEUE;
  }

  @Override
  public void start() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    PassengerQueue passengerQueue = procedure.passengerQueue();
    this.waitingSlot = passengerQueue.findWaitingSlot(this.passenger).orElseThrow(() -> new IllegalStateException("No waiting slot found for passenger"));

    this.firstTickDone = false;
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    if (!shouldStart()) {
      return true;
    }

    if (!this.passenger.entityUnsafe().isPathComplete()) {
      return false;
    }

    if (this.firstTickDone && Math.random() > 0.223D) {
      return false;
    }
    this.firstTickDone = true;

    WaitingSlot leadingWaitingSlot = this.waitingSlot.leadingSlot();
    PassengerTask task = this.passenger.task();
    assert task != null;

    if (leadingWaitingSlot == null) {
      if (task.procedure().animation() == null) {
        task.procedure().animation(new DummyAnimation()); // reserve animation slot while we move to work pos

        this.waitingSlot.free();
        task.state(State.MOVE_TO_WORK_POS);
        return true;
      }
      return false;
    }

    if (!task.procedure().allowQueueToMoveUp()) {
      return false;
    }

    if (leadingWaitingSlot.tryOccupy(this.passenger)) {
      this.waitingSlot.free();
      this.waitingSlot = leadingWaitingSlot;

      this.passenger.setPathTo(leadingWaitingSlot.position());
    }
    return false;
  }

  @Override
  public void end() {
    this.firstTickDone = false;
    this.waitingSlot = null;
  }
}
