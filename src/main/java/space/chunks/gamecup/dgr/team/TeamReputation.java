package space.chunks.gamecup.dgr.team;

import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Ticking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Nico_ND1
 */
public final class TeamReputation implements Ticking {
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

  public @NotNull Component getComponent() {
    int reputation = get();
    return Component.text(reputation).color(getColor(reputation));
  }

  private @NotNull NamedTextColor getColor(int reputation) {
    if (reputation < 0) {
      return NamedTextColor.RED;
    } else if (reputation < 50) {
      return NamedTextColor.YELLOW;
    } else {
      return NamedTextColor.GREEN;
    }
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
