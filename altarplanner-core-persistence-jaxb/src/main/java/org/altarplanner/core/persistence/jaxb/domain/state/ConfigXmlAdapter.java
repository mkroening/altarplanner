package org.altarplanner.core.persistence.jaxb.domain.state;

import org.altarplanner.core.planning.domain.state.Config;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ConfigXmlAdapter extends XmlAdapter<ConfigBean, Config> {
  @Override
  public Config unmarshal(ConfigBean configBean) {
    final var config = new Config();
    config.setServiceTypes(configBean.getServiceTypes());
    config.setServers(configBean.getServers());
    config.setPairs(configBean.getPairs());
    config.setRegularMasses(configBean.getRegularMasses());
    return config;
  }

  @Override
  public ConfigBean marshal(Config config) {
    final var configBean = new ConfigBean();
    configBean.setServiceTypes(config.getServiceTypes());
    configBean.setServers(config.getServers());
    configBean.setPairs(config.getPairs());
    configBean.setRegularMasses(config.getRegularMasses());
    return configBean;
  }
}
