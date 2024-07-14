package space.chunks.gamecup.dgr.map.object.impl.procedure.incident;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import lombok.Getter;
import lombok.experimental.Accessors;
import space.chunks.gamecup.dgr.map.Map;


/**
 * @author Nico_ND1
 */
@Getter
@Accessors(fluent=true)
public class TroubleMakerImpl implements TroubleMaker {
  private final Map parent;

  @AssistedInject
  public TroubleMakerImpl(@Assisted Map parent) {
    this.parent = parent;
  }

  @Override
  public void makeTrouble() {
    // TODO
  }
}
