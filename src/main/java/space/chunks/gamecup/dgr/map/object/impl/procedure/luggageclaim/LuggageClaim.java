package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public class LuggageClaim extends AbstractProcedure<LuggageClaimConfig> implements Procedure {
  private List<LuggageClaimLineEntry> line;

  @Override
  protected @NotNull Class<LuggageClaimConfig> configClass() {
    return LuggageClaimConfig.class;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    Animation animation = new LuggageClaimAnimation(this);
    animation(animation);
    parent.queueMapObjectRegister(animation);
  }

  @Override
  public @NotNull PassengerQueue createPassengerQueue() {
    Instance instance = this.parent.instance();
    Pos lineStartPos = this.config.lineStartPos();
    Direction lineStartWaitingDirection = this.config.lineStartWaitingDirection();
    Direction lineStartDiscorverInitDirection = this.config.lineStartDiscoverInitDirection();

    this.line = new ArrayList<>();
    PassengerQueue passengerQueue = super.createPassengerQueue();

    instance.loadChunk(lineStartPos);
    recursiveLineDiscover(lineStartPos, lineStartPos, instance.getBlock(lineStartPos), lineStartDiscorverInitDirection, lineStartWaitingDirection, passengerQueue, null);
    Collections.shuffle(passengerQueue.waitingSlots());
    return passengerQueue;
  }

  private void recursiveLineDiscover(
      Pos startPos, Pos currentPos, Block blockToTest,
      Direction lastDirection, Direction waitingDirection,
      PassengerQueue passengerQueue, WaitingSlot lastWaitingSlot
  ) {
    Instance instance = this.parent.instance();

    Block block = instance.getBlock(currentPos);
    if (block == blockToTest) {
      WaitingSlot waitingSlot = passengerQueue.createSlot(currentPos.add(waitingDirection.normalX(), waitingDirection.normalY(), waitingDirection.normalZ()).add(0.5, 0, 0.5), null);
      if (lastWaitingSlot != null) {
        lastWaitingSlot.leadingSlot(waitingSlot);
      }

      this.line.add(new LuggageClaimLineEntry(currentPos, lastDirection, waitingSlot, null));

      if (currentPos.add(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ()).equals(startPos)) {
        waitingSlot.leadingSlot(this.line.getFirst().waitingSlot());
      } else {
        recursiveLineDiscover(startPos, currentPos.add(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ()), blockToTest,
            lastDirection, waitingDirection, passengerQueue, waitingSlot);
      }
    } else {
      currentPos = currentPos.sub(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ());
      Direction nextDirection = moveToRight(lastDirection);
      recursiveLineDiscover(startPos, currentPos.add(nextDirection.normalX(), nextDirection.normalY(), nextDirection.normalZ()), blockToTest,
          nextDirection, moveToRight(waitingDirection), passengerQueue, lastWaitingSlot);
    }
  }

  private Direction moveToRight(Direction direction) {
    return switch (direction) {
      case NORTH -> Direction.EAST;
      case EAST -> Direction.SOUTH;
      case SOUTH -> Direction.WEST;
      case WEST -> Direction.NORTH;
      default -> throw new IllegalStateException("Only cardinal directions are allowed");
    };
  }

  @Override
  public void createAnimation(@NotNull Passenger passenger) {

  }

  @Override
  public Pos workPos() {
    return super.workPos();
  }

  @Override
  public Pos exitPos() {
    return super.exitPos();
  }

  @Override
  public boolean allowQueueToMoveUp() {
    return false;
  }
}
