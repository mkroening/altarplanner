package org.altarplanner.core.xml;

import javax.xml.bind.UnmarshalException;

public class UnexpectedElementException extends UnmarshalException {

    public UnexpectedElementException(Throwable exception) {
        super(exception);
    }

}
