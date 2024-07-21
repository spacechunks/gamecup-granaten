package space.chunks.gamecup.dgr.launcher.profiler;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.time.Duration;


/**
 * @author Nico_ND1
 */
public class ProfilerModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ProfilerRepository.class).to(ProfilerRepositoryImpl.class);
    bind(ApplicationProfilerCommand.class);
    bind(Profiler.class).annotatedWith(Names.named("minecraftServer")).toInstance(new MinecraftServerProfiler(Duration.ofMinutes(5)));
  }
}
