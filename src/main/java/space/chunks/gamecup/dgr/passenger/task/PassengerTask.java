package space.chunks.gamecup.dgr.passenger.task;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.procedure.Procedure;


/**
 * @author Nico_ND1
 */
@Getter
@Setter
@Accessors(fluent=true)
public final class PassengerTask {
  private final String procedureName;
  private Procedure procedure;
  private State state;

  public PassengerTask(@NotNull String procedureName) {
    this.procedureName = procedureName;
    this.state = State.values()[0];
  }

  public enum State {
    JOIN_QUEUE,
    WAIT_IN_QUEUE,
    MOVE_TO_WORK_POS,
    WORK,
    PROCEED
  }
}
