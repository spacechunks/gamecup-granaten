package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.MapObject.UnregisterReason;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class FindNextProcedureOrLeaveGoal extends GoalSelector {
  private final Passenger passenger;

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
    System.out.println("Start FindNextProcedureOrLeaveGoal");
    this.passenger.findNextTask();

    if (this.passenger.task() == null) {
      getEntityCreature().getNavigator().setPathTo(this.passenger.config().leavePosition());
    }
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    return this.passenger.task() != null || getEntityCreature().getNavigator().isComplete();
  }

  @Override
  public void end() {
    if (this.passenger.task() == null) {
      this.passenger.map().queueMapObjectUnregister(this.passenger, UnregisterReason.PASSENGER_NO_PROCEDURE_TARGET);
    }
  }
}
