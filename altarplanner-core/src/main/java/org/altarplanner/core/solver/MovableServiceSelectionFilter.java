package org.altarplanner.core.solver;

import org.altarplanner.core.domain.Schedule;
import org.altarplanner.core.domain.Service;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDate;

public class MovableServiceSelectionFilter implements SelectionFilter<Schedule, Service> {

    @Override
    public boolean accept(ScoreDirector<Schedule> scoreDirector, Service selection) {
        Schedule schedule = scoreDirector.getWorkingSolution();
        LocalDate serviceDate = selection.getMass().getDate();
        return serviceDate.compareTo(schedule.getPlanningWindow().getStart()) >= 0;
    }

}
