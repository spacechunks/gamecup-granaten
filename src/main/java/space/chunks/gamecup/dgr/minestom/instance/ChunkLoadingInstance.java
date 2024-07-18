package space.chunks.gamecup.dgr.minestom.instance;

import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author Nico_ND1
 */
public class ChunkLoadingInstance extends InstanceContainer {
  public ChunkLoadingInstance(@NotNull DynamicRegistry.Key<DimensionType> dimensionType, @Nullable IChunkLoader loader) {
    super(UUID.randomUUID(), dimensionType, loader);
  }

  @Override
  public @Nullable Block getBlock(int x, int y, int z, @NotNull Block.Getter.Condition condition) {
    int chunkX = ChunkUtils.getChunkCoordinate(x);
    int chunkZ = ChunkUtils.getChunkCoordinate(z);
    if (!isChunkLoaded(chunkX, chunkZ)) {
      try {
        loadChunk(chunkX, chunkZ).get(250L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        throw new RuntimeException(e);
      }
    }
    return super.getBlock(x, y, z, condition);
  }
}
