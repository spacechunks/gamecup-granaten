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

    addSyntax(this::execute, ArgumentType.Enum("destination", Destination.class), ArgumentType.Integer("amount").setDefaultValue(1));
  }

  private void execute(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;
    Destination destination = commandContext.get("destination");
    int amount = commandContext.get("amount");

    this.game.findTeam(player).ifPresentOrElse(team -> {
      for (FlightRadar flightRadar : team.map().objects().allOfType(FlightRadar.class)) {
        for (int i = 0; i < amount; i++) {
          flightRadar.forceCreate(destination);
        }
        player.sendMessage("Forced "+amount+" flight(s) to "+destination+"!");
      }
    }, () -> player.sendMessage("You are not in a team."));

  }
}
