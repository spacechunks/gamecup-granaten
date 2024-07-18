package space.chunks.gamecup.dgr.minestom.inventory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Nico_ND1
 */
@Accessors(fluent=true)
public class SinglePageGui implements Gui {
  @Getter(AccessLevel.PROTECTED)
  private final Player viewer;
  private final Inventory inventory;

  private final Map<Integer, Item> itemMap;
  @Getter(AccessLevel.PROTECTED)
  private boolean valid;
  @Getter(AccessLevel.PROTECTED)
  private boolean firstDraw;

  public SinglePageGui(@NotNull Player viewer, @NotNull Inventory inventory) {
    this.viewer = viewer;
    this.inventory = inventory;
    this.itemMap = new HashMap<>();
  }

  @Override
  public void open() {
    if (this.inventory.isViewer(this.viewer)) {
      throw new IllegalStateException("The inventory is already open");
    }

    this.firstDraw = true;
    redraw();
    this.firstDraw = false;

    this.registerListeners();
    this.viewer.openInventory(this.inventory);
  }

  @Override
  public void close() {
    this.viewer.closeInventory();
  }

  @Override
  public void redraw() {
  }

  protected boolean cancelAllClicks() {
    return true;
  }

  protected void registerListeners() {
    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
    eventHandler.addListener(EventListener.builder(InventoryOpenEvent.class)
        .filter(event -> event.getInventory() == this.inventory)
        .expireCount(1)
        .ignoreCancelled(true)
        .handler(event -> this.valid = true)
        .build());

    eventHandler.addListener(EventListener.builder(InventoryOpenEvent.class)
        .filter(event -> event.getInventory() != this.inventory && event.getPlayer().getUuid().equals(this.viewer.getUuid()))
        .expireWhen(this::testInvalid)
        .handler(event -> this.valid = false)
        .build());

    eventHandler.addListener(EventListener.builder(InventoryCloseEvent.class)
        .filter(event -> event.getInventory() == this.inventory)
        .expireWhen(this::testInvalid)
        .handler(this::handleClose)
        .build());

    eventHandler.addListener(EventListener.builder(InventoryPreClickEvent.class)
        .filter(event -> event.getInventory() == this.inventory)
        .expireWhen(this::testInvalid)
        .handler(this::handleClick)
        .build());
  }

  private void handleClick(InventoryPreClickEvent event) {
    if (cancelAllClicks()) {
      event.setCancelled(true);
    }

    if (this.itemMap.containsKey(event.getSlot())) {
      this.itemMap.get(event.getSlot()).trigger(event);
    }
  }

  private void handleClose(@NotNull InventoryCloseEvent event) {
    if (event.getInventory() == this.inventory) {
      this.valid = false;
    }
  }

  protected <E extends Event> boolean testInvalid(@NotNull E event) {
    return !this.valid || !this.viewer().isOnline();
  }

  @Override
  public void setItem(int slot, @NotNull Item item) {
    this.itemMap.put(slot, item);
    this.inventory.setItemStack(slot, item.itemStack());
  }

  @Override
  public void removeItem(int slot) {
    this.itemMap.remove(slot);
    this.inventory.setItemStack(slot, ItemStack.AIR);
  }

  @Override
  public void addItem(@NotNull Item item) {
    setItem(nextFreeSlot(), item);
  }

  private int nextFreeSlot() {
    int result = 0;
    while (this.itemMap.containsKey(result)) {
      result++;
    }
    return result;
  }
}
