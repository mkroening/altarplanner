package org.altarplanner.core.planning.domain.request;

import java.util.AbstractMap;
import org.altarplanner.core.planning.domain.planning.Server;

public class PairRequest extends AbstractMap.SimpleImmutableEntry<Server, Server> {
  public PairRequest(Server key, Server value) {
    super(key, value);
  }
}
