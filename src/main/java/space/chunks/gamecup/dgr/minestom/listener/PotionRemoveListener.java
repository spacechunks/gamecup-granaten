package space.chunks.gamecup.dgr.minestom.listener;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.entity.EntityPotionRemoveEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.NamespaceID;

import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
public class PotionRemoveListener implements Consumer<EntityPotionRemoveEvent> {
  @Override
  public void accept(EntityPotionRemoveEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof LivingEntity livingEntity)) {
      return;
    }

    Potion potion = event.getPotion();
    if (potion.effect() == PotionEffect.SLOWNESS) {
      //livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(NamespaceID.from("potions", "slowness"));
      livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2);
    }
  }
}
