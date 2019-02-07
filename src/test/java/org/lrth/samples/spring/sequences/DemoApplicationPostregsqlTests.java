package org.lrth.samples.spring.sequences;

import static org.hamcrest.CoreMatchers.*;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lrth.samples.spring.sequences.services.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@RunWith( SpringRunner.class )
@DataJpaTest
// Override dataSource bean with embedded Postgresql's
@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
})
@ComponentScan("org.lrth.samples.spring.sequences")
// Flyway:
@FlywayTest
public class DemoApplicationPostregsqlTests {

    private static final String TEST_ENTITY_ID = "Y99";

    @Autowired
    private MyService service;

    @Test
    public void minimalSequenceHappyPath() {
        Long seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(1l));

        seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(2l));

        seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(3l));
    }

}
