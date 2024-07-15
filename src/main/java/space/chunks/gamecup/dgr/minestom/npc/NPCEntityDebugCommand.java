package space.chunks.gamecup.dgr.minestom.npc;

import com.google.inject.Inject;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity.Pose;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class NPCEntityDebugCommand extends Command {
  @Inject
  public NPCEntityDebugCommand() {
    super("npcdebug");

    addSyntax(this::executePose, ArgumentType.Literal("spawn"), ArgumentType.String("name"), ArgumentType.Enum("pose", Pose.class));
  }

  private void executePose(CommandSender commandSender, CommandContext commandContext) {
    Player player = (Player) commandSender;
    String name = commandContext.get("name");
    Pose pose = commandContext.get("pose");

    NPCEntity entity = new NPCEntity(UUID.randomUUID(), name, new PlayerSkin(
        "ewogICJ0aW1lc3RhbXAiIDogMTcyMTA1OTA2MjcxNSwKICAicHJvZmlsZUlkIiA6ICJhMjk1ODZmYmU1ZDk0Nzk2OWZjOGQ4ZGE0NzlhNDNlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZXZlMjQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA3ZjZmOGQxOWJhNmQ1MGE0MTY4ZmFjY2ZlNjg0NzQ4MWQ2MzcxMDRmZTcxYmMyMDNiOWUxMWNlNTc1MTM4YSIKICAgIH0KICB9Cn0=",
        "L8bPLoZNzg+jA2s3moQG+4ZTeTqlWRJH7gQkaneg4htDS1s7N+3tIFQE0K5daWZkuYmS39aDo0qstCgwqrwGd+sqqi18BtrN3PC+VV/xL57O3E57CCxRJrJ3Fw9K0LvFj26B5S9KdwAFY3s6inawIIBHroH+4mxdfynKBu7dwOyTg5gAkjWa5gXDCJxjTQfgxM2UhXJWJKWn02oAUQ8ixvjdo/oPLQHB/FRFt0iVIV1czKgeSyGZsSGKaM8/F5ndtt1SmRT7CtnmPHsIFzjFhuLqSKbS0TrOuqsXwzsajNp9yrUKMf7MprkxCxsTP4IDnK7Ky8ygDMU6Wfn2nQKGPtiEKARd3trxFbso+IpzmlzEgsBWs7NMYk8zIVI8Y3Fo/rXIT+1nRn4fKeTTBSFORMG5S4yYESHgzvHEgOQ3p55OjavfGIaaX885v/DKmU4Xx5UqjAFXCtL6HhJ9vSH5/7b4GdS+GFdjAn+RgqNQ3C9bvbeslorI3vUrBrbX0Zz7B3yRf01MQSawjfPhDTd9/cSyN6XEWtxi7pAgiphpMLAw2SVfafmZlQTX4DYxvwh8iZCqh12aXzvCxs7NmAuAz11zVszBVBGqA3FhsMvWZsmy+Vm6UOqn5k2vUnrp7j7u7m3l3MJHJc86e/AOFzzgyva648YrPGTzlMMe0DsgT2Y="
    ));
    entity.setInstance(player.getInstance(), player.getPosition());
    entity.setPose(pose);
  }
}
