package space.chunks.gamecup.dgr.launcher.profiler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.log4j.Log4j2;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;


/**
 * @author Nico_ND1
 */
@Log4j2
public final class ApplicationProfiler {
  private final ProfilerRepository profilerRepository;
  private final Profiler<ServerTickMonitorEvent> minecraftServerProfiler;
  private final ApplicationProfilerCommand command;

  @Inject
  public ApplicationProfiler(
      @NotNull ProfilerRepository profilerRepository,
      @NotNull @Named("minecraftServer") Profiler minecraftServerProfiler,
      @NotNull ApplicationProfilerCommand command
  ) {
    this.profilerRepository = profilerRepository;
    this.minecraftServerProfiler = minecraftServerProfiler;
    this.command = command;
  }

  public void register() {
    log.info("Registering application profiler");

    BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();
    benchmarkManager.enable(Duration.of(10, TimeUnit.SECOND));
    log.info("Enabled benchmark manager with a duration of 10 seconds");

    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
    eventHandler.addListener(EventListener.of(ServerTickMonitorEvent.class, this.minecraftServerProfiler::feed));

    eventHandler.addListener(EventListener.of(InstanceTickEvent.class, event -> {
      for (SessionProfiler profiler : this.profilerRepository.sessionProfilers()) {
        profiler.feed(event);
      }
    }));

    log.info("Registered event listeners for application profiler");

    MinecraftServer.getCommandManager().register(this.command);
    log.info("Registered command for application profiler");
  }

}
