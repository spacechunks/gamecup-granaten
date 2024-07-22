package space.chunks.gamecup.dgr.goal;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Ticking;
import space.chunks.gamecup.dgr.team.Team;


/**
 * @author Nico_ND1
 */
public interface GameGoal extends Ticking {
  @NotNull
  Component name();

  @NotNull
  Component bossBar(@NotNull Team team);

  void showTitle(@NotNull Audience audience);

  boolean testReached(@NotNull Team team);

  double progress(@NotNull Team team);
}
