package space.chunks.gamecup.dgr.map.object.impl.flight.monitor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;
import space.chunks.gamecup.dgr.map.object.impl.flight.Flight;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class FlightMonitor extends AbstractMapObject<FlightMonitorConfig> implements MapObject, Ticking {
  private static final int FLIGHT_LINES = 4;

  private Entity boardEntity;
  private List<Entity> boardTextEntities;

  @Override
  protected @NotNull Class<FlightMonitorConfig> configClass() {
    return FlightMonitorConfig.class;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    this.boardEntity = new Entity(EntityType.ITEM_DISPLAY);
    this.boardEntity.setNoGravity(true);
    this.boardEntity.editEntityMeta(ItemDisplayMeta.class, meta -> {
      meta.setItemStack(ItemStack.of(Material.PAPER).withCustomModelData(10));
      meta.setScale(new Vec(3F, 2.5F, 1.5F));
    });
    this.boardEntity.setInstance(parent.instance(), this.config.spawnPos());

    int lines = 1+FLIGHT_LINES+1;
    this.boardTextEntities = new ArrayList<>(lines);
    for (int i = 0; i < lines; i++) {
      Entity boardEntity = new Entity(EntityType.TEXT_DISPLAY);
      boardEntity.setNoGravity(true);
      boardEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
        //meta.setWidth(2F);
        //meta.setHeight(2F);
        meta.setScale(new Vec(1.4F, 1.4F, 1.4F));
        //meta.setBackgroundColor(i % 2 == 0 ? -13465176 : -15767673);
        meta.setAlignLeft(true);
        meta.setUseDefaultBackground(false);
        meta.setBackgroundColor(0);
        meta.setSeeThrough(false);
        meta.setShadow(false);
      });

      Pos spawnPos = this.config.boardStartPos().sub(0, (0.4-0.03125+0.015625) * i, 0);
      if (i == 0) {
        spawnPos = this.config.boardHeaderPos();
      }
      boardEntity.setInstance(parent.instance(), spawnPos);

      this.boardTextEntities.add(boardEntity);
    }
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (currentTick % 20 == 0) {
      return TickResult.CONTINUE;
    }

    List<Flight> flights = new ArrayList<>();
    for (FlightRadar flightRadar : map.objects().allOfType(FlightRadar.class)) {
      flights.addAll(flightRadar.flights().stream()
          .filter(flight -> flight.config().destination() == this.config.destination())
          .toList());
    }
    flights.sort(
        Comparator.comparingInt((Flight flight) -> Boolean.TRUE.equals(flight.isBoarding()) ? -1 : 0)
            .thenComparingInt(Flight::targetFinishTick)
    );
    updateDisplay(currentTick, flights);
    return TickResult.CONTINUE;
  }

  private void updateDisplay(int currentTick, @NotNull List<Flight> flights) {
    Entity headEntity = this.boardTextEntities.getFirst();
    headEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
      meta.setText(Component.text(switch (this.config.destination()) {
        case ARRIVING -> "Arrivals";
        case LEAVING -> "Departures";
      }).color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD));
    });

    for (int i = 0; i < FLIGHT_LINES; i++) {
      int j = i;
      Entity entity = this.boardTextEntities.get(i+1);

      entity.editEntityMeta(TextDisplayMeta.class, meta -> {
        Component textComponent = Component.empty();
        if (j >= flights.size()) {
          textComponent = textComponent.append(Component.text(" - /").color(NamedTextColor.DARK_GRAY));
          meta.setText(textComponent);
          return;
        }

        Flight flight = flights.get(j);
        int ticksLeft = flight.targetFinishTick()-currentTick;

        Component delayComponent = Component.empty(); // TODO
        Component boardingComponent = Component.empty();
        if (Boolean.TRUE.equals(flight.isBoarding())) {
          boardingComponent = Component.text("(").color(NamedTextColor.DARK_GRAY)
              .append(Component.text("BOARDING").style(Style.style(TextDecoration.ITALIC)).color(NamedTextColor.GREEN))
              .append(Component.text(")"));
        }

        textComponent = textComponent.append(switch (this.config.destination()) {
              case ARRIVING -> Component.text("<- ");
              case LEAVING -> Component.text("-> ");
            })
            .append(Component.text(flight.airportName() != null ? flight.airportName() : flight.config().destination().name()))
            .append(Component.text("("+flight.spawnedPassengers()+"/"+flight.passengerGoal()+")"))
            .append(Component.text(": "))
            .append(Component.text(String.format("%.2f", flight.progress() * 100))).append(Component.text("%"))
            .append(Component.text(" ")).append(delayComponent)
            .append(Component.text(" ")).append(boardingComponent);
        meta.setText(textComponent);
      });

      Entity footerEntity = this.boardTextEntities.getLast();
      footerEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
        if (flights.size() > FLIGHT_LINES) {
          int notShownFlights = flights.size()-FLIGHT_LINES;
          meta.setText(Component.text("(").color(NamedTextColor.GRAY)
              .append(Component.text("+ "+notShownFlights+" more").color(NamedTextColor.GRAY).style(Style.style(TextDecoration.ITALIC)))
              .append(Component.text(")").color(NamedTextColor.GRAY)));
        } else {
          meta.setText(Component.empty());
        }
      });
    }
  }
}