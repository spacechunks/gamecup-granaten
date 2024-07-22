package space.chunks.gamecup.dgr.launcher.profiler;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @author Nico_ND1
 */
@Singleton
public class ApplicationProfilerCommand extends Command {
  private final ProfilerRepository profilerRepository;
  private final Profiler<?> minecraftServerProfiler;

  @Inject
  public ApplicationProfilerCommand(ProfilerRepository profilerRepository, @Named("minecraftServer") Profiler minecraftServerProfiler) {
    super("profiler");
    this.profilerRepository = profilerRepository;
    this.minecraftServerProfiler = minecraftServerProfiler;

    setDefaultExecutor(printOverview());

    addSyntax(printThreads(), ArgumentType.Literal("threads"));
    addSyntax(addThreadMonitor(), ArgumentType.Literal("threads"), ArgumentType.Literal("addmonitor"), ArgumentType.String("name"));

    addSyntax(printTPS(), ArgumentType.Literal("tps"));
    addSyntax(printServerTPS(), ArgumentType.Literal("tps"), ArgumentType.Literal("server"));
    addSyntax(printSessionTPS(), ArgumentType.Literal("tps"), ArgumentType.Literal("session"));

    addSyntax(printMemory(), ArgumentType.Literal("memory"));

    addSyntax(runGC(), ArgumentType.Literal("gc"));
  }

  private CommandExecutor runGC() {
    return (commandSender, commandContext) -> {
      commandSender.sendMessage(Component.text("Initiating garbage collection..."));
      System.gc();
      commandSender.sendMessage(Component.text("Garbage collection initiated."));
    };
  }

  private CommandExecutor printTPS() {
    return (commandSender, commandContext) -> {
      List<Duration> durations = List.of(TimeUnit.SECOND.getDuration().multipliedBy(15), TimeUnit.SECOND.getDuration().multipliedBy(60), TimeUnit.MINUTE.getDuration().multipliedBy(5));
      Component component = Component.text("TPS-Server (15s, 60s, 5m): ");
      for (Duration duration : durations) {
        ProfilingSnapshot snapshot = this.minecraftServerProfiler.snapshot(duration);
        component = component.append(Component.text(String.format("%.2f", snapshot.ticksPerSecond()))
            .hoverEvent(HoverEvent.showText(hoverText(snapshot))));
        if (duration != durations.get(durations.size()-1)) {
          component = component.append(Component.text(", "));
        }
      }

      Map<Duration, SessionCounter> sessionCounter = new HashMap<>();
      for (Duration duration : durations) {
        sessionCounter.put(duration, new SessionCounter(new AtomicDouble(), new AtomicDouble()));
      }

      Collection<SessionProfiler> sessions = this.profilerRepository.sessionProfilers();
      for (SessionProfiler profiler : sessions) {
        for (Duration duration : durations) {
          ProfilingSnapshot snapshot = profiler.snapshot(duration);
          SessionCounter counter = sessionCounter.get(duration);
          counter.totalTPSCounter.addAndGet(snapshot.ticksPerSecond());
          counter.totalTickDurationCounter.addAndGet(snapshot.averageTickDuration());
        }
      }

      Component sessionComponent = Component.text("TPS-Sessions (avg.) (15s, 60s, 5m): ");
      for (Duration duration : durations) {
        SessionCounter counter = sessionCounter.get(duration);
        double averageTPS = counter.totalTPSCounter.get() / sessions.size();
        double averageTickDuration = counter.totalTickDurationCounter.get() / sessions.size();

        sessionComponent = sessionComponent.append(Component.text(String.format("%.2f", averageTPS))
            .hoverEvent(HoverEvent.showText(Component.text("Average tick duration: "+String.format("%.2f", averageTickDuration)))));
        if (duration != durations.get(durations.size()-1)) {
          sessionComponent = sessionComponent.append(Component.text(", "));
        }
      }

      commandSender.sendMessage(component);
      commandSender.sendMessage(sessionComponent);
    };
  }

  private record SessionCounter(
      AtomicDouble totalTPSCounter,
      AtomicDouble totalTickDurationCounter
  ) {
  }

  private CommandExecutor printServerTPS() {
    return (commandSender, commandContext) -> {
      ProfilingSnapshot snapshot5sec = this.minecraftServerProfiler.snapshot(TimeUnit.SECOND.getDuration().multipliedBy(15));
      ProfilingSnapshot snapshot60sec = this.minecraftServerProfiler.snapshot(TimeUnit.SECOND.getDuration().multipliedBy(60));
      ProfilingSnapshot snapshot5min = this.minecraftServerProfiler.snapshot(TimeUnit.MINUTE.getDuration().multipliedBy(5));

      commandSender.sendMessage(Component.text("Server TPS (15s, 60s, 5m): ")
          .append(Component.text(String.format("%.2f", snapshot5sec.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot5sec))))
          .appendSpace()
          .append(Component.text(String.format("%.2f", snapshot60sec.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot60sec))))
          .appendSpace()
          .append(Component.text(String.format("%.2f", snapshot5min.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot5min))))
      );
    };
  }

  private CommandExecutor printSessionTPS() {
    return (commandSender, commandContext) -> {
      if (!(commandSender instanceof Player player)) {
        return;
      }

      Optional<SessionProfiler> optionalSession = this.profilerRepository.findFor(player);
      if (optionalSession.isEmpty()) {
        player.sendMessage("No session present.");
        return;
      }

      SessionProfiler profiler = optionalSession.get();
      ProfilingSnapshot snapshot5sec = profiler.snapshot(TimeUnit.SECOND.getDuration().multipliedBy(15));
      ProfilingSnapshot snapshot60sec = profiler.snapshot(TimeUnit.SECOND.getDuration().multipliedBy(60));
      ProfilingSnapshot snapshot5min = profiler.snapshot(TimeUnit.MINUTE.getDuration().multipliedBy(5));

      commandSender.sendMessage(Component.text("Session TPS (15s, 60s, 5m): ")
          .append(Component.text(String.format("%.2f", snapshot5sec.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot5sec))))
          .appendSpace()
          .append(Component.text(String.format("%.2f", snapshot60sec.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot60sec))))
          .appendSpace()
          .append(Component.text(String.format("%.2f", snapshot5min.ticksPerSecond())).hoverEvent(HoverEvent.showText(hoverText(snapshot5min))))
      );
    };
  }

  private @NotNull Component hoverText(@NotNull ProfilingSnapshot snapshot) {
    Component extra = Component.empty();
    if (snapshot instanceof MinecraftServerProfilingSnapshot serverSnapshot) {
      extra = Component.text("Average acquisition duration: "+String.format("%.4f", serverSnapshot.averageAcquisitionDuration())).appendNewline()
          .append(Component.text("Min acquisition duration: "+String.format("%.4f", serverSnapshot.minAcquisitionDuration())).appendNewline())
          .append(Component.text("Max acquisition duration: "+String.format("%.4f", serverSnapshot.maxAcquisitionDuration())).appendNewline());
    }

    return Component.text("Average duration: "+String.format("%.2f", snapshot.averageTickDuration())).appendNewline()
        .append(Component.text("Min duration: "+String.format("%.2f", snapshot.minTickDuration())).appendNewline())
        .append(Component.text("Max duration: "+String.format("%.2f", snapshot.maxTickDuration())).appendNewline())
        .append(extra);
  }

  private CommandExecutor printThreads() {
    return (commandSender, commandContext) -> {
      BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();
      commandSender.sendMessage(benchmarkManager.getCpuMonitoringMessage());
    };
  }

  private CommandExecutor addThreadMonitor() {
    return (commandSender, commandContext) -> {
      String name = commandContext.get("name");
      BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();
      benchmarkManager.addThreadMonitor(name);
      commandSender.sendMessage(Component.text("Added thread monitor: "+name));
    };
  }

  private CommandExecutor printMemory() {
    return (commandSender, commandContext) -> {
      Runtime runtime = Runtime.getRuntime();
      long usedMemory = runtime.totalMemory()-runtime.freeMemory();

      long usedMemoryMB = usedMemory / (1024 * 1024);
      long maxMemoryMB = runtime.maxMemory() / (1024 * 1024);
      double usedMemoryPercentage = (double) usedMemory / runtime.maxMemory() * 100.0D;

      commandSender.sendMessage(Component.text("Memory usage: "+usedMemoryMB+"MB / "+maxMemoryMB+"MB ("+String.format("%.2f", usedMemoryPercentage)+"%)"));
    };
  }

  private CommandExecutor printOverview() {
    return (commandSender, commandContext) -> {
      commandSender.sendMessage("Profiler commands:");
      for (String syntaxesString : this.getSyntaxesStrings()) {
        commandSender.sendMessage(syntaxesString);
      }
    };
  }
}
