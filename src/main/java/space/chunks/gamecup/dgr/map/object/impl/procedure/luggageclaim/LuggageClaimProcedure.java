package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public class LuggageClaimProcedure extends AbstractProcedure<LuggageClaimConfig> implements Procedure {
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
    recursiveLineDiscover(lineStartPos, lineStartPos, instance.getBlock(lineStartPos), new HashSet<>(), lineStartDiscorverInitDirection, lineStartWaitingDirection, 0, passengerQueue, null);
    //fixLineEnding();
    Collections.shuffle(passengerQueue.waitingSlots());
    return passengerQueue;
  }

  private void fixLineEnding() {
    LuggageClaimLineEntry last = this.line.getLast();
    WaitingSlot waitingSlot = last.waitingSlot();
    if (waitingSlot.leadingSlot() == null) {
      LuggageClaimLineEntry first = this.line.getFirst();
      waitingSlot.leadingSlot(first.waitingSlot());
    }
  }

  /*
  Uncomment to visualize the line:
  private int y = 0;
   */

  private void recursiveLineDiscover(
      Pos startPos, Pos currentPos, Block blockToTest,
      Set<Pos> visitedBlocks,
      Direction lastDirection, Direction waitingDirection,
      int unsuccessfulTries,
      PassengerQueue passengerQueue, WaitingSlot lastWaitingSlot
  ) {
    Instance instance = this.parent.instance();

    if (unsuccessfulTries == 5) {
      return;
    } else if (!visitedBlocks.add(currentPos)) {
      return;
    }

    Block block = instance.getBlock(currentPos);
    if (block == blockToTest) {
      WaitingSlot waitingSlot = passengerQueue.createSlot(currentPos.add(waitingDirection.normalX(), waitingDirection.normalY(), waitingDirection.normalZ()).add(0.5, 0, 0.5), null);
      if (lastWaitingSlot != null) {
        lastWaitingSlot.leadingSlot(waitingSlot);
      }

      this.line.add(new LuggageClaimLineEntry(currentPos, lastDirection, waitingSlot, null));

      /*
      Uncomment to visualize the line:
      instance.setBlock(currentPos.add(0, 2+(this.y++), 0), Block.RED_CONCRETE);
      instance.setBlock(currentPos.add(waitingDirection.normalX(), waitingDirection.normalY(), waitingDirection.normalZ()).add(0, 2+(this.y), 0), Block.BLUE_CONCRETE);
      instance.setBlock(currentPos.add(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ()).add(0, 3+(this.y), 0), Block.YELLOW_CONCRETE);
       */

      if (currentPos.add(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ()).equals(startPos)) {
        waitingSlot.leadingSlot(this.line.getFirst().waitingSlot());
      } else {
        recursiveLineDiscover(startPos, currentPos.add(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ()), blockToTest, visitedBlocks,
            lastDirection, waitingDirection, 0, passengerQueue, waitingSlot);
      }
    } else {
      currentPos = currentPos.sub(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ());
      Direction nextDirection = moveToRight(lastDirection);
      recursiveLineDiscover(startPos, currentPos.add(nextDirection.normalX(), nextDirection.normalY(), nextDirection.normalZ()), blockToTest, visitedBlocks,
          nextDirection, moveToRight(waitingDirection), unsuccessfulTries+1, passengerQueue, lastWaitingSlot);

      nextDirection = moveToLeft(lastDirection);
      recursiveLineDiscover(startPos, currentPos.add(nextDirection.normalX(), nextDirection.normalY(), nextDirection.normalZ()), blockToTest, visitedBlocks,
          nextDirection, moveToLeft(waitingDirection), unsuccessfulTries+1, passengerQueue, lastWaitingSlot);
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

  private Direction moveToLeft(Direction direction) {
    return moveToRight(direction).opposite();
  }

  @Override
  public @Nullable Animation createAnimation(@NotNull Passenger passenger) {
    return this.animation;
  }

  @Override
  public @NotNull Pos workPos() {
    return super.workPos();
  }

  @Override
  public @NotNull Pos exitPos() {
    return super.exitPos();
  }

  @Override
  public boolean allowQueueToMoveUp() {
    return false;
  }

  @Override
  public @NotNull String group() {
    return Procedure.LUGGAGE_CLAIM;
  }
}
