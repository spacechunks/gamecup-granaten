package space.chunks.gamecup.dgr.minestom.inventory;

import lombok.experimental.Accessors;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;


/**
 * @author Nico_ND1
 */
@Accessors(fluent=true)
public record ItemImpl(
    @NotNull ItemStack itemStack,
    @NotNull List<Listener> listeners
) implements Item {
  @Override
  public @NotNull Item addClickListener(@NotNull Consumer<InventoryPreClickEvent> listener, ClickType... clickTypes) {
    this.listeners.add(new Listener(listener, clickTypes));
    return this;
  }

  @Override
  public void trigger(@NotNull InventoryPreClickEvent event) {
    listenerLoop:
    for (Listener listener : this.listeners) {
      for (ClickType clickType : listener.clickTypes) {
        if (event.getClickType() == clickType) {
          listener.listener().accept(event);
          continue listenerLoop;
        }
      }
    }
  }

  private record Listener(
      @NotNull Consumer<InventoryPreClickEvent> listener,
      @NotNull ClickType[] clickTypes
  ) {
  }
}
