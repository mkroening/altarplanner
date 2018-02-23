package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.persistence.XStreamFileIO;

import java.io.File;
import java.io.FileNotFoundException;
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
                .flatMap(date -> Optional.ofNullable(dayMassMap.get(date.getDayOfWeek()))
                        .map(masses -> masses.parallelStream()
                                .map(regularMass -> new DiscreteMass(regularMass, date)))
                        .orElse(null));
    }

    public void save(File file) throws FileNotFoundException {
        XStreamFileIO.write(this, file);
    }

    public static Config load(File file) throws FileNotFoundException {
        return (Config) XStreamFileIO.read(file);
    }

}
