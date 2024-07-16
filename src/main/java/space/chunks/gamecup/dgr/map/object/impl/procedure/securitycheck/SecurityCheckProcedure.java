package space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck;

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
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
      meta.setItemStack(ItemStack.of(Material.WHITE_CARPET).withCustomModelData(1));
      meta.setScale(new Vec(1.3));
    });
  }

  @Override
  protected @NotNull Class<SecurityCheckConfig> configClass() {
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
    this.worker.setInstance(parent.instance(), this.config.workerPos());
    this.gate.setInstance(parent.instance(), this.config.gatePos());
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
