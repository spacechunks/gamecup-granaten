package space.chunks.gamecup.dgr.team;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.team.member.Member;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Nico_ND1
 */
public final class TeamImpl implements Team {
  private static final AtomicInteger ID_COUNT = new AtomicInteger();

  private final String name;
  private final Set<Member> members;
  private Map map;

  @Inject
  private Game game;

  public TeamImpl() {
    this.name = "Team#"+ID_COUNT.incrementAndGet();
    this.members = new HashSet<>();
  }

  @Override
  public @NotNull Map map() {
    if (this.map == null) {
      throw new IllegalStateException("Map not been loaded yet.");
    }
    return this.map;
  }

  @Override
  public @NotNull Team map(@NotNull Map map) {
    this.map = map;
    return this;
  }

  @Override
  public @NotNull Collection<Member> members() {
    return this.members;
  }

  @Override
  public void addMember(@NotNull Member member) {
    this.members.add(member);
  }

  @Override
  public void removeMember(@NotNull UUID uuid) {
    this.members.removeIf(member -> member.uuid().equals(uuid));
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }
}
