package space.chunks.gamecup.dgr.map.object.impl.flight.monitor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
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
  private Entity boardEntity;

  @Override
  protected @NotNull Class<FlightMonitorConfig> configClass() {
    return FlightMonitorConfig.class;
  }

  @Override
  public synchronized void handleRegister(@NotNull Map parent) {
    super.handleRegister(parent);

    this.boardEntity = new Entity(EntityType.TEXT_DISPLAY);
    this.boardEntity.setNoGravity(true);
    this.boardEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
      meta.setWidth(2F);
      meta.setHeight(2F);
      meta.setScale(new Vec(1.4F, 1.4F, 1.4F));
      meta.setBackgroundColor(-15073312);
      meta.setAlignLeft(true);
    });
    this.boardEntity.setInstance(parent.instance(), this.config.spawnPos());
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
    flights.sort(Comparator.comparingInt(Flight::targetFinishTick));
    updateDisplay(currentTick, flights);
    return TickResult.CONTINUE;
  }

  private void updateDisplay(int currentTick, @NotNull List<Flight> flights) {
    this.boardEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
      Component component = Component.text(switch (this.config.destination()) {
        case ARRIVING -> "Arrivals";
        case LEAVING -> "Departures";
      }).color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD);

      for (int i = 0; i < 5; i++) {
        if (i >= flights.size()) {
          component = component.appendNewline()
              .append(Component.text(" - /").color(NamedTextColor.DARK_GRAY));
          continue;
        }

        Flight flight = flights.get(i);
        if (flight.config().destination() != this.config.destination()) {
          continue;
        }

        int ticksLeft = flight.targetFinishTick()-currentTick;

        Component delayComponent = Component.empty(); // TODO
        Component boardingComponent = Component.empty();
        if (Boolean.TRUE.equals(flight.isBoarding())) {
          boardingComponent = Component.text("(").color(NamedTextColor.DARK_GRAY)
              .append(Component.text("BOARDING").style(Style.style(TextDecoration.ITALIC)).color(NamedTextColor.GREEN))
              .append(Component.text(")"));
        }

        component = component.appendNewline()
            .append(switch (this.config.destination()) {
              case ARRIVING -> Component.text("<- ");
              case LEAVING -> Component.text("-> ");
            })
            .append(Component.text(flight.airportName() != null ? flight.airportName() : flight.config().destination().name()))
            .append(Component.text(": "))
            .append(Component.text(String.format("%.2f", flight.progress() * 100))).append(Component.text("%"))
            .append(Component.text(" ")).append(delayComponent)
            .append(Component.text(" ")).append(boardingComponent);
      }

      if (flights.size() > 5) {
        int notShownFlights = flights.size()-5;
        component = component.appendNewline()
            .append(Component.text("(").color(NamedTextColor.GRAY))
            .append(Component.text("+ "+notShownFlights+" more").color(NamedTextColor.GRAY).style(Style.style(TextDecoration.ITALIC)))
            .append(Component.text(")").color(NamedTextColor.GRAY));
      } else {
        component = component.appendNewline();
      }

      meta.setText(component);
    });
  }
}
