package space.chunks.gamecup.dgr.minestom.displays;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class TextDisplay {
  private final Set<Line> lines = new HashSet<>();

  public TextDisplay(Pos pos, Instance instance, TextDisplayFunction @NotNull ... displayFunctions) {
    for (var textDisplayFunction : displayFunctions) {
      this.lines.add(new Line(pos, instance, textDisplayFunction));
    }
  }

  public void update() {
    this.lines.forEach(Line::update);
  }

  @Getter
  @Accessors(fluent=true)
  class Line {
    private final Entity entity;
    private final TextDisplayFunction function;

    public Line(Pos pos, Instance instance, TextDisplayFunction function) {
      this.function = function;
      this.entity = new Entity(EntityType.TEXT_DISPLAY);
      this.entity.setNoGravity(true);
      this.entity.setInstance(instance, pos);
    }

    public void update() {
      this.entity.editEntityMeta(TextDisplayMeta.class, meta -> {
        meta.setText(this.function.text());
      });
    }
  }
}
