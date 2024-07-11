package space.chunks.gamecup.dgr;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import space.chunks.gamecup.dgr.map.incident.Incident;
import space.chunks.gamecup.dgr.team.Team;
import space.chunks.gamecup.dgr.team.TeamImpl;


/**
 * @author Nico_ND1
 */
public final class GameModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GameConfig.class).toInstance(GameConfig.defaultConfig());

    bind(Game.class).to(GameImpl.class).asEagerSingleton();
    bind(Team.class).to(TeamImpl.class);

    // TODO: Add bindings:

    Multibinder.newSetBinder(binder(), Incident.class);
  }
}
