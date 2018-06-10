package org.altarplanner.core.xml.jaxb.util;

import org.altarplanner.core.domain.Server;
import org.altarplanner.core.domain.request.PairRequest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PairRequestXmlAdapter extends XmlAdapter<PairRequestXmlAdapter.AdaptedPairRequest, PairRequest> {
    public static class AdaptedPairRequest {
        @XmlIDREF
        @XmlAttribute
        public Server server;
        @XmlIDREF
        @XmlAttribute
        public Server pairedWith;
    }

    @Override
    public PairRequest unmarshal(AdaptedPairRequest v) throws Exception {
        return new PairRequest(v.server, v.pairedWith);
    }

    @Override
    public AdaptedPairRequest marshal(PairRequest v) throws Exception {
        AdaptedPairRequest adaptedPairRequest = new AdaptedPairRequest();
        adaptedPairRequest.server = v.getKey();
        adaptedPairRequest.pairedWith = v.getValue();
        return adaptedPairRequest;
    }
}
