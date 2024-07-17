package space.chunks.gamecup.dgr.passenger.task;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * @author Nico_ND1
 */
@Getter
@Setter
@Accessors(fluent=true)
public final class PassengerTask {
  private final Passenger passenger;
  private final String procedureGroup;
  private Procedure procedure;
  private State state;

  public PassengerTask(@NotNull Passenger passenger, @NotNull String procedureGroup) {
    this.passenger = passenger;
    this.procedureGroup = procedureGroup;
    this.state = State.values()[0];
  }

  public @NotNull Procedure procedure() {
    if (this.procedure == null) {
      this.procedure = findProcedure().orElseThrow();
    }
    return this.procedure;
  }

  private @NotNull Optional<Procedure> findProcedure() {
    Map map = passenger.map();
    List<MapObject> mapObjects = new ArrayList<>(map.objects().allOfGroup(this.procedureGroup));
    Collections.shuffle(mapObjects);
    return mapObjects.stream()
        .filter(mapObject -> mapObject instanceof Procedure)
        .map(Procedure.class::cast)
        .min((o1, o2) -> {
          if (o1.passengerQueue() == null) {
            if (o1.animation() == null) {
              return -1;
            } else if (o2.animation() == null) {
              return 1;
            }
            return 0;
          }

          int o1QueueSize = o1.passengerQueue().size();
          int o2QueueSize = o2.passengerQueue().size();
          if (o1QueueSize == 0) {
            return -1;
          } else if (o2QueueSize == 0) {
            return 1;
          }

          if (o1QueueSize == o2QueueSize || Math.abs(o1QueueSize-o2QueueSize) < 2) {
            return 0;
          }

          if (o1QueueSize < o2QueueSize) {
            return -1;
          }
          return 1;
        });
  }

  public enum State {
    JOIN_QUEUE,
    WAIT_IN_QUEUE,
    MOVE_TO_WORK_POS,
    WORK,
    PROCEED
  }
}
