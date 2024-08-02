package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
  @NotNull
  public Class<LuggageClaimConfig> configClass() {
    return LuggageClaimConfig.class;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    Animation animation = new LuggageClaimAnimation(this);
    animation.config(this.config);
    animation(animation);
    parent.queueMapObjectRegister(animation);

    passengerQueue();
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
    recursiveLineDiscover(lineStartPos, lineStartPos, instance.getBlock(lineStartPos), new HashSet<>(), lineStartDiscorverInitDirection, lineStartDiscorverInitDirection, lineStartWaitingDirection, 0, passengerQueue, null);

    for (int i = 0; i < this.line.size(); i++) {
      LuggageClaimLineEntry lineEntry = this.line.get(i);
      LuggageClaimLineEntry nextEntry = null;
      if (i+1 < this.line.size()) {
        nextEntry = this.line.get(i+1);
      }

      Entity model = new Entity(EntityType.ITEM_DISPLAY);
      boolean curved = nextEntry != null && nextEntry.direction() != lineEntry.direction();
      model.setNoGravity(true);
      model.editEntityMeta(ItemDisplayMeta.class, meta -> {
        meta.setItemStack(ItemStack.of(Material.PAPER).withCustomModelData(curved ? 13 : 12));
        meta.setScale(new Vec(1, 1.5, 1));
      });
      Direction d = curved ? nextEntry.direction() : null;
      model.setInstance(instance, lineEntry.pos().add(0.5, 0.75, 0.5).withYaw(yaw -> getModelDirection(yaw, curved, lineEntry.direction(), d)));

      lineEntry.model(model);
    }

    Collections.shuffle(passengerQueue.waitingSlots());
    return passengerQueue;
  }

  /*
  Uncomment to visualize the line:
  private int y = 0;
   */

  private void recursiveLineDiscover(
      Pos startPos, Pos currentPos, Block blockToTest,
      Set<Pos> visitedBlocks,
      Direction unchangedLastDirection, Direction lastDirection, Direction waitingDirection,
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

      instance.setBlock(currentPos, Block.BARRIER); // TODO: remove this, because the map will consist of barrier blocks
      this.line.add(new LuggageClaimLineEntry(currentPos, lastDirection, waitingSlot, null, null));

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
            lastDirection, lastDirection, waitingDirection, 0, passengerQueue, waitingSlot);
      }
    } else {
      currentPos = currentPos.sub(lastDirection.normalX(), lastDirection.normalY(), lastDirection.normalZ());
      Direction nextDirection = moveToRight(lastDirection);
      recursiveLineDiscover(startPos, currentPos.add(nextDirection.normalX(), nextDirection.normalY(), nextDirection.normalZ()), blockToTest, visitedBlocks,
          lastDirection, nextDirection, moveToRight(waitingDirection), unsuccessfulTries+1, passengerQueue, lastWaitingSlot);

      nextDirection = moveToLeft(lastDirection);
      recursiveLineDiscover(startPos, currentPos.add(nextDirection.normalX(), nextDirection.normalY(), nextDirection.normalZ()), blockToTest, visitedBlocks,
          lastDirection, nextDirection, moveToLeft(waitingDirection), unsuccessfulTries+1, passengerQueue, lastWaitingSlot);
    }
  }

  private double getModelDirection(double yaw, boolean curved, Direction lastDirection, Direction targetDirection) {
    if (curved) {
      return switch (targetDirection) {
        case NORTH -> yaw-90;
        case EAST -> yaw;
        case SOUTH -> yaw+90;
        case WEST -> yaw+180;
        default -> yaw;
      };
    } else {
      return switch (lastDirection) {
        case NORTH, SOUTH -> yaw;
        case WEST, EAST -> yaw+90;
        default -> yaw;
      };
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
