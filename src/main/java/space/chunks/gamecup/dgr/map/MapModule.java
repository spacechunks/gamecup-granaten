package space.chunks.gamecup.dgr.map;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import space.chunks.gamecup.dgr.flight.FlightRadarConfig;
import space.chunks.gamecup.dgr.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.flight.FlightRadarImpl;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.TestMapObject;
import space.chunks.gamecup.dgr.map.object.impl.flightboard.FlightMonitor;
import space.chunks.gamecup.dgr.map.object.impl.flightboard.FlightMonitorConfig;
import space.chunks.gamecup.dgr.map.object.impl.marketing.Marketing;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry.Level;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheck;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckFailedIncident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControl;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlConfig;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistryImpl;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetup;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetupConfig;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetupImpl;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig.Slot;

import java.util.List;


/**
 * @author Nico_ND1
 */
public final class MapModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(MapObjectTypeRegistry.class).to(MapObjectTypeRegistryImpl.class).asEagerSingleton();

    MapBinder<String, MapObject> mapObjectTypeBinder = MapBinder.newMapBinder(binder(), String.class, MapObject.class);
    mapObjectTypeBinder.addBinding("test").to(TestMapObject.class);
    mapObjectTypeBinder.addBinding("security_check").to(SecurityCheck.class);
    mapObjectTypeBinder.addBinding("security_check_failed_incident").to(SecurityCheckFailedIncident.class);
    mapObjectTypeBinder.addBinding("ticket_control").to(TicketControl.class);
    mapObjectTypeBinder.addBinding("flight_radar").to(FlightRadarImpl.class);
    mapObjectTypeBinder.addBinding("flight_monitor").to(FlightMonitor.class);
    mapObjectTypeBinder.addBinding("marketing").to(Marketing.class);

    bind(MapObjectDefaultSetupConfig.class).toInstance(new MapObjectDefaultSetupConfig(
        List.of(
            new SecurityCheckConfig(
                "security_check_1",
                new Pos(-41.5, -56.0, -12.5, -90, 0),
                new Pos(-37.5, -56.0, -12.5, -90, 0),
                new PassengerQueueConfig(
                    null,
                    "security_check_1_queue",
                    new Pos(-47.5, -56.0, -12.5),
                    List.of(
                        new Slot(new Pos(-44.5, -56.0, -12.5)),
                        new Slot(new Pos(-45.5, -56.0, -12.5)),
                        new Slot(new Pos(-46.5, -56.0, -12.5))
                    )
                ),
                new Pos(-40.5, -56.0, -13.5, 0, 0)
            )
        ),
        List.of(
            new TicketControlConfig(
                "ticket_control_1",
                new Pos(-33.5, -56.0, -17.5, -180, 0),
                new Pos(-32.5, -56.0, -17.5),
                new PassengerQueueConfig(
                    null,
                    "ticket_control_1_queue",
                    new Pos(-35.5, -56.0, -13.5),
                    List.of(
                        new Slot(new Pos(-33.5, -56.0, -16.5)),
                        new Slot(new Pos(-33.5, -56.0, -15.5)),
                        new Slot(new Pos(-34.5, -56.0, -15.5)),
                        new Slot(new Pos(-35.5, -56.0, -15.5)),
                        new Slot(new Pos(-35.5, -56.0, -14.5)),
                        new Slot(new Pos(-34.5, -56.0, -14.5)),
                        new Slot(new Pos(-33.5, -56.0, -14.5)),
                        new Slot(new Pos(-33.5, -56.0, -13.5)),
                        new Slot(new Pos(-34.5, -56.0, -13.5))
                    )
                ),
                new Pos(-33.5, -56.0, -19.5),
                new Pos(-33.3, -55.0, -19.0)
            )
        ),
        new MarketingConfigEntry(
            "marketing",
            new Pos(-17.5, -56.0, -10.5, 90, 0),
            new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTcyMDkwODE0OTgzOCwKICAicHJvZmlsZUlkIiA6ICI0NmNhODkyZTY4ODA0YThmYjFkYzkwYjg0ZTY5ZjVmZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPbG8xNjA2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyMTI2MzZiN2JkZDk3MTcxOTdlY2EwNmM2ZmExNWY1ZGY1Mzg5YjAxMTVkZmYxZjFhYTc5NWZhM2I2Y2Y5YjciCiAgICB9CiAgfQp9",
                "mICpZGBww7LAsWApc/YHeR46/bqe7xGOPBF07+nLJF0PuupKpGftogtncuchnWSV6yakF3fQy1JVKGwJEQCjzUpUHzIzph8zmN2zZJkSKpWB3MJF5BxTrItSKtYPwoOz27XMtnkPdqsjmcQrjkaZtRJ4JJpIqntwTf9wb8+lNh9YqdwH5xkBFUwJjaAKQrmmOJLrwt04uOt/Vb78EgwXUpF4Ij4wo0b/ATVeyXVx8pOOs5ChaDKzncXv+wXgyoT16A6NikyPthpRgTw5nQX6Y/m28irf4YMqZUlQGitRoQ/zm31JdLe12zplpaeKUvTjX96MkwZxMyA7FaFhVF1Mko7Qwafxh+sXAh3SDnlbRdK7qimYz8XcUWf3KxI6R25Iv42sp8IbxTioiLq4U4Jxg8fdFPzXmwqnZO42WXFAXD5F841+MAEJ5BieCMolpMuC9R2JMHXSSmrU2j3OlsOs5F6YZK7g+mmImItPElDOxY+eekY4IZnVwXQR9e138cqZPjPEDqMMMZgOriZ9ErvsNYl/BxOT3DpFUeYOepMD+lGbr308e/aln1ERgr8O73OJ9idLVMIXD7Vghc6UAZ7eGfipD+ODvRegoYkZ+tP0sl34QZ3Jv0IPFjgT2+s4UMJ0q10IAcbMu84WETkct04RUEE68C2bG/nCOLYi0ZWoKJk="
            ),
            List.of(new Level(1, 1), new Level(2, 2))
        ),
        List.of(
            new FlightRadarConfig(
                "flight_radar_1",
                List.of(
                    new DestinationConfig(
                        Destination.LEAVING,
                        3,
                        3, 10,
                        20 * 20,
                        20 * 50,
                        new Pos[]{
                            new Pos(-48.5, -56.0, -8.5, -90, 0), new Pos(-48.5, -56.0, -7.5, -90, 0),
                            new Pos(-48.5, -56.0, -13.5, -90, 0), new Pos(-48.5, -56.0, -12.5, -90, 0)
                        },
                        new Pos[]{
                            new Pos(-22.5, -56.0, -24.5, -180, 0),
                            new Pos(-22.5, -56.0, -24.5, -180, 0)
                        },
                        new String[]{
                            "security_check_1", "ticket_control_1"
                        }
                    )
                )
            )
        ),
        List.of(
            new FlightMonitorConfig(
                "flight_monitor_1",
                new Pos(-7.1, -48.0, -10.5, 90, 30)
            )
        )
    ));
    bind(MapObjectDefaultSetup.class).to(MapObjectDefaultSetupImpl.class);
  }
}
