package space.chunks.gamecup.dgr.map.object.setup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.AbstractMapObject;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.config.MapObjectConfigEntry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradableCondition;
import space.chunks.gamecup.dgr.map.object.upgradable.UpgradableConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * @author Nico_ND1
 */
@Log4j2
public class MapObjectDefaultSetupLoader implements MapObjectDefaultSetup {
  private final MapObjectTypeRegistry registry;
  private final java.util.Map<String, MapObject> configProviders = new HashMap<>();

  @Inject
  public MapObjectDefaultSetupLoader(MapObjectTypeRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void createDefaultObjects(@NotNull Map map) {
    File folder = new File("template/config/default_map_objects");
    log.info("Loading default map objects from {}", folder.getPath());

    File[] files = folder.listFiles();
    if (files == null) {
      log.warn("No default map objects folder found");
      return;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    for (File file : files) {
      if (!file.getName().endsWith(".json")) {
        continue;
      }

      String typeName = getTypeName(file);
      Class<? extends MapObjectConfigEntry> configClass = getConfigClass(typeName);

      int createdObjects = 0;
      try {
        List<MapObjectConfigEntry> entries = objectMapper.readerForListOf(configClass).readValue(file);
        for (MapObjectConfigEntry entry : entries) {
          MapObject mapObject = this.registry.create(typeName, entry);
          queueMapObject(map, typeName, mapObject, entry);
          createdObjects++;
        }
      } catch (IOException e) {
        log.error("Failed to load default map object from {}", file.getPath(), e);
      }

      log.info("Loaded {} default map objects of type {}", createdObjects, typeName);
    }
  }

  private void queueMapObject(@NotNull Map map, @NotNull String type, @NotNull MapObject mapObject, @NotNull MapObjectConfigEntry configEntry) {
    if (configEntry instanceof UpgradableConfig upgradableConfig) {
      Integer minLevel = upgradableConfig.minLevel();

      if (minLevel != null) {
        map.queueMapObjectRegister(mapObject, new UpgradableCondition(map, type, minLevel));
      } else {
        map.queueMapObjectRegister(mapObject);
      }
    } else {
      map.queueMapObjectRegister(mapObject);
    }
  }

  private @NotNull String getTypeName(@NotNull File file) {
    String[] fileNameSplit = file.getName().split("\\.");
    return fileNameSplit[0];
  }

  private @NotNull Class<? extends MapObjectConfigEntry> getConfigClass(@NotNull String typeName) {
    MapObject mapObject = this.configProviders.computeIfAbsent(typeName, this.registry::create);
    if (mapObject instanceof AbstractMapObject<? extends MapObjectConfigEntry> configProvider) {
      return configProvider.configClass();
    }
    throw new IllegalArgumentException("Unknown type: "+typeName);
  }
}
