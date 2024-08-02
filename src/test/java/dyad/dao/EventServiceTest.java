package dyad.dao;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import dyad.Dyad;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Dyad.class)
@DirtiesContext
@ActiveProfiles("test")
@Disabled
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

}
