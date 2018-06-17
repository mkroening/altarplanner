package org.altarplanner.core.domain.util;

import org.altarplanner.core.domain.*;
import org.altarplanner.core.domain.mass.DiscreteMass;
import org.altarplanner.core.domain.mass.RegularMass;
import org.altarplanner.core.domain.request.PairRequest;
import org.altarplanner.core.util.LocalDateInterval;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BigDomainGenerator {

    private static final Random RANDOM = new Random(0);
    private static final LocalDate TODAY = LocalDate.of(2018, 1, 1);
    private static final LocalDateInterval PLANNING_WINDOW;
    static {
        LocalDate nextMonday = TODAY.plusDays(DayOfWeek.MONDAY.getValue() - TODAY.getDayOfWeek().getValue());
        PLANNING_WINDOW = LocalDateInterval.of(nextMonday, nextMonday.plusWeeks(4).minusDays(1));
    }

    private static LocalDate getNextDayOfWeek(LocalDate starting, DayOfWeek dayOfWeek) {
        return starting.plusDays(dayOfWeek.getValue() - starting.getDayOfWeek().getValue());
    }

    private static Server genServer(Integer value) {
        Server server = new Server();
        server.setSurname("");
        server.setForename("");
        server.setYear(TODAY.getYear() - RANDOM.nextInt(10));

        if (RANDOM.nextFloat() < 0.4)
            server.getWeeklyAbsences().add(DayOfWeek.MONDAY);
        if (RANDOM.nextFloat() < 0.3)
            server.getWeeklyAbsences().add(DayOfWeek.TUESDAY);
        if (RANDOM.nextFloat() < 0.4)
            server.getWeeklyAbsences().add(DayOfWeek.WEDNESDAY);
        if (RANDOM.nextFloat() < 0.45)
            server.getWeeklyAbsences().add(DayOfWeek.THURSDAY);
        if (RANDOM.nextFloat() < 0.35)
            server.getWeeklyAbsences().add(DayOfWeek.FRIDAY);
        if (RANDOM.nextFloat() < 0.3)
            server.getWeeklyAbsences().add(DayOfWeek.SATURDAY);
        if (RANDOM.nextFloat() < 0.1)
            server.getWeeklyAbsences().add(DayOfWeek.SUNDAY);

        if (RANDOM.nextFloat() < 0.2)
            server.getAbsences().add(LocalDateInterval.of(TODAY, TODAY.plusWeeks(2)));

        if (RANDOM.nextFloat() < 0.05)
            server.getDateTimeOnWishes().add(LocalDateTime.of(getNextDayOfWeek(PLANNING_WINDOW.getStart(), DayOfWeek.SUNDAY).plusWeeks(RANDOM.nextInt(4)), LocalTime.of(11,0)));

        server.setSurname(Integer.toHexString(RANDOM.nextInt()));
        server.setForename(Integer.toHexString(RANDOM.nextInt()));

        return server;
    }

    public static Config genConfig() {
        Config config = new Config();

        ServiceType altar1 = Builder.buildServiceType("Altar", TODAY.getYear() - 2, TODAY.getYear() - 4);
        ServiceType altar2 = Builder.buildServiceType("Altar", TODAY.getYear() - 4, TODAY.getYear() - 6);
        ServiceType altar3 = Builder.buildServiceType("Altar", TODAY.getYear() - 6,  TODAY.getYear() - 18);
        ServiceType flambeau = Builder.buildServiceType("Flambeau", TODAY.getYear(), TODAY.getYear() - 2);
        ServiceType lector = Builder.buildServiceType("Lector", TODAY.getYear() - 6, TODAY.getYear() - 18);
        ServiceType incense = Builder.buildServiceType("Incense", TODAY.getYear() - 6, TODAY.getYear() - 18);
        ServiceType cross = Builder.buildServiceType("Cross", TODAY.getYear() - 6, TODAY.getYear() - 18);
        config.setServiceTypes(List.of(altar1, altar2, altar3, flambeau, lector, incense, cross));

        RegularMass mon19 = Builder.buildRegularMass(DayOfWeek.MONDAY, LocalTime.of(19, 0), "Chapel", "Holy Mass",
                Map.of(altar1, 2, altar2, 2));
        RegularMass thu18 = Builder.buildRegularMass(DayOfWeek.THURSDAY, LocalTime.of(18, 0), "Chapel", "Holy Mass",
                Map.of(altar1, 2, altar2, 2));
        RegularMass sat17 = Builder.buildRegularMass(DayOfWeek.SATURDAY, LocalTime.of(17, 0), "Church", "Saturday Evening Mass",
                Map.of(altar1, 2, altar2, 2, altar3, 2));
        RegularMass sun08 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(8, 0), "Church", "Holy Mass",
                Map.of(altar1, 2, altar2, 2));
        RegularMass sun09 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(9, 45), "Chapel", "Holy Mass",
                Map.of(altar1, 2, altar2, 2));
        RegularMass sun11 = Builder.buildRegularMass(DayOfWeek.SUNDAY, LocalTime.of(11, 0), "Church", "High Mass",
                Map.of(altar2, 2, altar3, 2, flambeau, 10, lector, 1, incense, 2, cross, 1));
        config.setRegularMasses(List.of(mon19, thu18, sat17, sun08, sun09, sun11));

        List<Server> servers = IntStream.range(0, 100).mapToObj(BigDomainGenerator::genServer).collect(Collectors.toList());

        servers.forEach(server -> {
            if (RANDOM.nextFloat() < .4)
                server.getInabilities().add(config.getServiceTypes().get(RANDOM.nextInt(7)));

            if (RANDOM.nextFloat() < .2)
                config.addPair(new PairRequest(server, servers.get(RANDOM.nextInt(servers.size()))));
        });

        config.setServers(servers);

        return config;
    }

    public static Schedule genSchedule() {
        Config config = genConfig();
        List<DiscreteMass> masses = config.getDiscreteMassParallelStreamWithin(LocalDateInterval.of(TODAY, TODAY.plusMonths(1))).collect(Collectors.toList());
        return new Schedule(null, masses, config);
    }

}
