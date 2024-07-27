package space.chunks.gamecup.dgr.map.object.setup;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradableCondition;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradableConfig;

import java.util.List;


/**
 * @author Nico_ND1
 */
public class MapObjectDefaultSetupHardcoded implements MapObjectDefaultSetup {
  private final MapObjectDefaultSetupConfig config;
  private final MapObjectTypeRegistry registry;

  @Inject
  public MapObjectDefaultSetupHardcoded(@NotNull MapObjectDefaultSetupConfig config, @NotNull MapObjectTypeRegistry registry) {
    this.config = config;
    this.registry = registry;
  }

  @Override
  public void createDefaultObjects(@NotNull Map map) {
    acknowledgeList(map, Procedure.SECURITY_CHECK, this.config.securityChecks());
    acknowledgeList(map, Procedure.TICKET_CONTROL, this.config.ticketControls());
    acknowledge(map, Procedure.MARKETING, this.config.marketing());
    acknowledgeList(map, "flight_radar", this.config.flightRadars());
    acknowledgeList(map, "flight_monitor", this.config.flightMonitors());
    acknowledgeList(map, Procedure.LUGGAGE_CLAIM, this.config.luggageClaims());
    acknowledgeList(map, "seat_scanner", this.config.seatScanners());
    acknowledgeList(map, "upgrader", this.config.upgraders());
  }

  private <C extends MapObjectConfigEntry> void acknowledgeList(@NotNull Map map, @NotNull String type, @NotNull List<C> configEntries) {
    for (MapObjectConfigEntry configEntry : configEntries) {
      acknowledge(map, type, configEntry);
    }
  }

  private void acknowledge(@NotNull Map map, @NotNull String type, @Nullable MapObjectConfigEntry configEntry) {
    MapObject mapObject = this.registry.create(type, configEntry);
    if (configEntry instanceof UpgradableConfig upgradableConfig) {
      Integer minLevel = upgradableConfig.minLevel();

      if (minLevel != null) {
        map.queueMapObjectRegister(mapObject, new UpgradableCondition(map, type, minLevel));
      } else {
        map.queueMapObjectRegister(mapObject);
      }
    } else {
      map.queueMapObjectRegister(mapObject);
    }
  }
}
