package org.altarplanner.core.planning.domain.state;

import java.time.LocalDate;
import java.util.List;

public interface FeastDayAware {

  List<LocalDate> getFeastDays();

  void setFeastDays(List<LocalDate> feastDays);
}
