package org.lrth.samples.spring.sequences.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "sequences")
public class SequenceEntity {
    @Id private String sequenceId;
    @Version private Long currentValue;

    public SequenceEntity() {
    }

    public SequenceEntity(String sequenceId, Long currentValue) {
        this.sequenceId = sequenceId;
        this.currentValue = currentValue;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }
}
