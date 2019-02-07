package org.lrth.samples.spring.sequences;

// WARNING: This is just for exemplary purposes, it is not recommended to have dynamic tests when app's build
// is shared with other teams

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lrth.samples.spring.sequences.entities.GeneratedSequencesEntity;
import org.lrth.samples.spring.sequences.entities.SequenceEntity;
import org.lrth.samples.spring.sequences.repositories.GeneratedSequencesRepository;
import org.lrth.samples.spring.sequences.repositories.SequenceRepository;
import org.lrth.samples.spring.sequences.services.MyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.*;

//@RunWith( SpringRunner.class )
@RunWith(ConcurrentTestRunner.class)
@EnableRetry
@DataJpaTest
// Load embedded Postgresql:
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
})
@ComponentScan("org.lrth.samples.spring.sequences")
// Flyway:
@FlywayTest
public class DemoApplicationStressTest {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplicationStressTest.class);

    private static final String TEST_ENTITY_ID = "A32";

    @Autowired
    private MyService service;

    @Autowired
    private SequenceRepository repo;

    @Autowired
    private GeneratedSequencesRepository repoDest;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private final AtomicInteger threadsCounter = new AtomicInteger(0);

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Test
    @ThreadCount(100)
    public void minimalSequenceHappyPath() {
        final int curThread = threadsCounter.incrementAndGet();
        logger.info("Running thread [" + curThread + "]");

        Long curSequenceVal = service.nextSeqVal(TEST_ENTITY_ID);

        logger.info("       thread [" + curThread + "]: curSequenceVal=" + curSequenceVal);

        GeneratedSequencesEntity entityDest = new GeneratedSequencesEntity(TEST_ENTITY_ID, curSequenceVal);

        repoDest.save(entityDest);

        logger.info("       thread [" + curThread + "]: SAVED SUCCESSFULLY curSequenceVal=" + curSequenceVal);

        // the database itself, rather than java code, will let us know if unique key was violated
        Assert.assertThat(curSequenceVal, not(0l));
    }

    /*
    Next works as a standalone app (eg. CommandLineRunner), not as a unit test, but it works just fine:

    @Override
    public void run(String... args) {
        SequenceEntity entity = repo.findById("A32").get();
        TestTransaction.end();

        List<Callable<Long>> tasks = new ArrayList<>();

        TestTransaction.end();

        final long numberOfThreads = 100;
        for (int i = 1; i <= numberOfThreads; i++) {
            final int curI = i;
            tasks.add(() -> {
                // it might be worth to do some tests with variant threads, but for a definitive test it's discouraged dynamic unrepeatable values
                // long numberOfUpdatesPerThread = ThreadLocalRandom.current().nextInt(1, 15);
                long numberOfUpdatesPerThread = 15;
                logger.info("Number of updates per thread: " + numberOfUpdatesPerThread);

                // it could be worth some sleeps to check different loads, but not for a definitive automated test
                // long sleep = ThreadLocalRandom.current().nextInt(1, 101);
                // logger.info("Thread [" + curI + "] sleeping " + sleep + " milliseconds : " + entity.getSequenceId() + ", " + entity.getCurrentCount());
                // TimeUnit.MILLISECONDS.sleep(sleep);

                long currentCount = 0;
                for ( int attempt = 1; attempt <= numberOfUpdatesPerThread; attempt++ ) {
                    logger.info("Thread [" + curI + ", " + attempt + "] genSec " + entity.getSequenceId() + ", " + entity.getCurrentValue());
                    // currentCount = repo.sequenceNextVal(entity.getSequenceId());
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    EntityTransaction transaction = entityManager.getTransaction();
                    transaction.begin();
                    currentCount = service.nextSeqVal(entity.getSequenceId());
                    transaction.commit();
                    logger.info("Thread [" + curI + ", " + attempt + "] got " + entity.getSequenceId() + ", " + entity.getCurrentValue() + " RESULT: " + currentCount);

                    GeneratedSequencesEntity entityDest = new GeneratedSequencesEntity(entity.getSequenceId(), currentCount);

                    try {
                        repoDest.save(entityDest);
                    } catch(Exception e) {
                        logger.info("Thread [" + curI + ", " + attempt + "] persisted failure " + entity.getSequenceId() + ", " + entity.getCurrentValue() + " CURRENT: " + currentCount + " ERROR: " + e.getMessage());
                        // this should never happen
                        throw e;
                    }
                }

                return currentCount;
            });
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            List<Future<Long>> results = executor.invokeAll(tasks);
            logger.info("RESULTS: " + results.size());

            for(Future<Long> result : results) {
                if (result != null) {
                    logger.info("  result: [" + result.get() + "]");
                } else {
                    logger.info("  result: [null]");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // this should never happen
            e.printStackTrace();
            throw e;
        }

        // no exception should have occurred on any thread
    }
    */
}
