package space.chunks.gamecup.dgr.flight;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.passenger.Passenger.Destination;

import java.util.List;


/**
 * @author Nico_ND1
 */
public record FlightRadarConfig(
    @NotNull String name,
    @NotNull List<DestinationConfig> destinations
) implements MapObjectConfigEntry {
  public record DestinationConfig(
      @NotNull Destination destination,
      int maxConcurrentFlights,
      int basePassengerCountMin,
      int basePassengerCountMax,
      int baseCreateDelayMin,
      int baseCreateDelayMax,
      @NotNull Pos[] spawnPositions,
      @NotNull Pos[] leavePositions,
      @NotNull String[] procedures
  ) {
    public int randomDelay() {
      return (int) (Math.random() * (baseCreateDelayMax()-baseCreateDelayMin())+baseCreateDelayMin());
    }

    public int randomPassengers() {
      return (int) (Math.random() * (basePassengerCountMax()-basePassengerCountMin())+basePassengerCountMin());
    }

    public @NotNull Pos randomSpawnPosition() {
      return spawnPositions[(int) (Math.random() * spawnPositions.length)];
    }

    public @NotNull Pos randomLeavePosition() {
      return leavePositions[(int) (Math.random() * leavePositions.length)];
    }
  }
}
