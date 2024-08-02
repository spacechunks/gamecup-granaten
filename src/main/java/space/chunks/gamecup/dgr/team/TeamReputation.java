package space.chunks.gamecup.dgr.team;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Ticking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Nico_ND1
 */
public final class TeamReputation implements Ticking {
  private static final int HORRIBLE_THRESHOLD = -20;
  private static final int BAD_THRESHOLD = 0;
  private static final int NEUTRAL_THRESHOLD = 14;
  private static final int GOOD_THRESHOLD = 22;

  private final List<Entry> entries;

  public TeamReputation() {
    this.entries = Collections.synchronizedList(new ArrayList<>());
  }

  public void addEntry(int modifier, int lifetime) {
    this.entries.add(new Entry(modifier, lifetime));
  }

  public int get() {
    return this.entries.stream().mapToInt(entry -> entry.modifier).sum();
  }

  public double moneyModifier() {
    int reputation = get();
    if (reputation < HORRIBLE_THRESHOLD) {
      return 0.5;
    } else if (reputation < BAD_THRESHOLD) {
      return 0.75;
    } else if (reputation < NEUTRAL_THRESHOLD) {
      return 1;
    } else if (reputation < GOOD_THRESHOLD) {
      return 1.25;
    } else {
      return 1.5;
    }
  }

  public @NotNull Component getComponent(boolean appendMoneyModifier) {
    int reputation = get();
    Component component;
    if (reputation < HORRIBLE_THRESHOLD) {
      component = Component.text("Horrible").color(NamedTextColor.DARK_RED);
    } else if (reputation < BAD_THRESHOLD) {
      component = Component.text("Bad").color(NamedTextColor.RED);
    } else if (reputation < NEUTRAL_THRESHOLD) {
      component = Component.text("Neutral").color(NamedTextColor.YELLOW);
    } else if (reputation < GOOD_THRESHOLD) {
      component = Component.text("Good").color(NamedTextColor.GREEN);
    } else {
      component = Component.text("Excellent").color(NamedTextColor.DARK_GREEN);
    }

    if (appendMoneyModifier) {
      double moneyModifier = moneyModifier();
      int moneyModifierPercentage = (int) (moneyModifier * 100D);
      component = component.append(Component.text(" (").color(NamedTextColor.DARK_GRAY))
          .append(Component.text(moneyModifierPercentage).color(NamedTextColor.GOLD).decorate(TextDecoration.ITALIC))
          .append(Component.text("%").color(NamedTextColor.GRAY))
          .append(Component.text(")").color(NamedTextColor.DARK_GRAY));
    }
    return component;
  }

  @Override
  public void tick(int currentTick) {
    this.entries.removeIf(entry -> {
      entry.ticksLeft--;
      return entry.ticksLeft <= 0;
    });
  }

  @AllArgsConstructor
  private class Entry {
    private final int modifier;
    private int ticksLeft;
  }
}
