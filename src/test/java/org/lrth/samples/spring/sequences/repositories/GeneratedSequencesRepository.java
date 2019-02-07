package org.lrth.samples.spring.sequences.repositories;


import org.lrth.samples.spring.sequences.entities.GeneratedSequencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneratedSequencesRepository extends JpaRepository<GeneratedSequencesEntity, String> {
}
