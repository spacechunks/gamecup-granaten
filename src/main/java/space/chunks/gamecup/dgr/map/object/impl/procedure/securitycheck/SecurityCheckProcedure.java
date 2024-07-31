package space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta.DisplayContext;
import net.minestom.server.entity.metadata.villager.VillagerMeta;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Level;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Profession;
import net.minestom.server.entity.metadata.villager.VillagerMeta.Type;
import net.minestom.server.entity.metadata.villager.VillagerMeta.VillagerData;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.impl.animation.Animation;
import space.chunks.gamecup.dgr.map.object.impl.procedure.AbstractProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.passenger.Passenger;


/**
 * @author Nico_ND1
 */
public class SecurityCheckProcedure extends AbstractProcedure<SecurityCheckConfig> implements Procedure {
  protected EntityCreature worker;
  protected Entity gate;

  public SecurityCheckProcedure() {
    this.worker = new EntityCreature(EntityType.VILLAGER);
    this.worker.editEntityMeta(VillagerMeta.class, meta -> {
      meta.setVillagerData(new VillagerData(Type.PLAINS, Profession.NITWIT, Level.MASTER));
    });

    this.gate = new Entity(EntityType.ITEM_DISPLAY);
    this.gate.setNoGravity(true);
    this.gate.editEntityMeta(ItemDisplayMeta.class, meta -> {
      meta.setDisplayContext(DisplayContext.NONE);
      meta.setItemStack(ItemStack.of(Material.PAPER).withCustomModelData(1));
      meta.setScale(new Vec(1.3));
    });
  }

  @Override
  @NotNull
  public Class<SecurityCheckConfig> configClass() {
    return SecurityCheckConfig.class;
  }

  @Override
  public @Nullable Animation createAnimation(@NotNull Passenger passenger) {
    SecurityCheckAnimation animation = new SecurityCheckAnimation(this, passenger);
    animation.config(this.config);
    bind(animation);

    this.parent.queueMapObjectRegister(animation);
    this.animation = animation;
    return this.animation;
  }

  @Override
  public void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    Instance instance = parent.instance();
    this.worker.setInstance(instance, this.config.workerPos());
    this.gate.setInstance(instance, this.config.gatePos());

    if (this.config.minLevel() != null) {
      ParticlePacket packet = new ParticlePacket(Particle.GLOW_SQUID_INK, false, this.config.gatePos().add(0, 1, 0), new Vec(0.5, 0.6, 0.5), 0.001F, 25);
      instance.sendGroupedPacket(packet);
      //instance.playSound(Sound.sound(Key.key("entity.villager.ambient"), Source.AMBIENT, 1F, 1F), this.config.workerPos());
    }
  }

  @Override
  public void handleUnregister(@NotNull Map parent, @NotNull UnregisterReason reason) {
    super.handleUnregister(parent, reason);
    this.worker.remove();
    this.gate.remove();
  }

  @Override
  public void handleTargetUnregister(@NotNull Map parent) {
    super.handleTargetUnregister(parent);
    this.animation = null;
  }

  @Override
  public @NotNull String group() {
    return Procedure.SECURITY_CHECK;
  }
}
