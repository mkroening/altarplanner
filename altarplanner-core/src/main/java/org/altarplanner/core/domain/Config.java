package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.io.XML;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config implements Serializable {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");
    private static final String pathname = "config.xml";

    private List<ServiceType> serviceTypes = new ArrayList<>();
    private List<RegularMass> regularMasses = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    private List<PairRequest> pairs = new ArrayList<>();

    public Config() {
    }

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

    public List<Server> getPairedWith(Server server) {
        return pairs.parallelStream()
                .filter(pairRequest -> pairRequest.getServer() == server)
                .map(PairRequest::getPairedWith)
                .collect(Collectors.toList());
    }

    public void addPair(PairRequest pair) {
        pairs.add(pair);
        pairs.add(new PairRequest(pair.getPairedWith(), pair.getServer()));
    }

    public void removePair(PairRequest pair) {
        pairs.remove(pair);
        pairs.remove(new PairRequest(pair.getPairedWith(), pair.getServer()));
    }

    public void removeAllPairsWith(Server server) {
        pairs.removeIf(pairRequest -> pairRequest.getServer() == server || pairRequest.getPairedWith() == server);
    }

    public void save() throws FileNotFoundException {
        XML.write(this, new File(pathname));
    }

    public static Config load() {
        final File defaultFile = new File(pathname);
        try {
            return XML.read(defaultFile, Config.class);
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(Config.class).info("File not found: \"{}\". Creating new config.", defaultFile);
            return new Config();
        }
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<RegularMass> getRegularMasses() {
        return regularMasses;
    }

    public void setRegularMasses(List<RegularMass> regularMasses) {
        this.regularMasses = regularMasses;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public List<PairRequest> getPairs() {
        return pairs;
    }

    public void setPairs(List<PairRequest> pairs) {
        this.pairs = pairs;
    }

}
