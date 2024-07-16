package space.chunks.gamecup.dgr.passenger;

import com.google.inject.Inject;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.GameFactory;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;


/**
 * @author Nico_ND1
 */
public class SpawnPassengersCommand extends Command {
  private final Game game;
  private final GameFactory factory;

  @Inject
  public SpawnPassengersCommand(Game game, @NotNull GameFactory factory) {
    super("spawnpassengers");
    this.game = game;
    this.factory = factory;

    addSyntax(this::executeForOutgoing, ArgumentType.Literal("outgoing"));
    addSyntax(this::executeForIncoming, ArgumentType.Literal("incoming"));
  }

  private void executeForIncoming(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;

    this.game.findTeam(player).ifPresentOrElse(team -> {
      PassengerConfig config = new PassengerConfig(player.getPosition(), player.getPosition(), Destination.ARRIVING, 0.75D);
      Passenger passenger = this.factory.createPassenger(config);

      Map map = team.map();
      map.queueMapObjectRegister(passenger);

      player.sendMessage("Queued passenger spawn!");
    }, () -> player.sendMessage("You are not in a team."));
  }

  private void executeForOutgoing(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;

    this.game.findTeam(player).ifPresentOrElse(team -> {
      PassengerConfig config = new PassengerConfig(player.getPosition(), player.getPosition().add(20, 0, 0), Destination.LEAVING, 0.75D);
      Passenger passenger = this.factory.createPassenger(config);

      Map map = team.map();
      map.queueMapObjectRegister(passenger);

      player.sendMessage("Queued passenger spawn!");
    }, () -> player.sendMessage("You are not in a team."));
  }
}
