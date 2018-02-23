package org.altarplanner.core.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.*;

public class XStreamFileIO {

    private static XStream makeXStream(Class... xStreamAnnotatedClasses) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(xStreamAnnotatedClasses);
        XStream.setupDefaultSecurity(xStream);
        xStream.addPermission(new AnyTypePermission());
        return xStream;
    }

    public static Object read(File inputFile, Class... xStreamAnnotatedClasses) throws FileNotFoundException {
        XStream xStream = makeXStream(xStreamAnnotatedClasses);
        Reader reader = new InputStreamReader(new FileInputStream(inputFile));
        return xStream.fromXML(reader);
    }

    public static void write(Object obj, File outputFile) throws FileNotFoundException {
        XStream xStream = makeXStream(obj.getClass());
        Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
        xStream.toXML(obj, writer);
    }

}
