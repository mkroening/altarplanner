package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class Config {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");

    @Getter @Setter private List<ServiceType> serviceTypes = new ArrayList<>();
    @Getter @Setter private List<RegularMass> regularMasses = new ArrayList<>();
    @Getter @Setter private List<Server> servers = new ArrayList<>();

    public Stream<DiscreteMass> getDiscreteMassParallelStreamWithin(DateSpan dateSpan) {
        Map<DayOfWeek, List<RegularMass>> dayMassMap = regularMasses.parallelStream()
                .collect(Collectors.groupingBy(RegularMass::getDay));

        return dateSpan.getDateParallelStream()
                .flatMap(date -> dayMassMap.get(date.getDayOfWeek()).parallelStream()
                        .map(regularMass -> new DiscreteMass(regularMass, date)));
    }

}
