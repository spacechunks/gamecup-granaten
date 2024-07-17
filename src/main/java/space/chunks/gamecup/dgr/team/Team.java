package space.chunks.gamecup.dgr.team;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.team.member.Member;

import java.util.Collection;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
public interface Team extends MapObject, Named {
  @NotNull
  RGBLike color();

  /**
   * Returns the map for this team.
   *
   * @throws IllegalStateException if the map is not loaded yet.
   */
  @NotNull
  Map map();

  @NotNull
  Team map(@NotNull Map map);

  @NotNull
  Collection<Member> members();

  void addMember(@NotNull Member member);

  void removeMember(@NotNull UUID uuid);

  int money();

  void addMoney(int money);

  boolean removeMoney(int money);

  void forceRemoveMoney(int money);

  int passengersMoved();

  void addPassengerMoved();

  @NotNull
  BossBar goalBossBar();
}
