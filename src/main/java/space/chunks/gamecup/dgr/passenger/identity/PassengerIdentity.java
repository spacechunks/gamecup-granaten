package space.chunks.gamecup.dgr.passenger.identity;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Nico_ND1
 */
@Accessors(fluent=true)
@Getter
public class PassengerIdentity {
  private final UUID uuid;
  private final boolean uuidGenerated;
  private final String name;
  private PlayerSkin skin;

  private final Map<space.chunks.gamecup.dgr.map.Map, Passenger> occupants;
  private final Lock lock = new ReentrantLock();

  public PassengerIdentity(@Nullable UUID uuid, @NotNull String name, @Nullable PlayerSkin skin) {
    this.uuidGenerated = uuid == null;
    if (this.uuidGenerated) {
      this.uuid = UUID.randomUUID();
    } else {
      this.uuid = uuid;
    }
    this.name = name;
    this.skin = skin;
    this.occupants = new HashMap<>();
  }

  public boolean isValid() {
    return this.uuid != null && this.name != null && this.skin != null;
  }

  public @NotNull PlayerSkin skin() {
    if (this.skin == null) {
      if (this.uuidGenerated) {
        this.skin = PlayerSkin.fromUsername(this.name);
      } else {
        this.skin = PlayerSkin.fromUuid(this.uuid.toString().replace("-", ""));
      }
    }
    assert this.skin != null;
    return this.skin;
  }

  public boolean isOccupied(@NotNull space.chunks.gamecup.dgr.map.Map map) {
    try {
      this.lock.lock();
      Passenger occupant = this.occupants.get(map);
      return occupant != null && occupant.isValid();
    } finally {
      this.lock.unlock();
    }
  }

  public boolean occupy(@NotNull space.chunks.gamecup.dgr.map.Map map, @NotNull Passenger occupant) {
    try {
      this.lock.lock();

      if (isOccupied(map)) {
        return false;
      }
      this.occupants.put(map, occupant);
      return true;
    } finally {
      this.lock.unlock();
    }
  }

  public void free(@NotNull space.chunks.gamecup.dgr.map.Map map) {
    try {
      this.lock.lock();
      this.occupants.remove(map);
    } finally {
      this.lock.unlock();
    }
  }
}
