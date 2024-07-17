package space.chunks.gamecup.dgr.map.object.impl.procedure;

import com.google.inject.Inject;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.team.Team;

import java.util.Optional;


/**
 * @author Nico_ND1
 */
public class ProcedureDebugCommand extends Command {
  private final Game game;

  @Inject
  public ProcedureDebugCommand(Game game) {
    super("procedures");
    this.game = game;

    addSyntax(this::executeInfo, ArgumentType.String("name").setSuggestionCallback(this::suggestName));
    addSyntax(this::executeUpgrade, ArgumentType.String("name").setSuggestionCallback(this::suggestName), ArgumentType.Literal("upgrade"));
  }

  private void executeInfo(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;
    Team team = this.game.findTeam(player).orElseThrow();
    Map map = team.map();

    Optional<MapObject> optionalMapObject = map.objects().find(commandContext.get("name"));
    if (optionalMapObject.isEmpty()) {
      player.sendMessage("Unknown procedure");
      return;
    }

    Procedure procedure = (Procedure) optionalMapObject.get();
    player.sendMessage("Procedure: "+procedure.name());
    player.sendMessage("Level: "+procedure.currentLevel()+"/"+procedure.maxLevel());
  }

  private void executeUpgrade(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;
    Team team = this.game.findTeam(player).orElseThrow();
    Map map = team.map();

    Optional<MapObject> optionalMapObject = map.objects().find(commandContext.get("name"));
    if (optionalMapObject.isEmpty()) {
      player.sendMessage("Unknown procedure");
      return;
    }

    Procedure procedure = (Procedure) optionalMapObject.get();
    boolean upgrade = procedure.upgrade();
    player.sendMessage("Upgraded: "+upgrade);
  }

  private void suggestName(CommandSender commandSender, CommandContext commandContext, Suggestion suggestion) {
    Player player = (Player) commandSender;
    Team team = this.game.findTeam(player).orElseThrow();
    Map map = team.map();
    for (Procedure procedure : map.objects().allOfType(Procedure.class)) {
      suggestion.addEntry(new SuggestionEntry(procedure.name()));
    }
  }
}
