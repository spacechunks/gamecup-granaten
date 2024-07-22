package space.chunks.gamecup.dgr.launcher.profiler;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;


/**
 * @author Nico_ND1
 */
public record MinecraftServerProfilingSnapshot(
    @NotNull Duration timespan,
    double ticksPerSecond,
    double averageTickDuration,
    double maxTickDuration,
    double minTickDuration,
    double averageAcquisitionDuration,
    double maxAcquisitionDuration,
    double minAcquisitionDuration
) implements ProfilingSnapshot {
}
