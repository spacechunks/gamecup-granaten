package space.chunks.gamecup.dgr.map.object.impl.flight.monitor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.impl.flight.Flight;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadar;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.Ticking;

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
    });
    this.boardEntity.setInstance(parent.instance(), this.config.spawnPos());
  }

  @Override
  public @NotNull TickResult tick(@NotNull Map map, int currentTick) {
    if (currentTick % 20 == 0) {
      return TickResult.CONTINUE;
    }

    List<Flight> flights = new ArrayList<>();
    for (FlightRadar flightRadar : map.objects().allOf(FlightRadar.class)) {
      flights.addAll(flightRadar.flights());
    }
    flights.sort(Comparator.comparingInt(Flight::targetFinishTick));

    if (flights.size() > 5) {
      flights = flights.subList(0, 5);
    }

    updateDisplay(currentTick, flights);
    return TickResult.CONTINUE;
  }

  private void updateDisplay(int currentTick, @NotNull List<Flight> flights) {
    this.boardEntity.editEntityMeta(TextDisplayMeta.class, meta -> {
      Component component = Component.text("Flights:");
      for (Flight flight : flights) {
        int ticksLeft = flight.targetFinishTick()-currentTick;
        component = component.appendNewline()
            .append(Component.text(flight.config().destination().name(), Style.style(TextDecoration.BOLD)))
            .append(Component.text(": "))
            .append(Component.text(flight.currentPassengers())).append(Component.text("/")).append(Component.text(flight.passengerGoal())).append(Component.text(" Passengers"))
            .append(Component.text(" - "))
            .append(Component.text(ticksLeft)).append(Component.text(" Ticks"));
      }

      meta.setText(component);
    });
  }
}
