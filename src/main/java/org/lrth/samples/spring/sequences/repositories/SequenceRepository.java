package org.lrth.samples.spring.sequences.repositories;

import org.lrth.samples.spring.sequences.entities.SequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Optional;

public interface SequenceRepository
        extends JpaRepository<SequenceEntity, String> {

    // @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Optional<SequenceEntity> findById(String sequenceId);

}
