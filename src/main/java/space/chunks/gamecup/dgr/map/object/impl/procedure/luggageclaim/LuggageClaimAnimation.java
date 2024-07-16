package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractBindableMapObject;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class LuggageClaimAnimation extends AbstractBindableMapObject<LuggageClaimConfig> implements Animation {
  private static final int TICKS_PER_STEP = 30;

  private final LuggageClaimProcedure luggageClaim;
  private final List<Luggage> luggage;

  public LuggageClaimAnimation(LuggageClaimProcedure luggageClaim) {
    this.luggageClaim = luggageClaim;
    this.luggage = new ArrayList<>();
  }

  @Override
  protected @NotNull Class<LuggageClaimConfig> configClass() {
    return LuggageClaimConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.luggageClaim.line() == null) {
      return TickResult.CONTINUE;
    }

    for (int i = 0; i < this.luggageClaim.line().size(); i++) {
      LuggageClaimLineEntry lineEntry = this.luggageClaim.line().get(i);
      WaitingSlot waitingSlot = lineEntry.waitingSlot();
      Passenger occupant = waitingSlot.occupant();
      if (occupant == null) {
        if (lineEntry.luggage() != null) {
          lineEntry.luggage().remove();
          lineEntry.luggage(null);
          this.luggage.remove(lineEntry.luggage());
        }
        continue;
      }

      Luggage luggage = lineEntry.luggage();
      if (luggage == null && occupant.task().state() == State.WAIT_IN_QUEUE && Math.random() > 0.25D) {
        luggage = spawnLuggage(map, i);
        lineEntry.luggage(luggage);

        occupant.entityUnsafe().lookAt(lineEntry.pos().withY(y -> y+1.5));
      }
    }

    this.luggage.removeIf(this::stepLuggage);
    return TickResult.CONTINUE;
  }

  private void collectLuggage(@NotNull Luggage luggage) {
    LuggageClaimLineEntry lineEntry = this.luggageClaim.line().get(luggage.targetLineEntryIndex);
    assert luggage == lineEntry.luggage();
    WaitingSlot waitingSlot = lineEntry.waitingSlot();
    Passenger occupant = waitingSlot.occupant();
    assert occupant != null;

    occupant.entityUnsafe().setItemInMainHand(luggage.entity.getItemStack());
    occupant.entityUnsafe().swingMainHand();
    luggage.remove();

    occupant.task().state(State.PROCEED);
    waitingSlot.free();
  }

  private boolean stepLuggage(@NotNull Luggage luggage) {
    luggage.step++;

    if (luggage.step == TICKS_PER_STEP) {
      luggage.step = 0;
      luggage.currentLineEntryIndex = (luggage.currentLineEntryIndex+1) % this.luggageClaim.line().size();

      if (luggage.currentLineEntryIndex == luggage.targetLineEntryIndex) {
        collectLuggage(luggage);
        return true;
      }
    }

    LuggageClaimLineEntry fromEntry = this.luggageClaim.line().get(luggage.currentLineEntryIndex);
    int nextLineIndex = (luggage.currentLineEntryIndex+1) % this.luggageClaim.line().size();
    LuggageClaimLineEntry targetEntry = this.luggageClaim.line().get(nextLineIndex);

    Vec step = new Vec(targetEntry.direction().normalX(), 0, targetEntry.direction().normalZ()).div(TICKS_PER_STEP).mul(luggage.step);
    Pos newPos = fromEntry.pos().add(step).add(0.5, 1, 0.5);
    luggage.entity.teleport(newPos);
    return false;
  }

  private @Nullable Luggage spawnLuggage(@NotNull Map map, int owningEntryIndex) {
    Integer lineEntryIndex = tryFindSpawnLineEntryIndex();
    if (lineEntryIndex == null) {
      return null;
    }

    LuggageClaimLineEntry lineEntry = this.luggageClaim.line().get(lineEntryIndex);
    ItemEntity itemEntity = new ItemEntity(ItemStack.of(Material.ACACIA_BOAT));
    itemEntity.setInstance(map.instance(), lineEntry.pos().add(0.5, 1, 0.5));
    itemEntity.setPickable(false);

    Luggage luggage = new Luggage(itemEntity, lineEntryIndex, owningEntryIndex);
    this.luggage.add(luggage);
    return luggage;
  }

  // We lazy try only once per tick and are ok if it fails, because it will retry next tick anyway
  private @Nullable Integer tryFindSpawnLineEntryIndex() {
    List<LuggageClaimLineEntry> line = this.luggageClaim.line();
    int entryIndex = (int) (Math.random() * line.size());
    LuggageClaimLineEntry lineEntry = line.get(entryIndex);
    Pos pos = lineEntry.pos();

    for (Luggage luggage : this.luggage) {
      Pos luggagePos = luggage.entity.getPosition();

      if (luggagePos.add(0, -1, 0).sameBlock(pos)) {
        return null;
      }
    }
    return entryIndex;
  }

  public static class Luggage {
    private final ItemEntity entity;
    private final int targetLineEntryIndex;
    private int currentLineEntryIndex;

    private int step;

    public Luggage(ItemEntity entity, int currentLineEntryIndex, int targetLineEntryIndex) {
      this.entity = entity;
      this.currentLineEntryIndex = currentLineEntryIndex;
      this.targetLineEntryIndex = targetLineEntryIndex;
    }

    private void remove() {
      this.entity.remove();
    }
  }
}
