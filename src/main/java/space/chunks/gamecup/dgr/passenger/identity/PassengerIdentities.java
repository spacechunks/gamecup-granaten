package space.chunks.gamecup.dgr.passenger.identity;

import com.google.inject.Inject;
import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.passenger.Passenger;

import java.util.List;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
public final class PassengerIdentities {
  private final List<PassengerIdentity> fixedIdentities;
  private final List<String> randomNames;
  private final List<PlayerSkin> randomSkins;

  @Inject
  public PassengerIdentities(@NotNull PassengerIdentitiesConfig config) {
    this.fixedIdentities = config.fixedIdentities().stream()
        .map(fixed -> new PassengerIdentity(fixed.uuid() == null ? UUID.randomUUID() : fixed.uuid(), fixed.name(), fixed.skin()))
        .toList();
    this.randomNames = config.randomNames();
    this.randomSkins = config.randomSkins();
  }

  public @NotNull PassengerIdentity random(@NotNull Passenger passenger) {
    double fixedIdentityChance = (double) this.fixedIdentities.size() / (double) this.randomNames.size();
    if (Math.random() < fixedIdentityChance) {
      return randomFixed(passenger);
    }
    return createRandom();
  }

  private @NotNull PassengerIdentity randomFixed(@NotNull Passenger passenger) {
    int tries = this.fixedIdentities.size() / 5;
    PassengerIdentity fixedIdentity;
    do {
      fixedIdentity = this.fixedIdentities.get((int) (Math.random() * this.fixedIdentities.size()));
    } while (!fixedIdentity.occupy(passenger) && tries-- > 0);

    if (!fixedIdentity.isOccupied() || fixedIdentity.occupant() != passenger) {
      return createRandom();
    }
    return fixedIdentity;
  }

  private @NotNull PassengerIdentity createRandom() {
    return new PassengerIdentity(
        UUID.randomUUID(),
        this.randomNames.get((int) (Math.random() * this.randomNames.size())),
        this.randomSkins.get((int) (Math.random() * this.randomSkins.size()))
    );
  }
}
