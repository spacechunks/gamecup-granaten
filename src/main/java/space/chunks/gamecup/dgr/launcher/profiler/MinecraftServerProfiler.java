package space.chunks.gamecup.dgr.launcher.profiler;

import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;


/**
 * @author Nico_ND1
 */
public class MinecraftServerProfiler implements Profiler<ServerTickMonitorEvent> {
  private final int maxTicks;
  private final Deque<Tick> ticks;

  public MinecraftServerProfiler(@NotNull Duration maxDuration) {
    this.maxTicks = (int) maxDuration.dividedBy(TimeUnit.SERVER_TICK.getDuration());
    this.ticks = new ArrayDeque<>(this.maxTicks);
  }

  @Override
  public Class<ServerTickMonitorEvent> type() {
    return ServerTickMonitorEvent.class;
  }

  @Override
  public void feed(@NotNull ServerTickMonitorEvent event) {
    if (this.ticks.size() >= this.maxTicks) {
      this.ticks.removeFirst();
    }

    TickMonitor tickMonitor = event.getTickMonitor();
    Tick tick = new Tick(System.currentTimeMillis(), tickMonitor.getTickTime(), tickMonitor.getAcquisitionTime());
    this.ticks.offerLast(tick);
  }

  @Override
  public @NotNull ProfilingSnapshot snapshot(@NotNull Duration duration) {
    long minTime = System.currentTimeMillis()-duration.toMillis();

    Iterator<Tick> tickIterator = this.ticks.descendingIterator();
    double minTickDuration = Double.MAX_VALUE;
    double maxTickDuration = Double.MIN_VALUE;
    double totalTickDuration = 0;
    double minAcquisitionTime = Double.MAX_VALUE;
    double maxAcquisitionTime = Double.MIN_VALUE;
    double totalAcquisitionTime = 0;
    int totalTicks = 0;
    while (tickIterator.hasNext()) {
      Tick tick = tickIterator.next();
      if (tick.time < minTime) {
        continue;
      }

      double tickDuration = tick.duration;
      if (tickDuration < minTickDuration) {
        minTickDuration = tickDuration;
      }
      if (tickDuration > maxTickDuration) {
        maxTickDuration = tickDuration;
      }
      totalTickDuration += tickDuration;

      double acquisitionTime = tick.acquisitionTime;
      if (acquisitionTime < minAcquisitionTime) {
        minAcquisitionTime = acquisitionTime;
      }
      if (acquisitionTime > maxAcquisitionTime) {
        maxAcquisitionTime = acquisitionTime;
      }

      totalAcquisitionTime += acquisitionTime;
      totalTicks++;
    }

    double ticksPerSecond = totalTicks / (double) duration.toSeconds();
    return new MinecraftServerProfilingSnapshot(
        duration,
        ticksPerSecond,
        totalTickDuration / (double) totalTicks,
        maxTickDuration,
        minTickDuration,
        totalAcquisitionTime / (double) totalTicks,
        maxAcquisitionTime,
        minAcquisitionTime
    );
  }

  private record Tick(long time, double duration, double acquisitionTime) {
  }
}
