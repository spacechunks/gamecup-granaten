package space.chunks.gamecup.dgr.map;

import com.google.inject.multibindings.MapBinder;
import space.chunks.gamecup.dgr.AbstractGameModule;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.TestMapObject;
import space.chunks.gamecup.dgr.map.object.impl.flight.RealisticFlightRadar;
import space.chunks.gamecup.dgr.map.object.impl.flight.monitor.FlightMonitor;
import space.chunks.gamecup.dgr.map.object.impl.marketing.Marketing;
import space.chunks.gamecup.dgr.map.object.impl.procedure.Procedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.luggageclaim.LuggageClaimProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.seats.SeatScanner;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckFailedIncident;
import space.chunks.gamecup.dgr.map.object.impl.procedure.securitycheck.SecurityCheckProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.PassControlProcedure;
import space.chunks.gamecup.dgr.map.object.impl.procedure.ticketcontrol.TicketControlProcedure;
import space.chunks.gamecup.dgr.map.object.impl.trash.Trash;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistryImpl;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetup;
import space.chunks.gamecup.dgr.map.object.setup.MapObjectDefaultSetupLoader;
import space.chunks.gamecup.dgr.map.object.upgradable.upgrader.Upgrader;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentities;
import space.chunks.gamecup.dgr.passenger.identity.PassengerIdentitiesConfig;

import java.io.File;


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

    bind(MapObjectDefaultSetup.class).to(MapObjectDefaultSetupLoader.class);
  }
}
