package space.chunks.gamecup.dgr;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;


/**
 * @author Nico_ND1
 */
public interface Named {
  @NotNull
  String name();

  default @NotNull Component displayName() {
    return Component.text(name());
  }
}
