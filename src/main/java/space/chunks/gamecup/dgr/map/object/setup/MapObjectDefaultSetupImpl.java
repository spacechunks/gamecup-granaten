package space.chunks.gamecup.dgr.map.object.setup;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import space.chunks.gamecup.dgr.map.Map;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.procedure.securitycheck.SecurityCheckConfig;


/**
 * @author Nico_ND1
 */
public class MapObjectDefaultSetupImpl implements MapObjectDefaultSetup {
  private final MapObjectDefaultSetupConfig config;
  private final MapObjectTypeRegistry registry;

  @Inject
  public MapObjectDefaultSetupImpl(@NotNull MapObjectDefaultSetupConfig config, @NotNull MapObjectTypeRegistry registry) {
    this.config = config;
    this.registry = registry;
  }

  @Override
  public void createDefaultObjects(@NotNull Map map) {
    createSecurityChecks(map);
    createMarketing(map);
  }

  private void createMarketing(Map map) {
    MapObject marketing = this.registry.create("marketing", this.config.marketing());
    map.queueMapObjectRegister(marketing);
  }

  private void createSecurityChecks(@NotNull Map map) {
    for (SecurityCheckConfig securityCheckConfig : this.config.securityChecks()) {
      MapObject securityCheck = this.registry.create("security_check", securityCheckConfig);
      map.queueMapObjectRegister(securityCheck);
    }
  }
}
