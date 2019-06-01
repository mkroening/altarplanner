package org.altarplanner.core.persistence.jaxb.domain.planning;

import org.altarplanner.core.planning.domain.planning.Server;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerXmlAdapter extends XmlAdapter<ServerBean, Server> {
  private final Map<Server, Server> servers = new HashMap<>();

  @Override
  public Server unmarshal(ServerBean serverBean) {
    final var server = new Server();
    server.setSurname(serverBean.getSurname());
    server.setForename(serverBean.getForename());
    server.setYear(serverBean.getYear());
    server.setAbsences(serverBean.getAbsences());
    server.setDateTimeOnWishes(serverBean.getDateTimeOnWishes());
    server.setWeeklyAbsences(serverBean.getWeeklyAbsences());
    server.setInabilities(serverBean.getInabilities());
    servers.putIfAbsent(server, server);
    return servers.getOrDefault(server, server);
  }

  @Override
  public ServerBean marshal(Server server) {
    return Optional.ofNullable(server)
        .map(
            nonNullServer -> {
              final var serverBean = new ServerBean();
              serverBean.setSurname(nonNullServer.getSurname());
              serverBean.setForename(nonNullServer.getForename());
              serverBean.setYear(nonNullServer.getYear());
              serverBean.setXmlID(
                  nonNullServer.getSurname()
                      + "_"
                      + nonNullServer.getForename()
                      + "-"
                      + nonNullServer.getYear());
              serverBean.setAbsences(nonNullServer.getAbsences());
              serverBean.setDateTimeOnWishes(nonNullServer.getDateTimeOnWishes());
              serverBean.setWeeklyAbsences(nonNullServer.getWeeklyAbsences());
              serverBean.setInabilities(nonNullServer.getInabilities());
              return serverBean;
            })
        .orElse(null);
  }
}
