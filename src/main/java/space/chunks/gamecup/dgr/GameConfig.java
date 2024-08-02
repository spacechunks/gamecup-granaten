package space.chunks.gamecup.dgr;

import org.jetbrains.annotations.NotNull;


/**
 * @author Nico_ND1
 */
public record GameConfig(
    int teams,
    int playersPerTeam
) {
  public static @NotNull GameConfig defaultConfig() {
    return new GameConfig(10, 1);
  }
}
