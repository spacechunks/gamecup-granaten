package space.chunks.gamecup.dgr.minestom.inventory;

import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
public sealed interface Item permits ItemImpl {
  @NotNull
  ItemStack itemStack();

  @NotNull
  Item addClickListener(@NotNull Consumer<InventoryPreClickEvent> listener, ClickType... clickTypes);

  default Item addAllClickListener(@NotNull Consumer<InventoryPreClickEvent> listener) {
    return addClickListener(listener, ClickType.LEFT_CLICK, ClickType.RIGHT_CLICK);
  }

  void trigger(@NotNull InventoryPreClickEvent event);

  static @NotNull Item of(@NotNull ItemStack itemStack) {
    return new ItemImpl(itemStack, new ArrayList<>());
  }
}
