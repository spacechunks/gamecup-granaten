package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.procedure.Procedure;
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
    System.out.println("Start WaitInProcedureQueueGoal");

    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    PassengerQueue passengerQueue = procedure.passengerQueue();
    this.waitingSlot = passengerQueue.findWaitingSlot(this.passenger);
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    if (!getEntityCreature().getNavigator().isComplete()) {
      return false;
    }

    if (Math.random() > 0.09D) {
      return false;
    }

    WaitingSlot leadingWaitingSlot = this.waitingSlot.leadingSlot();
    PassengerTask task = this.passenger.task();
    assert task != null;

    if (leadingWaitingSlot == null) {
      if (task.procedure().animation() == null) {
        System.out.println("We are "+this.waitingSlot+" with "+this.passenger.name()+" and slot "+this.waitingSlot.passenger().name());
        int i = 0;
        for (WaitingSlot slot : this.passenger.task().procedure().passengerQueue().waitingSlots()) {
          System.out.println("I am slot "+(i++)+" "+slot+" "+slot.isOccupied());
        }

        this.waitingSlot.free();
        task.state(State.MOVE_TO_WORK_POS);
        return true;
      }
      return false;
    }

    if (leadingWaitingSlot.isOccupied()) {
      return false;
    }

    this.waitingSlot.free();
    leadingWaitingSlot.occupy(passenger);
    this.waitingSlot = leadingWaitingSlot;

    getEntityCreature().getNavigator().setPathTo(leadingWaitingSlot.position());
    return false;
  }

  @Override
  public void end() {

  }
}
