package space.chunks.gamecup.dgr.minestom.pathfinding;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.pathfinding.PNode;
import net.minestom.server.entity.pathfinding.generators.NodeGenerator;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.Set;


/**
 * @author Nico_ND1
 */
public class SimpleGroundNodeGenerator implements NodeGenerator {
  private PNode tempNode = null;

  @Override
  public @NotNull Collection<? extends PNode> getWalkable(@NotNull Instance instance, @NotNull Set<PNode> visited, @NotNull PNode current, @NotNull Point goal, @NotNull BoundingBox boundingBox) {
    Collection<PNode> nearby = new ArrayDeque<>();
    this.tempNode = new PNode(0, 0, 0, 0, 0, current);

    int stepSize = (int) Math.max(Math.floor(boundingBox.width() / 2), 1);
    if (stepSize < 1) {
      stepSize = 1;
    }

    for (int x = -stepSize; x <= stepSize; ++x) {
      for (int z = -stepSize; z <= stepSize; ++z) {
        if (x == 0 && z == 0) {
          continue;
        }
        double cost = Math.sqrt(x * x+z * z) * 0.98;

        double floorPointX = current.blockX()+0.5+x;
        double floorPointY = current.blockY();
        double floorPointZ = current.blockZ()+0.5+z;
        var floorPoint = new Vec(floorPointX, floorPointY, floorPointZ);

        var nodeWalk = createWalk(instance, floorPoint, boundingBox, cost, current, goal, visited);
        if (nodeWalk != null && !visited.contains(nodeWalk)) {
          nearby.add(nodeWalk);
        }
      }
    }

    return nearby;
  }

  private PNode createWalk(Instance instance, Point point, BoundingBox boundingBox, double cost, PNode start, Point goal, Set<PNode> closed) {
    Block pointBlock = instance.getBlock(point.add(0, -1, 0));
    if (pointBlock == Block.LIGHT_GRAY_WOOL) {
      cost *= 0.9;
    }

    var n = newNode(start, cost, point, goal);
    if (closed.contains(n)) {
      return null;
    }

    if (!canMoveTowards(instance, new Vec(start.x(), start.y(), start.z()), point, boundingBox)) {
      return null;
    }
    return n;
  }

  @Override
  public boolean canMoveTowards(@NotNull Instance instance, @NotNull Point start, @NotNull Point end, @NotNull BoundingBox boundingBox) {
    return instance.getBlock(end) == Block.AIR && instance.getBlock(end.add(0, 1, 0)) == Block.AIR && instance.getBlock(end.sub(0, 1, 0)) != Block.AIR;
  }

  private PNode newNode(PNode current, double cost, Point point, Point goal) {
    tempNode.setG(current.g()+cost);
    tempNode.setH(heuristic(point, goal));
    tempNode.setPoint(point.x(), point.y(), point.z());

    var newNode = tempNode;
    tempNode = new PNode(0, 0, 0, 0, 0, PNode.NodeType.WALK, current);

    return newNode;
  }

  @Override
  public boolean hasGravitySnap() {
    return false;
  }

  @Override
  public @NotNull OptionalDouble gravitySnap(@NotNull Instance instance, double v, double v1, double v2, @NotNull BoundingBox boundingBox, double v3) {
    return OptionalDouble.empty();
  }
}
