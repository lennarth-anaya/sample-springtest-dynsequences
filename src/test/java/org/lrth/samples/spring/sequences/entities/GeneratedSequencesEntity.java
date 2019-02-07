package org.lrth.samples.spring.sequences.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "generated_sequences")
public class GeneratedSequencesEntity {
    @EmbeddedId
    private Key compositeId;

    public GeneratedSequencesEntity() {}

    public GeneratedSequencesEntity(String sequenceId, Long currentValue) {
        compositeId = new Key(sequenceId, currentValue);
    }

    @Embeddable
    public static class Key implements Serializable {
        private String sequenceId;
        private Long currentValue;

        public Key() {}

        public Key(String sequenceId, Long currentValue) {
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
}
