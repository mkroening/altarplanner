package org.altarplanner.core.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.altarplanner.core.domain.mass.RegularMass;

import java.util.*;

@NoArgsConstructor
public class Config {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.altarplanner.core.locale.locale");

    @Getter @Setter private List<ServiceType> serviceTypes = new ArrayList<>();
    @Getter @Setter private List<RegularMass> regularMasses = new ArrayList<>();
    @Getter @Setter private List<Server> servers = new ArrayList<>();

}
