package org.altarplanner.core.io;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.Optional;

public class XML {

    private static XStream makeXStream(Class... xStreamAnnotatedClasses) {
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.processAnnotations(xStreamAnnotatedClasses);
        XStream.setupDefaultSecurity(xStream);
        xStream.addPermission(new AnyTypePermission());
        return xStream;
    }

    public static void write(Object obj, File outputFile) throws FileNotFoundException {
        XStream xStream = makeXStream(obj.getClass());
        Optional.ofNullable(outputFile.getParentFile()).ifPresent(File::mkdirs);
        Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
        xStream.toXML(obj, writer);
    }

    public static <T> T read(File inputFile, Class<T> instanceOf, Class... otherXStreamAnnotatedClasses) throws FileNotFoundException {
        XStream xStream = makeXStream(ArrayUtils.add(otherXStreamAnnotatedClasses, instanceOf));
        Reader reader = new InputStreamReader(new FileInputStream(inputFile));
        Object unknownObject = xStream.fromXML(reader);
        if (instanceOf.isInstance(unknownObject)) {
            @SuppressWarnings("unchecked")
            T cast = (T) unknownObject;
            return cast;
        } else throw new ClassCastException(unknownObject.getClass() + " cannot be cast to " + instanceOf);
    }

}
