package space.chunks.gamecup.dgr;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import net.minestom.server.command.builder.Command;
import space.chunks.gamecup.dgr.debug.EnterPhaseCommand;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.MapImpl;
import space.chunks.gamecup.dgr.map.MapModule;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistryImpl;
import space.chunks.gamecup.dgr.map.procedure.incident.Incident;
import space.chunks.gamecup.dgr.map.procedure.incident.TroubleMaker;
import space.chunks.gamecup.dgr.map.procedure.incident.TroubleMakerImpl;
import space.chunks.gamecup.dgr.passenger.Passenger;
import space.chunks.gamecup.dgr.passenger.PassengerImpl;
import space.chunks.gamecup.dgr.passenger.SpawnPassengersCommand;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueue;
import space.chunks.gamecup.dgr.passenger.queue.PassengerQueueImpl;
import space.chunks.gamecup.dgr.phase.ActiveGamePhase;
import space.chunks.gamecup.dgr.phase.Phase;
import space.chunks.gamecup.dgr.phase.ShoppingPhase;
import space.chunks.gamecup.dgr.phase.WaitingPhase;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandler;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandlerImpl;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.TeamImpl;
import space.chunks.gamecup.dgr.team.member.Member;
import space.chunks.gamecup.dgr.team.member.MemberImpl;


/**
 * @author Nico_ND1
 */
public final class GameModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new MapModule());

    install(new FactoryModuleBuilder()
        .implement(Map.class, MapImpl.class)
        .implement(MapObjectRegistry.class, MapObjectRegistryImpl.class)
        .implement(Member.class, MemberImpl.class)
        .implement(TroubleMaker.class, TroubleMakerImpl.class)
        .implement(Passenger.class, PassengerImpl.class)
        .implement(PassengerQueue.class, PassengerQueueImpl.class)
        .build(GameFactory.class));

    Multibinder<Phase> phaseBinder = Multibinder.newSetBinder(binder(), Phase.class);
    phaseBinder.addBinding().to(WaitingPhase.class);
    phaseBinder.addBinding().to(ActiveGamePhase.class);
    phaseBinder.addBinding().to(ShoppingPhase.class);

    bind(PhaseHandler.class).to(PhaseHandlerImpl.class).asEagerSingleton();
    bind(GameConfig.class).toInstance(GameConfig.defaultConfig());

    Multibinder<Command> commandsBinder = Multibinder.newSetBinder(binder(), Command.class);
    commandsBinder.addBinding().to(EnterPhaseCommand.class);
    commandsBinder.addBinding().to(SpawnPassengersCommand.class);

    bind(Game.class).to(GameImpl.class).asEagerSingleton();
    bind(GameTickTask.class);

    bind(Team.class).to(TeamImpl.class);

    // TODO: Add bindings:

    Multibinder.newSetBinder(binder(), Incident.class);
    // Mapbinder for Incident?
  }
}
