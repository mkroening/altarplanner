package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.io.XStreamFileIO;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
public class Config implements Serializable {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");
    private static final String pathname = "config.xml";

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

    public void removeFromRegularMasses(ServiceType serviceType) {
        regularMasses.parallelStream().forEach(regularMass -> regularMass.getServiceTypeCount().remove(serviceType));
    }

    public void save() throws FileNotFoundException {
        XStreamFileIO.write(this, new File(pathname));
    }

    public static Config load() {
        final File defaultFile = new File(pathname);
        try {
            Config config = (Config) XStreamFileIO.read(defaultFile, Config.class);
            config.servers = config.servers.parallelStream().map(Server::new).collect(Collectors.toList());
            return config;
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(Config.class).info("File not found: \"{}\". Creating new config.", defaultFile);
            return new Config();
        }
    }

}
