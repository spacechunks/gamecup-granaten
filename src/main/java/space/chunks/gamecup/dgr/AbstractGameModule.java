package space.chunks.gamecup.dgr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


/**
 * @author Nico_ND1
 */
public abstract class AbstractGameModule extends AbstractModule {
  protected <T> void bindConfig(@NotNull Class<T> clazz, @NotNull File file) {
    try {
      T config = objectMapper().readerFor(clazz).readValue(file);
      bind(clazz).toInstance(config);
    } catch (IOException e) {
      throw new RuntimeException("Error binding config for "+clazz, e);
    }
  }

  private ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
