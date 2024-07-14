package space.chunks.gamecup.dgr.map.procedure.securitycheck;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntryDefault;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.procedure.incident.AbstractIncident;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheckFailedIncident extends AbstractIncident<MapObjectConfigEntryDefault> {
  public SecurityCheckFailedIncident() {
  }

  private void handleEntityInteract(@NotNull Map parent, @NotNull PlayerEntityInteractEvent event) {
    if (event.getHand() != Hand.MAIN) {
      return;
    }

    SecurityCheck securityCheck = (SecurityCheck) boundTarget();
    assert securityCheck != null;
    SecurityCheckFailedAnimation animation = (SecurityCheckFailedAnimation) securityCheck.animation();
    assert animation != null;
    Passenger passenger = animation.passenger;
    // TODO: better (and null-safe) approach to getting affected passenger(s)

    if (event.getTarget().equals(passenger.entityUnsafe())) {
      passenger.entity().async(entity -> {
        event.getPlayer().addPassenger(entity);
        entity.setGlowing(false);
      });
    }
  }

  private void handleEntityAttack(Map parent, EntityAttackEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    SecurityCheck securityCheck = (SecurityCheck) boundTarget();
    assert securityCheck != null;
    SecurityCheckFailedAnimation animation = (SecurityCheckFailedAnimation) securityCheck.animation();
    assert animation != null;
    Passenger passenger = animation.passenger;

    if (event.getTarget().equals(passenger.entityUnsafe())) {
      player.removePassenger(passenger.entityUnsafe());
      passenger.entity().async(entity -> {
        System.out.println(entity.getVelocity());
        entity.setVelocity(entity.getVelocity().mul(1, 0.5, 1));
      });

      MinecraftServer.getSchedulerManager().scheduleTask(() -> {
        resolve(SolutionType.RESOLVED);
        parent.queueMapObjectUnregister(passenger, UnregisterReason.INCIDENT_RESOLVED);
        parent.queueMapObjectUnregister(this, UnregisterReason.INCIDENT_RESOLVED);
      }, TaskSchedule.tick(10), TaskSchedule.stop());
    }
  }

  @Override
  protected @NotNull Class<MapObjectConfigEntryDefault> configClass() {
    return MapObjectConfigEntryDefault.class;
  }

  @Override
  public @Nullable Animation replaceProcedureAnimation(@Nullable Animation currentAnimation) {
    if (currentAnimation instanceof SecurityCheckAnimation scAnimation) {
      return new SecurityCheckFailedAnimation(scAnimation.securityCheck, scAnimation.passenger);
    } else if (currentAnimation != null) {
      throw new IllegalStateException("The current animation is not a SecurityCheckAnimation");
    }
    return null;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    addListener(EventListener.of(PlayerEntityInteractEvent.class, event -> handleEntityInteract(parent, event)));
    addListener(EventListener.of(EntityAttackEvent.class, event -> handleEntityAttack(parent, event)));

    SecurityCheck securityCheck = (SecurityCheck) boundTarget();
    assert securityCheck != null;
    SecurityCheckFailedAnimation animation = (SecurityCheckFailedAnimation) securityCheck.animation();
    assert animation != null;
    Passenger passenger = animation.passenger;

    passenger.entity().async(entity -> {
      entity.setGlowing(true);
    });

    parent.executeForMembers(member -> {
      member.player().sendMessage("Security check failed! Pick them up and throw them out!");
    });
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
  }
}
