package space.chunks.gamecup.dgr.team.member;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.team.Team;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class MemberImpl implements Member {
  private final Player player;
  private Team team;

  @AssistedInject
  public MemberImpl(@Assisted Player player) {
    this.player = player;
  }

  @Override
  public @NotNull UUID uuid() {
    return this.player.getUuid();
  }

  @Override
  public @Nullable Player player() {
    return this.player;
  }

  @Override
  public @Nullable Team team() {
    return this.team;
  }

  @Override
  public void assignTeam(@Nullable Team team) {
    if (this.team != null) {
      this.team.removeMember(uuid());
    }

    this.team = team;
    if (team != null) {
      team.addMember(this);
    }
  }

  @Override
  public @NotNull String name() {
    return this.player.getUsername();
  }
}
