package space.chunks.gamecup.dgr.passenger.identity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.UUID;


/**
 * @author Nico_ND1
 */
@RequiredArgsConstructor
@Accessors(fluent=true)
@Getter
public class PassengerIdentity {
  private final UUID uuid;
  private final String name;
  private final PlayerSkin skin;
  private Passenger occupant;

  public boolean isOccupied() {
    return this.occupant != null;
  }

  public boolean occupy(@NotNull Passenger occupant) {
    if (isOccupied()) {
      return false;
    }
    this.occupant = occupant;
    return true;
  }

  public void free() {
    this.occupant = null;
  }
}
