package space.chunks.gamecup.dgr;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.incident.TroubleMaker;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.team.Team;


/**
 * @author Nico_ND1
 */
public interface GameFactory {
  @NotNull
  Team createTeam();

  @NotNull
  Map createMap(@NotNull Team owner);

  @NotNull
  MapObjectRegistry createMapObjectRegistry(@NotNull Map parent);

  @NotNull
  TroubleMaker createTroubleMaker(@NotNull Map parent);
}
