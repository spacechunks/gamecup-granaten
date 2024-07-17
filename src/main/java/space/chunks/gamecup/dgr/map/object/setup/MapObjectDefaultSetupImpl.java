package space.chunks.gamecup.dgr.map.object.setup;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig;
import space.chunks.gamecup.dgr.map.object.impl.flight.monitor.FlightMonitorConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatScannerConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlConfig;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.upgradable.upgrader.UpgraderConfig;


/**
 * @author Nico_ND1
 */
public class MapObjectDefaultSetupImpl implements MapObjectDefaultSetup {
  private final MapObjectDefaultSetupConfig config;
  private final MapObjectTypeRegistry registry;

  @Inject
  public MapObjectDefaultSetupImpl(@NotNull MapObjectDefaultSetupConfig config, @NotNull MapObjectTypeRegistry registry) {
    this.config = config;
    this.registry = registry;
  }

  @Override
  public void createDefaultObjects(@NotNull Map map) {
    createSecurityChecks(map);
    createTicketControls(map);
    createMarketing(map);
    createFlightRadars(map);
    createFlightMonitors(map);
    createLuggageClaims(map);
    createSeatScanners(map);
    createUpgraders(map);
  }

  private void createTicketControls(@NotNull Map map) {
    for (TicketControlConfig ticketControlConfig : this.config.ticketControls()) {
      MapObject ticketControl = this.registry.create("ticket_control", ticketControlConfig);
      map.queueMapObjectRegister(ticketControl);
    }
  }

  private void createMarketing(@NotNull Map map) {
    MapObject marketing = this.registry.create("marketing", this.config.marketing());
    map.queueMapObjectRegister(marketing);
  }

  private void createSecurityChecks(@NotNull Map map) {
    for (SecurityCheckConfig securityCheckConfig : this.config.securityChecks()) {
      MapObject securityCheck = this.registry.create("security_check", securityCheckConfig);
      map.queueMapObjectRegister(securityCheck);
    }
  }

  private void createFlightRadars(@NotNull Map map) {
    for (FlightRadarConfig flightRadar : this.config.flightRadars()) {
      MapObject flightRadarObject = this.registry.create("flight_radar", flightRadar);
      map.queueMapObjectRegister(flightRadarObject);
    }
  }

  private void createFlightMonitors(@NotNull Map map) {
    for (FlightMonitorConfig flightMonitorConfig : this.config.flightMonitors()) {
      MapObject flightMonitor = this.registry.create("flight_monitor", flightMonitorConfig);
      map.queueMapObjectRegister(flightMonitor);
    }
  }

  private void createLuggageClaims(@NotNull Map map) {
    for (LuggageClaimConfig luggageClaimConfig : this.config.luggageClaims()) {
      MapObject luggageClaim = this.registry.create("luggage_claim", luggageClaimConfig);
      map.queueMapObjectRegister(luggageClaim);
    }
  }

  private void createSeatScanners(@NotNull Map map) {
    for (SeatScannerConfig seatScanner : this.config.seatScanners()) {
      MapObject seatScannerObject = this.registry.create("seat_scanner", seatScanner);
      map.queueMapObjectRegister(seatScannerObject);
    }
  }

  private void createUpgraders(Map map) {
    for (UpgraderConfig upgraderConfig : this.config.upgraders()) {
      MapObject seatScannerObject = this.registry.create("upgrader", upgraderConfig);
      map.queueMapObjectRegister(seatScannerObject);
    }
  }
}
