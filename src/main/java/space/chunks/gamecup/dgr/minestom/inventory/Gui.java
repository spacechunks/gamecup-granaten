package space.chunks.gamecup.dgr.minestom.inventory;

import org.jetbrains.annotations.NotNull;


/**
 * @author Nico_ND1
 */
public interface Gui {
  void open();

  void close();

  void redraw();

  void addItem(@NotNull Item item);

  void setItem(int slot, @NotNull Item item);

  void removeItem(int slot);
}
