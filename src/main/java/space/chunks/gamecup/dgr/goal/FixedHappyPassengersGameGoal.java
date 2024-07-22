package space.chunks.gamecup.dgr.goal;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.Game.EndReason;
import space.chunks.gamecup.dgr.Ticking;
import space.chunks.gamecup.dgr.team.Team;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Nico_ND1
 */
public final class FixedHappyPassengersGameGoal implements GameGoal, Ticking {
  private final Game game;
  private final int happyPassengers;
  private final Map<Team, Boolean[]> alertTicksSentPerMap;
  private final int[] alertTicks;

  public FixedHappyPassengersGameGoal(@NotNull Game game, int happyPassengers, int[] alertTicks) {
    this.game = game;
    this.happyPassengers = happyPassengers;
    this.alertTicksSentPerMap = new HashMap<>();
    this.alertTicks = alertTicks;
  }

  @Override
  public @NotNull Component name() {
    return Component.text("Reach "+this.happyPassengers+" happy passengers");
  }

  @Override
  public @NotNull Component bossBar(@NotNull Team team) {
    return Component.text(team.passengersMoved()).color(NamedTextColor.GREEN)
        .append(Component.text("/").color(NamedTextColor.GRAY))
        .append(Component.text(this.happyPassengers).color(NamedTextColor.DARK_GREEN))
        .append(Component.text(" Passengers").color(NamedTextColor.GRAY));
  }

  @Override
  public void showTitle(@NotNull Audience audience) {
    audience.showTitle(Title.title(
        Component.text("Game goal:").color(NamedTextColor.GOLD),
        Component.text("Reach ").color(NamedTextColor.GREEN)
            .append(Component.text(this.happyPassengers).color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD))
            .append(Component.text(" happy passengers").color(NamedTextColor.GREEN)),
        Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
    ));
  }

  @Override
  public boolean testReached(@NotNull Team team) {
    return team.passengersMoved() >= this.happyPassengers;
  }

  @Override
  public double progress(@NotNull Team team) {
    return Math.min((double) team.passengersMoved() / this.happyPassengers, 1.0D);
  }

  @Override
  public void tick(int currentTick) {
    for (Team team : this.game.teams()) {
      if (testReached(team)) {
        this.game.end(team, EndReason.GOAL_REACHED);
        return;
      }

      Boolean[] goalSentStates = this.alertTicksSentPerMap.computeIfAbsent(team, t -> new Boolean[this.alertTicks.length]);
      for (int i = 0; i < this.alertTicks.length; i++) {
        int alertTick = this.alertTicks[i];
        if (alertTick == team.passengersMoved()) {
          Boolean goalSentState = goalSentStates[i];

          if (goalSentState == null || !goalSentState) {
            System.out.println("A Team has reached "+team.passengersMoved()+"/"+this.happyPassengers+" happy passengers!\nStates: " +Arrays.toString(goalSentStates) + " with " + i + "=" + alertTick);
            for (Team t : this.game.teams()) {
              t.audience().sendMessage(Component.text("A Team").color(TextColor.color(team.color()))
                  .append(Component.text(" has reached ").color(NamedTextColor.GRAY)
                      .append(Component.text(team.passengersMoved()).color(NamedTextColor.YELLOW))
                      .append(Component.text("/").color(NamedTextColor.GRAY))
                      .append(Component.text(this.happyPassengers).color(NamedTextColor.GOLD))
                      .append(Component.text(" happy passengers!").color(NamedTextColor.GRAY))
                  ));
            }
            goalSentStates[i] = true;
          }
        }
      }
    }
  }
}
