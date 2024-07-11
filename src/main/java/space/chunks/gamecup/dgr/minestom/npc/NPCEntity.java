package space.chunks.gamecup.dgr.minestom.npc;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Action;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Entry;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Property;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;


/**
 * @author Nico_ND1
 */
public class NPCEntity extends EntityCreature {
  private final String username;
  private final PlayerSkin skin;

  public NPCEntity(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerSkin skin) {
    super(EntityType.PLAYER, uuid);

    setBoundingBox(0.6F, 1.8F, 0.6F);
    getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2F);
    setupMetadata();

    this.username = username;
    this.skin = skin;
  }

  protected void setupMetadata() {
    PlayerMeta playerMeta = new PlayerMeta(this, this.metadata);
    playerMeta.setNotifyAboutChanges(false);

    playerMeta.setCapeEnabled(true);
    playerMeta.setHatEnabled(true);
    playerMeta.setJacketEnabled(true);
    playerMeta.setLeftLegEnabled(true);
    playerMeta.setRightLegEnabled(true);
    playerMeta.setLeftSleeveEnabled(true);
    playerMeta.setRightSleeveEnabled(true);

    playerMeta.setNotifyAboutChanges(true);
  }

  @Override
  public void updateNewViewer(@NotNull Player player) {
    PlayerInfoUpdatePacket infoAddPacket = new PlayerInfoUpdatePacket(Action.ADD_PLAYER, toEntry());
    player.sendPacket(infoAddPacket);

    super.updateNewViewer(player);

    MinecraftServer.getSchedulerManager().buildTask(() -> {
      PlayerInfoRemovePacket infoRemovePacket = new PlayerInfoRemovePacket(getUuid());
      player.sendPacket(infoRemovePacket);
    }).delay(TaskSchedule.tick(30)).schedule();
  }

  private Entry toEntry() {
    return new Entry(
        getUuid(), this.username,
        List.of(new Property("textures", this.skin.textures(), this.skin.signature())),
        false, 69, GameMode.SURVIVAL, null, null
    );
  }

  @Override
  public void updateOldViewer(@NotNull Player player) {
    PlayerInfoRemovePacket infoRemovePacket = new PlayerInfoRemovePacket(getUuid());
    player.sendPacket(infoRemovePacket);

    super.updateOldViewer(player);
  }
}
