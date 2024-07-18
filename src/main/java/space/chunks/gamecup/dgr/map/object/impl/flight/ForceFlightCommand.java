package space.chunks.gamecup.dgr.map.object.impl.flight;

import com.google.inject.Inject;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;


/**
 * @author Nico_ND1
 */
public class ForceFlightCommand extends Command {
  private final Game game;

  @Inject
  public ForceFlightCommand(Game game) {
    super("forceflight");
    this.game = game;

    addSyntax(this::execute, ArgumentType.Enum("destination", Destination.class));
  }

  private void execute(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;
    Destination destination = commandContext.get("destination");

    this.game.findTeam(player).ifPresentOrElse(team -> {
      for (FlightRadar flightRadar : team.map().objects().allOfType(FlightRadar.class)) {
        flightRadar.forceCreate(destination);
        player.sendMessage("Forced flight to "+destination+"!");
      }
    }, () -> player.sendMessage("You are not in a team."));

  }
}
