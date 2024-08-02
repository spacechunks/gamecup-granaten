package space.chunks.gamecup.dgr.team;

import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import space.chunks.gamecup.dgr.minestom.actionbar.ActionBarHelper;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.team.member.Member;
import space.chunks.gamecup.dgr.team.member.scoreboard.MemberScoreboard.ForceUpdateEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Nico_ND1
 */
public final class TeamImpl extends AbstractMapObject<MapObjectConfigEntry> implements Team {
  private static final NamedTextColor[] COLORS = new NamedTextColor[]{NamedTextColor.BLUE, NamedTextColor.RED, NamedTextColor.GREEN, NamedTextColor.YELLOW, NamedTextColor.AQUA,
      NamedTextColor.DARK_AQUA, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.LIGHT_PURPLE, NamedTextColor.GOLD,
      NamedTextColor.WHITE};
  private static final AtomicInteger ID_COUNT = new AtomicInteger();

  private final Game game;
  private final ActionBarHelper actionBarHelper;
  private final int id;
  private final String name;
  private final Set<Member> members;
  private final AtomicInteger money;
  private final AtomicInteger passengersMoved;
  private final TeamReputation reputation;
  private final net.minestom.server.scoreboard.Team scoreboardTeam;
  private Map map;

  private final BossBar goalBossBar;

  @Inject
  public TeamImpl(@NotNull Game game) {
    this.game = game;
    this.actionBarHelper = new ActionBarHelper();
    this.id = ID_COUNT.incrementAndGet();
    this.name = "Team#"+this.id;
    this.members = new HashSet<>();
    this.money = new AtomicInteger();
    this.passengersMoved = new AtomicInteger();
    this.reputation = new TeamReputation();
    this.goalBossBar = BossBar.bossBar(Component.text(" "), 0F, bossbarColor(), Overlay.PROGRESS);
    this.scoreboardTeam = MinecraftServer.getTeamManager().createTeam(name(), Component.empty(), color(), Component.empty());

    addListener(EventListener.of(ForceUpdateEvent.class, this::handleBossbarUpdate));
    addListener(EventListener.of(AddEntityToInstanceEvent.class, this::handleInstanceEntityAdd));
    addListener(EventListener.of(RemoveEntityFromInstanceEvent.class, this::handleInstanceEntityRemove));
    addListener(EventListener.of(MapObjectUnregisterEvent.class, this::handleMapObjectUnregister));
  }

  @Override
  public void tick(int currentTick) {
    this.reputation.tick(currentTick);
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

      int moneyReward = passenger.calculateMoneyReward();
      addMoney(moneyReward);
      updateActionbar(1, 0, 0);
      this.reputation.addEntry(passenger.config().happyPassengerReputationModifier(), passenger.config().happyPassengerReputationLifetime());

      MinecraftServer.getGlobalEventHandler().call(new ForceUpdateEvent(this));
    } else if (event.reason() == UnregisterReason.PASSENGER_LEFT_ANGRY) {
      this.reputation.addEntry(passenger.config().angryPassengerReputationModifier(), passenger.config().angryPassengerReputationLifetime());
    }
  }

  private void updateActionbar(int passengersAdded, int moneyAdded, int moneyLost) {
    this.actionBarHelper.sendActionBar(audience(), this, (@NotNull TeamImpl key, @Nullable MoneyAddActionBar previousValue) -> {
      if (previousValue == null) {
        return new MoneyAddActionBar(passengersAdded, moneyAdded, moneyLost);
      }
      return previousValue.add(moneyAdded, moneyLost);
    }, (key, value) -> {
      Component component = Component.text("+ "+value.passengers).color(NamedTextColor.GREEN)
          .append(Component.text(" | ").color(NamedTextColor.DARK_GRAY))
          .append(Component.text("+ "+value.money, NamedTextColor.GOLD));
      if (value.moneyLost > 0) {
        component = component
            .append(Component.text(" (").color(NamedTextColor.GRAY))
            .append(Component.text("-").color(NamedTextColor.RED))
            .append(Component.text(value.moneyLost).color(NamedTextColor.RED))
            .append(Component.text(")").color(NamedTextColor.GRAY));
      }
      return component;
    });
  }

  private void handleInstanceEntityRemove(RemoveEntityFromInstanceEvent event) {
    if (event.getEntity() instanceof Player player && event.getInstance() == this.map.instance()) {
      this.goalBossBar.removeViewer(player);
    }
  }

  private void handleInstanceEntityAdd(AddEntityToInstanceEvent event) {
    if (event.getEntity() instanceof Player player && event.getInstance() == this.map.instance()) {
      this.goalBossBar.addViewer(player);
    }
  }

  private @NotNull Color bossbarColor() {
    return switch (this.id) {
      case 0 -> Color.BLUE;
      case 1 -> Color.RED;
      case 2 -> Color.GREEN;
      case 3 -> Color.YELLOW;
      default -> Color.PURPLE;
    };
  }

  @Override
  public @NotNull NamedTextColor color() {
    return COLORS[this.id];
  }

  @NotNull
  @Override
  public net.minestom.server.scoreboard.Team scoreboardTeam() {
    return this.scoreboardTeam;
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
    this.scoreboardTeam.addMember(member.player().getUsername());
  }

  @Override
  public void removeMember(@NotNull UUID uuid) {
    this.members.removeIf(member -> {
      if (member.uuid().equals(uuid)) {
        this.scoreboardTeam.removeMember(member.player().getUsername());
        return true;
      }
      return false;
    });
  }

  @Override
  public @NotNull Audience audience() {
    return Audience.audience(this.members.stream().map(Member::player).toList());
  }

  @Override
  public int money() {
    return this.money.get();
  }

  @Override
  public void addMoney(int money) {
    int moneyOriginal = money;
    double moneyModifier = this.reputation.moneyModifier();
    money = Math.max(1, (int) (moneyModifier * money));

    this.money.addAndGet(money);
    updateActionbar(0, money, moneyOriginal-money);
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
  public @NotNull TeamReputation reputation() {
    return this.reputation;
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
  public @NotNull Component displayName() {
    Component result = Component.empty();
    int i = 0;
    for (Member member : this.members) {
      result = result.append(member.displayName().color(NamedTextColor.GOLD));

      if (i++ < this.members.size()-1) {
        result = result.append(Component.text(", ").color(NamedTextColor.DARK_GRAY));
      }
    }
    return result;
  }

  @Override
  @NotNull
  public Class<MapObjectConfigEntry> configClass() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamImpl team = (TeamImpl) o;
    return id == team.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @AllArgsConstructor
  private class MoneyAddActionBar {
    private int passengers;
    private int money;
    private int moneyLost;

    private @NotNull MoneyAddActionBar add(int add, int lost) {
      this.money += add;
      this.moneyLost += lost;
      this.passengers++;
      return this;
    }
  }
}
