package space.chunks.gamecup.dgr.team;

import com.google.inject.Inject;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.goal.GameGoal;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.event.MapObjectUnregisterEvent;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.team.member.Member;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard.ForceUpdateEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Nico_ND1
 */
public final class TeamImpl extends AbstractMapObject<MapObjectConfigEntry> implements Team {
  private static final RGBLike[] COLORS = new RGBLike[]{NamedTextColor.BLUE, NamedTextColor.RED, NamedTextColor.GREEN, NamedTextColor.YELLOW};
  private static final AtomicInteger ID_COUNT = new AtomicInteger();

  private final Game game;
  private final int id;
  private final String name;
  private final Set<Member> members;
  private final AtomicInteger money;
  private final AtomicInteger passengersMoved;
  private Map map;

  private final BossBar goalBossBar;

  @Inject
  public TeamImpl(@NotNull Game game) {
    this.game = game;
    this.id = ID_COUNT.get();
    this.name = "Team#"+this.id;
    this.members = new HashSet<>();
    this.money = new AtomicInteger();
    this.passengersMoved = new AtomicInteger();
    this.goalBossBar = BossBar.bossBar(Component.text(" "), 0F, bossbarColor(), Overlay.PROGRESS);

    addListener(EventListener.of(ForceUpdateEvent.class, this::handleBossbarUpdate));
    addListener(EventListener.of(AddEntityToInstanceEvent.class, this::handleInstanceEntityAdd));
    addListener(EventListener.of(RemoveEntityFromInstanceEvent.class, this::handleInstanceEntityRemove));
    addListener(EventListener.of(MapObjectUnregisterEvent.class, this::handleMapObjectUnregister));
  }

  private void handleMapObjectUnregister(MapObjectUnregisterEvent event) {
    Map map = event.map();
    if (map != map()) {
      return;
    }

    MapObject mapObject = event.mapObject();
    if (!(mapObject instanceof Passenger passenger)) {
      return;
    }

    if (event.reason() == UnregisterReason.PASSENGER_LEFT_HAPPY) {
      addPassengerMoved();
      addMoney(10); // TODO: maybe give money depending on patience. Base value could be 10 and depending on how much patience they lost it will go up until it's doubled

      MinecraftServer.getGlobalEventHandler().call(new ForceUpdateEvent(this));
    }
  }

  private void handleInstanceEntityRemove(RemoveEntityFromInstanceEvent event) {
    if (event.getEntity() instanceof Player player) {
      this.goalBossBar.removeViewer(player);
    }
  }

  private void handleInstanceEntityAdd(AddEntityToInstanceEvent event) {
    if (event.getEntity() instanceof Player player) {
      this.goalBossBar.addViewer(player);
    }
  }

  private @NotNull Color bossbarColor() {
    return switch (this.id) {
      case 0 -> Color.BLUE;
      case 1 -> Color.RED;
      case 2 -> Color.GREEN;
      case 3 -> Color.YELLOW;
      default -> throw new IllegalStateException("Unexpected value: "+this.id);
    };
  }

  @Override
  public @NotNull RGBLike color() {
    return COLORS[this.id];
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
  public void forceRemoveMoney(int money) {
    this.money.addAndGet(-money);
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
  public @NotNull BossBar goalBossBar() {
    return this.goalBossBar;
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }

  @Override
  protected @NotNull Class<MapObjectConfigEntry> configClass() {
    return MapObjectConfigEntry.class;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    handleBossbarUpdate(null);
    parent.executeForMembers(member -> this.goalBossBar.addViewer(member.player()));
  }

  private void handleBossbarUpdate(@Nullable ForceUpdateEvent event) {
    if (event == null || event.team() == this) {
      GameGoal goal = this.game.goal();
      this.goalBossBar.name(goal.bossBar(this));
      this.goalBossBar.progress((float) goal.progress(this));
    }
  }
}
