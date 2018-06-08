package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.io.XML;
import org.altarplanner.core.xml.jaxb.util.PairRequestXmlAdapter;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "regularMasses", "servers", "pairs"})
public class Config implements Serializable {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");
    private static final String pathname = "config.xml";

    private List<ServiceType> serviceTypes = new ArrayList<>();
    private List<RegularMass> regularMasses = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    private List<PairRequest> pairs = new ArrayList<>();

    public static Config load() {
        final File defaultFile = new File(pathname);
        try {
            return XML.read(defaultFile, Config.class);
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(Config.class).info("File not found: \"{}\". Creating new config.", defaultFile);
            return new Config();
        }
    }

    public Config() {
    }

    public void save() throws FileNotFoundException {
        XML.write(this, new File(pathname));
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
                .filter(pairRequest -> pairRequest.getKey() == server)
                .map(PairRequest::getValue)
                .collect(Collectors.toList());
    }

    public void addPair(PairRequest pair) {
        pairs.add(pair);
        pairs.add(new PairRequest(pair.getValue(), pair.getKey()));
    }

    public void removePair(PairRequest pair) {
        pairs.remove(pair);
        pairs.remove(new PairRequest(pair.getValue(), pair.getKey()));
    }

    public void removeAllPairsWith(Server server) {
        pairs.removeIf(pairRequest -> pairRequest.getKey() == server || pairRequest.getValue() == server);
    }

    @XmlElementWrapper(name = "serviceTypes")
    @XmlElement(name = "serviceType")
    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @XmlElementWrapper(name = "regularMasses")
    @XmlElement(name = "regularMass")
    public List<RegularMass> getRegularMasses() {
        return regularMasses;
    }

    public void setRegularMasses(List<RegularMass> regularMasses) {
        this.regularMasses = regularMasses;
    }

    @XmlElementWrapper(name = "servers")
    @XmlElement(name = "server")
    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    @XmlElementWrapper(name = "pairs")
    @XmlElement(name = "pair")
    @XmlJavaTypeAdapter(PairRequestXmlAdapter.class)
    public List<PairRequest> getPairs() {
        this.pairs.sort(Comparator.comparing(PairRequest::getKey, Server.getDescComparator()));
        return pairs;
    }

    public void setPairs(List<PairRequest> pairs) {
        this.pairs = pairs;
    }

}
