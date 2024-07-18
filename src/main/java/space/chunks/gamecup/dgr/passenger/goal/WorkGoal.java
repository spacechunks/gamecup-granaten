package space.chunks.gamecup.dgr.passenger.goal;

import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class WorkGoal extends GoalSelector {
  private final Passenger passenger;
  private UUID workAnimationId;

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
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();

    Animation animation = procedure.createAnimation(this.passenger);
    if (animation != null) {
      this.workAnimationId = animation.contextId();
    }
  }

  @Override
  public void tick(long l) {
  }

  @Override
  public boolean shouldEnd() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    Procedure procedure = task.procedure();
    Animation animation = procedure.animation();
    if (animation != null) {
      return !animation.contextId().equals(this.workAnimationId);
    }
    return true;
  }

  @Override
  public void end() {
    PassengerTask task = this.passenger.task();
    assert task != null;
    task.state(PassengerTask.State.PROCEED);
  }
}
