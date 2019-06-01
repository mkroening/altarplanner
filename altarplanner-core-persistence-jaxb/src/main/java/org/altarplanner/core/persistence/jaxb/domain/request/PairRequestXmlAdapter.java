package org.altarplanner.core.persistence.jaxb.domain.request;

import org.altarplanner.core.planning.domain.request.PairRequest;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PairRequestXmlAdapter extends XmlAdapter<PairRequestBean, PairRequest> {
  @Override
  public PairRequest unmarshal(PairRequestBean pairRequestBean) {
    return new PairRequest(pairRequestBean.getServer(), pairRequestBean.getPairedWith());
  }

  @Override
  public PairRequestBean marshal(PairRequest pairRequest) {
    PairRequestBean pairRequestBean = new PairRequestBean();
    pairRequestBean.setServer(pairRequest.getKey());
    pairRequestBean.setPairedWith(pairRequest.getValue());
    return pairRequestBean;
  }
}
