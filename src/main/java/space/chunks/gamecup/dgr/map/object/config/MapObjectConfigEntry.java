package space.chunks.gamecup.dgr.map.object.config;

import org.jetbrains.annotations.NotNull;


/**
 * @author Nico_ND1
 */
public interface MapObjectConfigEntry {
  @NotNull
  String name();

  MapObjectConfigEntry EMPTY = new MapObjectConfigEntryDefault("EMPTY");
}
