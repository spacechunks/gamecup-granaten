package space.chunks.gamecup.dgr.passenger.identity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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

  private final Lock lock = new ReentrantLock();

  public boolean isOccupied() {
    try {
      this.lock.lock();
      return this.occupant != null;
    } finally {
      this.lock.unlock();
    }
  }

  public boolean occupy(@NotNull Passenger occupant) {
    try {
      this.lock.lock();

      if (isOccupied()) {
        return false;
      }
      this.occupant = occupant;
      return true;
    } finally {
      this.lock.unlock();
    }
  }

  public void free() {
    try {
      this.lock.lock();
      this.occupant = null;
    } finally {
      this.lock.unlock();
    }
  }
}
