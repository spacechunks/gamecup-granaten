package space.chunks.gamecup.dgr.team.member;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.team.Team;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public interface Member extends Named {
  @NotNull
  UUID uuid();

  @NotNull
  Player player();

  @Nullable
  Team team();

  void assignTeam(@Nullable Team team);
}
