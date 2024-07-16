package space.chunks.gamecup.dgr.team.member.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.scoreboard.Sidebar.NumberFormat;
import net.minestom.server.scoreboard.Sidebar.ScoreboardLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntryDefault;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.member.Member;


/**
 * @author Nico_ND1
 */
public class MemberScoreboard extends AbstractMapObject<MapObjectConfigEntryDefault> implements MapObject {
  private final Member member;
  private Map parent;
  protected Sidebar sidebar;

  public MemberScoreboard(@NotNull Member member) {
    this.member = member;

    addListener(EventListener.of(ForceUpdateEvent.class, this::handleForceUpdate));
  }

  private void handleForceUpdate(ForceUpdateEvent event) {
    if (event.team == this.member.team()) {
      updateSidebar();
    }
  }

  @Override
  public void config(@NotNull MapObjectConfigEntry config) {
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    this.parent = parent;

    if (this.sidebar != null) {
      this.sidebar.removeViewer(this.member.player());
    }
    this.sidebar = new Sidebar(Component.text("Die Granaten \uD83D\uDE0E").color(NamedTextColor.LIGHT_PURPLE));
    this.sidebar.addViewer(this.member.player());
    initSidebar();
    updateSidebar();
  }

  protected @NotNull Sidebar.ScoreboardLine line(@NotNull String id, int score, @Nullable Sidebar.NumberFormat numberFormat) {
    return new ScoreboardLine(id, Component.text(" "), score, numberFormat);
  }

  protected void initSidebar() {
    this.sidebar.createLine(line("money_1", 10, NumberFormat.blank()));
    this.sidebar.createLine(line("money_2", 9, NumberFormat.blank()));
    this.sidebar.createLine(line("blank_1", 8, NumberFormat.blank()));
    this.sidebar.createLine(line("passengers_1", 7, NumberFormat.blank()));
    this.sidebar.createLine(line("passengers_2", 6, NumberFormat.blank()));
  }

  public void updateSidebar() {
    Team team = this.member.team();
    this.sidebar.updateLineContent("money_1", Component.text("Money:").color(NamedTextColor.GRAY));
    this.sidebar.updateLineContent("money_2", Component.text(team == null ? "/" : Integer.toString(team.money())).color(NamedTextColor.GOLD));

    this.sidebar.updateLineContent("passengers_1", Component.text("Passengers:").color(NamedTextColor.GRAY));
    this.sidebar.updateLineContent("passengers_2", Component.text(team == null ? "/" : Integer.toString(team.passengersMoved())).color(NamedTextColor.GREEN));
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.parent = null;
  }

  @Override
  public @NotNull String name() {
    return "scoreboard-"+member.player().getUsername();
  }

  @Override
  protected @NotNull Class<MapObjectConfigEntryDefault> configClass() {
    return MapObjectConfigEntryDefault.class;
  }

  public record ForceUpdateEvent(@NotNull Team team) implements Event {
  }
}
