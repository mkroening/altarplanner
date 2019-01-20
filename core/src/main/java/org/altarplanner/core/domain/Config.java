package org.altarplanner.core.domain;

import org.altarplanner.core.domain.mass.PlanningMassTemplate;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.xml.JaxbIO;
import org.altarplanner.core.xml.UnexpectedElementException;
import org.altarplanner.core.xml.UnknownJAXBException;
import org.altarplanner.core.xml.jaxb.util.PairRequestXmlAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.extra.LocalDateRange;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
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

    public static Config load() throws UnknownJAXBException, IOException {
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
            Files.move(defaultFile.toPath(), corruptFile.toPath());
            LOGGER.info("Creating new config.");
            return new Config();
        }
    }

    public Config() {
    }

    public void save() throws UnknownJAXBException {
        this.pairs.sort(Comparator.comparing(PairRequest::getKey));
        JaxbIO.marshal(this, new File(pathname));
    }

    public Stream<PlanningMassTemplate> getPlanningMassTemplateStreamFromRegularMassesIn(LocalDateRange dateRange) {
        Map<DayOfWeek, List<RegularMass>> dayMassMap = regularMasses.stream()
                .collect(Collectors.groupingBy(RegularMass::getDay));
        return dateRange.stream()
                .flatMap(date -> Optional.ofNullable(dayMassMap.get(date.getDayOfWeek())).stream()
                        .flatMap(masses -> masses.stream()
                                .map(mass -> new PlanningMassTemplate(mass, date))));
    }

    public void remove(final ServiceType serviceType) {
        regularMasses.forEach(regularMass -> regularMass.getServiceTypeCounts().remove(serviceType));
        servers.forEach(server -> server.getInabilities().remove(serviceType));
    }

    public List<Server> getPairedWith(Server server) {
        return pairs.parallelStream()
                .filter(pairRequest -> pairRequest.getKey() == server)
                .map(PairRequest::getValue)
                .collect(Collectors.toUnmodifiableList());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(serviceTypes, config.serviceTypes) &&
                Objects.equals(regularMasses, config.regularMasses) &&
                Objects.equals(servers, config.servers) &&
                Objects.equals(pairs, config.pairs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypes, regularMasses, servers, pairs);
    }

}
