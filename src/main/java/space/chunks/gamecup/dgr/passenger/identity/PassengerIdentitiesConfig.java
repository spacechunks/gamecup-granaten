package space.chunks.gamecup.dgr.passenger.identity;

import net.minestom.server.entity.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
public record PassengerIdentitiesConfig(
    @NotNull List<FixedPassengerIdentityConfig> fixedIdentities,
    @NotNull List<String> randomNames,
    @NotNull List<PlayerSkin> randomSkins
) {
  public record FixedPassengerIdentityConfig(
      @Nullable UUID uuid,
      @NotNull String name,
      @NotNull PlayerSkin skin
  ) {
  }
}
