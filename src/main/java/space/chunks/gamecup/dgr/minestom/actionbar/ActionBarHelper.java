package space.chunks.gamecup.dgr.minestom.actionbar;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Nico_ND1
 */
@Log4j2
public final class ActionBarHelper {
  private final Cache<Object, Object> activeBars = CacheBuilder.newBuilder()
      .expireAfterAccess(3250L, TimeUnit.MILLISECONDS)
      .build();

  public <K, V> void sendActionBar(@NotNull Audience audience, @NotNull K key, @NotNull ValueSupplier<K, V> valueSupplier, @NotNull ContentSupplier<K, V> contentSupplier) {
    AtomicBoolean fresh = new AtomicBoolean(false);
    try {
      V value = (V) this.activeBars.get(key, () -> {
        fresh.set(true);
        return valueSupplier.supply(key, null);
      });
      if (!fresh.get()) {
        value = valueSupplier.supply(key, value);
      }

      Component content = contentSupplier.supply(key, value);
      audience.sendActionBar(content);
    } catch (ExecutionException e) {
      log.error("Failed to get value for key {}", key, e);
    }
  }

  public interface ValueSupplier<K, V> {
    @NotNull
    V supply(@NotNull K key, @Nullable V previousValue);
  }

  public interface ContentSupplier<K, V> {
    @NotNull
    Component supply(@NotNull K key, @NotNull V value);
  }
}
