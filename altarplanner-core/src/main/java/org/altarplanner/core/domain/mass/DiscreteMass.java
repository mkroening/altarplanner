package org.altarplanner.core.domain.mass;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DiscreteMass extends EditableMass {

    @Getter @Setter private LocalDate date;

    public DiscreteMass() {
        super();
        this.date = LocalDate.now().plusMonths(1);
    }

    public DiscreteMass(EditableMass editableMass, LocalDate date) {
        super(editableMass);
        this.date = date;
    }

}
