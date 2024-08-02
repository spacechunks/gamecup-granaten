package space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.AbstractAnimation;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;


/**
 * @author Nico_ND1
 */
public class LuggageClaimStuckAnimation extends AbstractAnimation<LuggageClaimConfig> implements Animation {
  private final LuggageClaimProcedure procedure;
  private final LuggageClaimAnimation mainAnimation;
  private Map parent;

  LuggageClaimStuckAnimation(@NotNull LuggageClaimProcedure procedure, @NotNull LuggageClaimAnimation mainAnimation) {
    this.procedure = procedure;
    this.mainAnimation = mainAnimation;

    addListener(EventListener.of(PlayerBlockInteractEvent.class, this::handleBlockInteract));
  }

  private void handleBlockInteract(@NotNull PlayerBlockInteractEvent event) {
    BlockVec blockPosition = event.getBlockPosition();
    for (LuggageClaimLineEntry lineEntry : this.procedure.line()) {
      if (blockPosition.sameBlock(lineEntry.pos())) {
        event.setCancelled(true);
        this.parent.queueMapObjectUnregister(this, UnregisterReason.INCIDENT_RESOLVED);
        return;
      }
    }
  }

  @Override
  public @NotNull Class<LuggageClaimConfig> configClass() {
    return LuggageClaimConfig.class;
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    return TickResult.CONTINUE;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);
    this.mainAnimation.halt(true);
    this.parent = parent;

    for (LuggageClaimLineEntry lineEntry : this.procedure.line()) {
      Entity model = lineEntry.model();
      if (model != null && model.isActive()) {
        model.setGlowing(true);
      }
    }
  }

  @Override
  public synchronized void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.mainAnimation.halt(false);

    for (LuggageClaimLineEntry lineEntry : this.procedure.line()) {
      Entity model = lineEntry.model();
      if (model != null && model.isActive()) {
        model.setGlowing(false);
      }
    }
  }
}
