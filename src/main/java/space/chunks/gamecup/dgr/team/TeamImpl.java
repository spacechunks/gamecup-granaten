package space.chunks.gamecup.dgr.team;

import org.jetbrains.annotations.NotNull;
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
  private final AtomicInteger money;
  private final AtomicInteger passengersMoved;
  private Map map;

  public TeamImpl() {
    this.name = "Team#"+ID_COUNT.incrementAndGet();
    this.members = new HashSet<>();
    this.money = new AtomicInteger();
    this.passengersMoved = new AtomicInteger();
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
  public int money() {
    return this.money.get();
  }

  @Override
  public void addMoney(int money) {
    this.money.addAndGet(money);
  }

  @Override
  public boolean removeMoney(int money) {
    return this.money.updateAndGet(value -> value-money >= 0 ? value-money : value) != money;
  }

  @Override
  public int passengersMoved() {
    return this.passengersMoved.get();
  }

  @Override
  public void addPassengerMoved() {
    this.passengersMoved.addAndGet(1);
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }
}
