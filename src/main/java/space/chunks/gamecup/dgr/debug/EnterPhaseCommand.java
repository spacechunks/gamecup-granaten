package space.chunks.gamecup.dgr.debug;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import space.chunks.gamecup.dgr.Game;
import space.chunks.gamecup.dgr.phase.Phase;
import space.chunks.gamecup.dgr.phase.handler.PhaseHandler;


/**
 * @author Nico_ND1
 */
public class EnterPhaseCommand extends Command {
  private final Game game;

  @Inject
  public EnterPhaseCommand(Game game) {
    super("enterphase");
    this.game = game;

    addSyntax(this::execute, ArgumentType.String("phase").setSuggestionCallback(this::suggest));
  }

  private void execute(CommandSender commandSender, CommandContext commandContext) {
    PhaseHandler phaseHandler = this.game.phases();
    String phaseName = commandContext.get("phase");

    try {
      commandSender.sendMessage(Component.text("Entering phase "+phaseName));
      phaseHandler.enterPhase(phaseName);
      commandSender.sendMessage(Component.text("Entered phase "+phaseName));
    } catch (NullPointerException ignored) {
      commandSender.sendMessage(Component.text("Phase not found for "+phaseName));
    }
  }

  private void suggest(CommandSender commandSender, CommandContext commandContext, Suggestion suggestion) {
    PhaseHandler phaseHandler = this.game.phases();
    for (Phase phase : phaseHandler.registeredPhases()) {
      suggestion.addEntry(new SuggestionEntry(phase.name(), Component.text("Text??")));
    }
  }
}
