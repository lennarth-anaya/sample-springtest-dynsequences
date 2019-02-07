package org.lrth.samples.spring.sequences.services;

import org.lrth.samples.spring.sequences.repositories.SequenceRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private SequenceRepository repo;

    public MyService(SequenceRepository repo) {
        this.repo = repo;
    }

    // We could also try retrying with Pessimistic Locks instead (coordinated with Repository's findById):
    // @Retryable(value = {PessimisticLockingFailureException.class})
    @Retryable(
            value = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 15,
            backoff = @Backoff(delay = 2500)
    )
    public Long nextSeqVal(String sequenceId) {
        return repo.findById(sequenceId).get().getCurrentValue();
    }

}
