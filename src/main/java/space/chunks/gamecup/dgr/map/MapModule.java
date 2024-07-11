package space.chunks.gamecup.dgr.map;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import space.chunks.gamecup.dgr.map.object.MapObject;
import space.chunks.gamecup.dgr.map.object.impl.TestMapObject;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectRegistryImpl;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistry;
import space.chunks.gamecup.dgr.map.object.registry.MapObjectTypeRegistryImpl;


/**
 * @author Nico_ND1
 */
public final class MapModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Map.class).to(MapImpl.class);
    bind(MapObjectRegistry.class).to(MapObjectRegistryImpl.class);
    bind(MapObjectTypeRegistry.class).to(MapObjectTypeRegistryImpl.class).asEagerSingleton();

    MapBinder<String, MapObject> mapObjectTypeBinder = MapBinder.newMapBinder(binder(), String.class, MapObject.class);
    mapObjectTypeBinder.addBinding("test").to(TestMapObject.class);
  }
}
