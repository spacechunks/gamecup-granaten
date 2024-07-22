package space.chunks.gamecup.dgr.launcher.profiler;

import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;


/**
 * @author Nico_ND1
 */
public class SessionProfiler implements Profiler<InstanceTickEvent> {
  private final Instance instance;
  private final int maxTicks;
  private final Deque<Tick> ticks;

  public SessionProfiler(@NotNull Instance instance, @NotNull Duration maxDuration) {
    this.instance = instance;
    this.maxTicks = (int) maxDuration.dividedBy(TimeUnit.SERVER_TICK.getDuration());
    this.ticks = new ArrayDeque<>(this.maxTicks);
  }

  @Override
  public Class<InstanceTickEvent> type() {
    return InstanceTickEvent.class;
  }

  @Override
  public void feed(@NotNull InstanceTickEvent event) {
    Instance eventInstance = event.getInstance();
    if (this.instance == null || !this.instance.getUniqueId().equals(eventInstance.getUniqueId())) {
      return;
    }

    if (this.ticks.size() >= this.maxTicks) {
      this.ticks.removeFirst();
    }

    Tick tick = new Tick(System.currentTimeMillis(), event.getDuration());
    this.ticks.offerLast(tick);
  }

  @Override
  public @NotNull ProfilingSnapshot snapshot(@NotNull Duration duration) {
    long minTime = System.currentTimeMillis()-duration.toMillis();

    Iterator<Tick> tickIterator = this.ticks.descendingIterator();
    long minDuration = Long.MAX_VALUE;
    long maxDuration = Long.MIN_VALUE;
    long totalDuration = 0;
    int totalTicks = 0;
    while (tickIterator.hasNext()) {
      Tick tick = tickIterator.next();
      if (tick.time < minTime) {
        continue;
      }

      long tickDuration = tick.duration;
      if (tickDuration < minDuration) {
        minDuration = tickDuration;
      }
      if (tickDuration > maxDuration) {
        maxDuration = tickDuration;
      }

      totalDuration += tickDuration;
      totalTicks++;
    }

    double ticksPerSecond = totalTicks / (double) duration.toSeconds();
    return new ProfilingSnapshotImpl(
        duration,
        ticksPerSecond,
        totalDuration / (double) totalTicks,
        maxDuration,
        minDuration
    );
  }

  private record Tick(long time, long duration) {
  }
}
