package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.upgradable.Upgradable;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradeHolder;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.PassengerImpl;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue.WaitingSlot;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask;
import space.chunks.gamecup.dgr.passenger.task.PassengerTask.State;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Nico_ND1
 */
@Accessors(fluent=true)
public class LuggageClaimAnimation extends AbstractAnimation<LuggageClaimConfig> implements Animation {
  private static final int TICKS_PER_STEP = 30;

  private final LuggageClaimProcedure luggageClaim;
  private final List<Luggage> luggage;

  @Getter
  @Setter
  private boolean halt;

  public LuggageClaimAnimation(@NotNull LuggageClaimProcedure luggageClaim) {
    this.luggageClaim = luggageClaim;
    this.luggage = new ArrayList<>();
  }

  @Override
  @NotNull
  public Class<LuggageClaimConfig> configClass() {
    return LuggageClaimConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (this.luggageClaim.line() == null) {
      return TickResult.CONTINUE;
    }

    if (this.halt) {
      return TickResult.CONTINUE;
    } else if (tickFail(map, currentTick)) {
      Animation failAnimation = new LuggageClaimStuckAnimation(this.luggageClaim, this);
      failAnimation.config(this.config);
      map.queueMapObjectRegister(failAnimation);

      map.owner().audience().sendMessage(Component.text("Luggage claim is stuck!").color(NamedTextColor.RED));
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
      PassengerTask task = occupant.task();
      if (luggage == null && task != null && task.state() == State.WAIT_IN_QUEUE && Math.random() > 0.25D) {
        ItemStack item = occupant.baggage();
        if (item == null) {
          item = PassengerImpl.BAGGAGE_ITEMS[(int) (Math.random() * PassengerImpl.BAGGAGE_ITEMS.length)];
        }

        luggage = spawnLuggage(item, map, i);
        lineEntry.luggage(luggage);

        occupant.entityUnsafe().lookAt(lineEntry.pos().withY(y -> y+1.5));
      }
    }

    this.luggage.removeIf(this::stepLuggage);
    return TickResult.CONTINUE;
  }

  private boolean tickFail(Map map, int currentTick) {
    if (currentTick % 20 != 0) {
      return false;
    }

    UpgradeHolder marketingUpgradeHolder = map.upgradeRegistry().holder(Procedure.MARKETING);
    if (marketingUpgradeHolder != null) {
      double baseFailRate = this.config.baseFailRate();
      double failRateModifier = marketingUpgradeHolder.getCurrentPerkValue(Upgradable.LUGGAGE_CLAIM_FAIL_RATE, 1.0D);
      double random = Math.random();
      System.out.println("Fail rate: " + (baseFailRate * failRateModifier) + " Random: " + random + " baseFailRate: " + baseFailRate + " failRateModifier: " + failRateModifier);
      return random < baseFailRate * failRateModifier;
    }
    return false;
  }

  private void collectLuggage(@NotNull Luggage luggage) {
    LuggageClaimLineEntry lineEntry = this.luggageClaim.line().get(luggage.targetLineEntryIndex);
    assert luggage == lineEntry.luggage();
    WaitingSlot waitingSlot = lineEntry.waitingSlot();
    Passenger occupant = waitingSlot.occupant();

    if (occupant != null) {
      occupant.entityUnsafe().setItemInMainHand(luggage.entity.getItemStack());
      occupant.entityUnsafe().swingMainHand();

      PassengerTask task = occupant.task();
      if (task != null) {
        task.state(State.PROCEED);
      }
      waitingSlot.free();
    }

    luggage.remove();
  }

  private boolean stepLuggage(@NotNull Luggage luggage) {
    luggage.step++;

    int neededSteps = (int) (TICKS_PER_STEP * this.luggageClaim.getCurrentPerkValue(Upgradable.LUGGAGE_CLAIM_SPEED));
    if (luggage.step >= neededSteps) {
      luggage.step = 0;
      luggage.currentLineEntryIndex = (luggage.currentLineEntryIndex+1) % this.luggageClaim.line().size();

      if (luggage.currentLineEntryIndex == luggage.targetLineEntryIndex) {
        collectLuggage(luggage);
        return true;
      }
    }

    LuggageClaimLineEntry fromEntry = this.luggageClaim.line().get(luggage.currentLineEntryIndex);
    int nextLineIndex = (luggage.currentLineEntryIndex+1) % this.luggageClaim.line().size();
    LuggageClaimLineEntry nextEntry = this.luggageClaim.line().get(nextLineIndex);

    Direction direction = nextEntry.direction();
    if (nextLineIndex == 0) {
      direction = fromEntry.direction();
    }
    Vec step = new Vec(direction.normalX(), 0, direction.normalZ()).div(neededSteps).mul(luggage.step);
    Pos newPos = fromEntry.pos().add(step).add(0.5, 1, 0.5);
    if (luggage.entity.getInstance() != null) {
      luggage.entity.teleport(newPos);
    }

    if (luggage.currentToTargetLineDistance() <= 3) {
      LuggageClaimLineEntry targetEntry = this.luggageClaim.line().get(luggage.targetLineEntryIndex);
      WaitingSlot waitingSlot = targetEntry.waitingSlot();
      Passenger occupant = waitingSlot.occupant();
      if (occupant != null && occupant.isValid()) {
        if (luggage.entity.getInstance() != null) {
          occupant.entityUnsafe().lookAt(luggage.entity);
        }
      }
    }
    return false;
  }

  private @Nullable Luggage spawnLuggage(@NotNull ItemStack item, @NotNull Map map, int owningEntryIndex) {
    Integer lineEntryIndex = tryFindSpawnLineEntryIndex();
    if (lineEntryIndex == null) {
      return null;
    }

    LuggageClaimLineEntry lineEntry = this.luggageClaim.line().get(lineEntryIndex);
    ItemEntity itemEntity = new ItemEntity(item);
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

  public class Luggage {
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

    private int currentToTargetLineDistance() {
      // find the distance between the indexes targetLineEntryIndex and currentLineEntryIndex in the list luggageClaim.line
      // the distance is the number of entries that need to be passed to reach the targetLineEntryIndex from the currentLineEntryIndex
      // the distance is always positive and can be calculated by the following formula:
      // (targetLineEntryIndex - currentLineEntryIndex + line.size()) % line.size()
      // the + line.size() is to ensure that the result is positive
      return Math.abs((this.targetLineEntryIndex-this.currentLineEntryIndex)) % LuggageClaimAnimation.this.luggageClaim.line().size();
    }
  }
}
