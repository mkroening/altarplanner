package org.altarplanner.core.domain.mass;

import org.altarplanner.core.domain.Config;

import java.util.Comparator;
import java.util.Objects;

public abstract class BaseMass implements GenericMass {

    protected static final Comparator<BaseMass> BASE_COMPARATOR = Comparator
            .comparing(BaseMass::getChurch)
            .thenComparing(BaseMass::getForm)
            .thenComparing(BaseMass::getAnnotation);

    protected String church;

    protected String form;

    protected String annotation;

    protected BaseMass() {
        this.church = Config.RESOURCE_BUNDLE.getString("mass.church");
        this.form = Config.RESOURCE_BUNDLE.getString("mass.form");
        this.annotation = Config.RESOURCE_BUNDLE.getString("mass.annotation");
    }

    protected BaseMass(BaseMass baseMass) {
        this.church = baseMass.church;
        this.form = baseMass.form;
        this.annotation = baseMass.annotation;
    }

    @Override
    public String getChurch() {
        return church;
    }

    @Override
    public void setChurch(String church) {
        this.church = church;
    }

    @Override
    public String getForm() {
        return form;
    }

    @Override
    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public String getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseMass baseMass = (BaseMass) o;
        return Objects.equals(church, baseMass.church) &&
                Objects.equals(form, baseMass.form) &&
                Objects.equals(annotation, baseMass.annotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(church, form, annotation);
    }
}
