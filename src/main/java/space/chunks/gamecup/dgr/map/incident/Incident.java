package space.chunks.gamecup.dgr.map.incident;

import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.Named;
import space.chunks.gamecup.dgr.map.machine.Machine;
import space.chunks.gamecup.dgr.map.object.MapObject;

import java.util.Set;


/**
 * @author Nico_ND1
 */
public interface Incident extends MapObject, Named {
  @NotNull
  Set<Machine> blockedMachines();
}
