package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.util.LocalDateInterval;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.altarplanner.core.xml.jaxb.util.PairRequestXmlAdapter;
import org.slf4j.Logger;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlRootElement
@XmlType(propOrder = {"serviceTypes", "regularMasses", "servers", "pairs"})
public class Config implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");
    private static final String pathname = "config.xml";

    private List<ServiceType> serviceTypes = new ArrayList<>();
    private List<RegularMass> regularMasses = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    private List<PairRequest> pairs = new ArrayList<>();

    public static Config load() throws UnknownJAXBException {
        File defaultFile = new File(pathname);
        try {
            return JaxbIO.unmarshal(defaultFile, Config.class);
        } catch (FileNotFoundException e) {
            LOGGER.info(e.toString());
            LOGGER.info("Creating new config.");
            return new Config();
        } catch (UnexpectedElementException e) {
            File corruptFile = new File("config_corrupt_" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".xml");
            LOGGER.error("Moving \"{}\" to \"{}\"", defaultFile, corruptFile);
            defaultFile.renameTo(corruptFile);
            LOGGER.info("Creating new config.");
            return new Config();
        }
    }

    public Config() {
    }

    public void save() throws UnknownJAXBException {
        this.pairs.sort(Comparator.comparing(PairRequest::getKey, Server.getDescComparator()));
        JaxbIO.marshal(this, new File(pathname));
    }

    public Stream<DiscreteMass> getDiscreteMassParallelStreamWithin(LocalDateInterval dateInterval) {
        Map<DayOfWeek, List<RegularMass>> dayMassMap = regularMasses.parallelStream()
                .collect(Collectors.groupingBy(RegularMass::getDay));

        return dateInterval.stream()
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
        return pairs;
    }

    public void setPairs(List<PairRequest> pairs) {
        this.pairs = pairs;
    }

}
