package space.chunks.gamecup.dgr.map.object.impl.procedure.seats;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;


/**
 * @author Nico_ND1
 */
public class SeatScanner extends AbstractMapObject<SeatScannerConfig> implements MapObject {
  @Override
  @NotNull
  public Class<SeatScannerConfig> configClass() {
    return SeatScannerConfig.class;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    MapObjectTypeRegistry objectTypeRegistry = parent.objectTypes();

    Vec min = this.config.min();
    Vec max = this.config.max();

    Instance instance = parent.instance();
    int i = 0;
    for (int x = min.blockX(); x <= max.blockX(); x++) {
      for (int y = min.blockY(); y <= max.blockY(); y++) {
        for (int z = min.blockZ(); z <= max.blockZ(); z++) {
          Block block = instance.getBlock(x, y, z);
          if (block.registry().id() != Block.PURPUR_STAIRS.id()) {
            continue;
          }

          java.util.Map<String, String> properties = block.properties();
          if (!properties.containsKey("facing")) {
            continue;
          }

          String facing = properties.get("facing");
          Direction direction = Direction.valueOf(facing.toUpperCase()).opposite();

          Pos workPos = new Pos(x+0.5D, y, z+0.5D).add(direction.normalX(), direction.normalY(), direction.normalZ());
          Pos seatPos = new Pos(x+0.5D, y-1.5D, z+0.5D);
          MapObject seat = objectTypeRegistry.create("seat", new SeatConfig("seat_"+(i++), direction, null, null, workPos, seatPos, null));
          parent.queueMapObjectRegister(seat);
          // TODO: bind seats to Destinations
        }
      }
    }

    parent.queueMapObjectUnregister(this);
  }
}
