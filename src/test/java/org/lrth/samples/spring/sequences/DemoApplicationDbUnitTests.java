package org.lrth.samples.spring.sequences;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
// import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lrth.samples.spring.sequences.services.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;

@RunWith( SpringRunner.class )
@DataJpaTest //(properties = "h2.datasource")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        // DBUnit:
        DbUnitTestExecutionListener.class
})
// DBUnit test data:
@DatabaseSetup( "classpath:test-datasets.xml" )
// DBUnit cannot work with embedded Postgresql
@ComponentScan("org.lrth.samples.spring.sequences")
public class DemoApplicationDbUnitTests {
    private static final String TEST_ENTITY_ID = "W10";

    @Autowired
    private MyService service;

    @Test
    @Commit // <-- TestTransaction.end() will roll transaction back by default if @Commit is not present
    public void minimalSequenceHappyPath() {
        // it's weird we have to do next so H2's new transactions work well
        TestTransaction.end();

        Long seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(1l));

        seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(2l));

        seq = service.nextSeqVal(TEST_ENTITY_ID);
        Assert.assertThat(seq, is(3l));
    }
}
