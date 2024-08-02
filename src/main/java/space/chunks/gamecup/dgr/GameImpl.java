package space.chunks.gamecup.dgr;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.goal.FixedHappyPassengersGameGoal;
import space.chunks.gamecup.dgr.goal.GameGoal;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.phase.WaitingPhase;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandler;
import space.chunks.gamecup.dgr.team.Team;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
@Log4j2
public final class GameImpl implements Game {
  private final GameTickTask tickTask;
  private final GameGoal goal;
  private final PhaseHandler phases;
  private final List<Map> maps;
  private final List<Team> teams;

  @Inject
  public GameImpl(@NotNull GameTickTask tickTask, @NotNull PhaseHandler phases, @NotNull GameConfig config, @NotNull GameFactory factory, @NotNull Provider<Team> teamProvider) {
    this.tickTask = tickTask;
    this.goal = new FixedHappyPassengersGameGoal(this, 500, new int[]{100, 200, 300, 400, 450});
    this.phases = phases;

    log.info("Init game with config: {}", config);
    this.teams = createTeams(config, teamProvider);
    this.maps = createMaps(factory);
  }

  private @NotNull List<Map> createMaps(@NotNull GameFactory factory) {
    List<Map> maps = new ArrayList<>();
    for (Team team : this.teams) {
      Map map = factory.createMap(team);
      maps.add(map);
      team.map(map);
      map.load();
    }

    log.info("Created and loaded {} for {} teams.", maps.size(), this.teams.size());
    return maps;
  }

  private @NotNull List<Team> createTeams(@NotNull GameConfig config, @NotNull Provider<Team> teamProvider) {
    List<Team> teams = new ArrayList<>();
    for (int i = 0; i < config.teams(); i++) {
      Team team = teamProvider.get();
      teams.add(team);
    }
    log.info("Created {} teams.", teams.size());
    return teams;
  }

  @Override
  public void launch() {
    log.info("Launching game, entering waiting phase");

    this.phases.enterPhase(WaitingPhase.class);
    MinecraftServer.getSchedulerManager().scheduleTask(this.tickTask, TaskSchedule.tick(1), TaskSchedule.tick(1));
  }

  @Override
  public void end(@Nullable Team winnerTeam, @NotNull EndReason reason) {
    phases().enterPhase("end");

    for (Map map : maps()) {
      Team owningTeam = map.owner();
      Audience teamAudience = owningTeam.audience();

      teamAudience.playSound(Sound.sound(Key.key("entity.firework_rocket.launch"), Source.AMBIENT, 1F, 1F), Emitter.self());
      teamAudience.showTitle(Title.title(
          Component.text("Winners:").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
          owningTeam.displayName(),
          Times.times(Duration.ofMillis(100), Duration.ofSeconds(5), Duration.ofMillis(250))
      ));

      Component message = Component.text("Game ended! Winner: ");
      if (winnerTeam == null) {
        message = message.append(Component.text("/").color(NamedTextColor.RED));
      } else {
        message = message.append(winnerTeam.displayName());
      }
      message = message.append(Component.text(" Reason: ").color(NamedTextColor.GRAY))
          .append(Component.text(reason.name()).color(NamedTextColor.YELLOW));

      Component t = message;
      map.executeForMembers(member -> member.player().sendMessage(t));
    }
  }

  @Inject
  public void registerCommands(@NotNull Set<Command> commands) {
    for (Command command : commands) {
      MinecraftServer.getCommandManager().register(command);
      log.info("Registered command: {}", command.getName());
    }
  }

  @Override
  public void tick(int currentTick) {
    try {
      this.phases.tick(currentTick);
      for (Team team : this.teams) {
        team.tick(currentTick);
      }
    } catch (Throwable throwable) {
      log.error("Error during game tick @ {}", currentTick, throwable);
    }
  }
}
