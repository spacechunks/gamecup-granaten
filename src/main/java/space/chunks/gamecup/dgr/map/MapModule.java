package space.chunks.gamecup.dgr.map;

import com.google.inject.multibindings.MapBinder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.utils.Direction;
import space.chunks.gamecup.dgr.AbstractGameModule;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.TestMapObject;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig;
import space.chunks.gamecup.dgr.map.object.impl.flight.FlightRadarConfig.DestinationConfig;
import space.chunks.gamecup.dgr.map.object.impl.flight.RealisticFlightRadar;
import space.chunks.gamecup.dgr.map.object.impl.flight.monitor.FlightMonitor;
import space.chunks.gamecup.dgr.map.object.impl.flight.monitor.FlightMonitorConfig;
import space.chunks.gamecup.dgr.map.object.impl.marketing.Marketing;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry;
import space.chunks.gamecup.dgr.map.object.impl.marketing.MarketingConfigEntry.Level;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatScanner;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatScannerConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckFailedIncident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.PassControlProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlConfig;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlProcedure;
import space.chunks.gamecup.dgr.map.object.impl.trash.Trash;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistryImpl;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetup;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetupConfig;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetupImpl;
import space.chunks.gamecup.dgr.map.object.upgradable.upgrader.Upgrader;
import space.chunks.gamecup.dgr.map.object.upgradable.upgrader.UpgraderConfig;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentities;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentitiesConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig.Slot;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueConfig.SlotOccupyStrategy;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * @author Nico_ND1
 */
public final class MapModule extends AbstractGameModule {
  @Override
  protected void configure() {
    bind(MapObjectTypeRegistry.class).to(MapObjectTypeRegistryImpl.class).asEagerSingleton();

    bindConfig(PassengerIdentitiesConfig.class, new File("template/config/passenger_identities.json"));
    bind(PassengerIdentities.class).asEagerSingleton();

    MapBinder<String, MapObject> mapObjectTypeBinder = MapBinder.newMapBinder(binder(), String.class, MapObject.class);
    mapObjectTypeBinder.addBinding("test").to(TestMapObject.class);
    mapObjectTypeBinder.addBinding(Procedure.SECURITY_CHECK).to(SecurityCheckProcedure.class);
    mapObjectTypeBinder.addBinding("security_check_failed_incident").to(SecurityCheckFailedIncident.class);
    mapObjectTypeBinder.addBinding(Procedure.TICKET_CONTROL).to(TicketControlProcedure.class);
    mapObjectTypeBinder.addBinding(Procedure.PASS_CONTROL).to(PassControlProcedure.class);
    mapObjectTypeBinder.addBinding("flight_radar").to(RealisticFlightRadar.class);
    mapObjectTypeBinder.addBinding("flight_monitor").to(FlightMonitor.class);
    mapObjectTypeBinder.addBinding(Procedure.LUGGAGE_CLAIM).to(LuggageClaimProcedure.class);
    mapObjectTypeBinder.addBinding(Procedure.MARKETING).to(Marketing.class);
    mapObjectTypeBinder.addBinding("seat").to(SeatProcedure.class);
    mapObjectTypeBinder.addBinding("seat_scanner").to(SeatScanner.class);
    mapObjectTypeBinder.addBinding("trash").to(Trash.class);
    mapObjectTypeBinder.addBinding("upgrader").to(Upgrader.class);

    bind(MapObjectDefaultSetupConfig.class).toInstance(new MapObjectDefaultSetupConfig(
        List.of(
            new SecurityCheckConfig(
                "security_check_1",
                0.9,
                Map.ofEntries(
                    Map.entry("security_check_success_rate", new Double[]{1.0, 1.1, 1.3, 1.5}),
                    Map.entry("procedure_amount", new Double[]{1.0, 1.0, 2.0, 2.0})
                ),
                null,
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
                    ),
                    SlotOccupyStrategy.LAST_EMPTY
                ),
                new Pos(-40.5, -56.0, -13.5, 0, 0),
                new Pos(-41.5, -55.5, -12.5, -180, 0)
            ),
            new SecurityCheckConfig(
                "security_check_2",
                0.9,
                null,
                3,
                new Pos(-41.5, -56.0, -8.5, -90, 0),
                new Pos(-37.5, -56.0, -8.5, -90, 0),
                new PassengerQueueConfig(
                    null,
                    "security_check_2_queue",
                    new Pos(-47.5, -56.0, -8.5),
                    List.of(
                        new Slot(new Pos(-44.5, -56.0, -8.5)),
                        new Slot(new Pos(-45.5, -56.0, -8.5)),
                        new Slot(new Pos(-46.5, -56.0, -8.5))
                    ),
                    SlotOccupyStrategy.LAST_EMPTY
                ),
                new Pos(-40.5, -56.0, -9.5, 0, 0),
                new Pos(-41.5, -55.5, -8.5, -180, 0)
            )
        ),
        List.of(
            new TicketControlConfig(
                "ticket_control_1",
                Map.of(),
                null,
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
                    ),
                    SlotOccupyStrategy.LAST_EMPTY
                ),
                new Pos(-33.5, -56.0, -19.5),
                new Pos(-33.3, -55.0, -19.0)
            ),
            new TicketControlConfig(
                "ticket_control_2",
                null,
                2,
                new Pos(-31.5, -56.0, -17.5, -180, 0),
                new Pos(-30.5, -56.0, -17.5),
                new PassengerQueueConfig(
                    null,
                    "ticket_control_1_queue",
                    new Pos(-33.5, -56.0, -13.5),
                    List.of(
                        new Slot(new Pos(-31.5, -56.0, -16.5)),
                        new Slot(new Pos(-31.5, -56.0, -15.5)),
                        new Slot(new Pos(-32.5, -56.0, -15.5)),
                        new Slot(new Pos(-33.5, -56.0, -15.5)),
                        new Slot(new Pos(-33.5, -56.0, -14.5)),
                        new Slot(new Pos(-32.5, -56.0, -14.5)),
                        new Slot(new Pos(-31.5, -56.0, -14.5)),
                        new Slot(new Pos(-31.5, -56.0, -13.5)),
                        new Slot(new Pos(-32.5, -56.0, -13.5))
                    ),
                    SlotOccupyStrategy.LAST_EMPTY
                ),
                new Pos(-31.5, -56.0, -19.5),
                new Pos(-31.3, -55.0, -19.0)
            )
        ),
        new MarketingConfigEntry(
            "marketing",
            Map.ofEntries(
                Map.entry("flight_radar_spawn_speed", new Double[]{1.0, 0.9, 0.8, 0.7})
            ),
            null,
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
                        5, 7,
                        20 * 30,
                        20 * 25,
                        new Pos[]{
                            new Pos(-48.5, -56.0, -8.5, -90, 0), new Pos(-48.5, -56.0, -7.5, -90, 0),
                            new Pos(-48.5, -56.0, -13.5, -90, 0), new Pos(-48.5, -56.0, -12.5, -90, 0)
                        },
                        new Pos[]{
                            new Pos(-22.5, -56.0, -24.5, -180, 0),
                            new Pos(-22.5, -56.0, -24.5, -180, 0)
                        }
                    ),
                    new DestinationConfig(
                        Destination.ARRIVING,
                        3,
                        2, 5,
                        20 * 5,
                        20 * 20,
                        new Pos[]{
                            new Pos(27.5, -56.0, -13.5, 90, 0), new Pos(27.5, -56.0, -12.5, 90, 0),
                            new Pos(27.5, -56.0, -8.5, 90, 0), new Pos(27.5, -56.0, -7.5, 90, 0),
                        },
                        new Pos[]{
                            new Pos(9.5, -56.0, -24.5, -180, 0),
                            new Pos(9.5, -56.0, 3.5, 0, 0)
                        }
                    )
                )
            )
        ),
        List.of(
            new FlightMonitorConfig(
                "flight_monitor_1",
                Destination.LEAVING,
                new Pos(-8.0, -50.0, -10.5, -90, 0),
                new Pos(-8.14, -48.5, -10.5, 90, 0),
                new Pos(-8.1, -48.6, -10.5, 90, 0)
            ),
            new FlightMonitorConfig(
                "flight_monitor_2",
                Destination.ARRIVING,
                new Pos(-6.9, -50.0, -10.5, -90, 0),
                new Pos(-6.9, -50.0, -10.5, -90, 0),
                new Pos(-6.9, -50.0, -10.5, -90, 0)
            )
        ),
        List.of(
            new LuggageClaimConfig(
                "luggage_claim_1",
                Map.ofEntries(
                    Map.entry("luggage_claim_speed", new Double[]{1.0, 0.8, 0.6, 0.2})
                ),
                null,
                new Pos(0.5, -56.0, -7.5, 180, 0),
                new PassengerQueueConfig(
                    null,
                    "luggage_claim_1_queue",
                    new Pos(0.5, -56.0, -5.5),
                    List.of(),
                    SlotOccupyStrategy.RANDOM
                ),
                new Pos(-1, -56, 4),
                Direction.NORTH, Direction.WEST
            )
        ),
        List.of(
            new SeatScannerConfig(
                "seat_scanner_1",
                new Vec(-17, -56, -27),
                new Vec(13, -56, 5)
            )
        ),
        List.of(
            new UpgraderConfig(
                "security_check_upgrader",
                Procedure.SECURITY_CHECK,
                new Pos(-43.5, -55.0, -10.5, 90, 0),
                new int[]{10, 50, 100, 200}
            ),
            new UpgraderConfig(
                "luggage_claim_upgrader",
                Procedure.LUGGAGE_CLAIM,
                new Pos(0.5, -55.0, -6.5, 180, 0),
                new int[]{50, 200, 500}
            )
        )
    ));
    bind(MapObjectDefaultSetup.class).to(MapObjectDefaultSetupImpl.class);
  }
}
