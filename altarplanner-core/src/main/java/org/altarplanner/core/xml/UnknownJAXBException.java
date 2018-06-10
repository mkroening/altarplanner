package org.altarplanner.core.xml;

import javax.xml.bind.JAXBException;

public class UnknownJAXBException extends JAXBException {

    public UnknownJAXBException(Throwable exception) {
        super(exception);
    }

}
