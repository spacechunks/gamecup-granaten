package space.chunks.gamecup.dgr.minestom.listener;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.event.entity.EntityPotionAddEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.NamespaceID;

import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
public class PotionAddListener implements Consumer<EntityPotionAddEvent> {
  @Override
  public void accept(EntityPotionAddEvent event) {
    Entity entity = event.getEntity();
    if (!(entity instanceof LivingEntity livingEntity)) {
      return;
    }

    Potion potion = event.getPotion();
    if (potion.effect().namespace().equals(PotionEffect.SLOWNESS.namespace()) && livingEntity instanceof Player player) {
      double modifier = -0.15 * (potion.amplifier()+1);
      livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier(NamespaceID.from("potions", "slowness"), modifier, AttributeOperation.MULTIPLY_TOTAL));
    }
  }
}
