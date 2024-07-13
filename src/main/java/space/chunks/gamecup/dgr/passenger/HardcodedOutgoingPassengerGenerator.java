package space.chunks.gamecup.dgr.passenger;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.minestom.server.coordinate.Pos;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.Map;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;


/**
 * @author Nico_ND1
 */
public class HardcodedOutgoingPassengerGenerator implements PassengerGenerator {
  private final GameFactory factory;
  private final Game game;
  private final Map map;
  private final PassengerGeneratorConfig config;

  private final List<Pos> spawnPositions;
  private int passengersJoining;

  @AssistedInject
  public HardcodedOutgoingPassengerGenerator(GameFactory factory, @Assisted Map map, @Assisted @Nullable PassengerGeneratorConfig config, Game game) {
    this.factory = factory;
    this.game = game;
    this.map = map;
    this.config = config;
    this.spawnPositions = Arrays.asList(new Pos(-48.5, -56.0, -8.5), new Pos(-48.5, -56.0, -12.5));
  }

  @Override
  public void tick(int currentTick) {
    if (currentTick % 60 == 0) {
      //Passenger passenger = this.factory.create
    }
  }
}
