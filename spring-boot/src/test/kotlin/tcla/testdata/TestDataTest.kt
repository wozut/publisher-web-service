package tcla.testdata

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import javax.sql.DataSource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestDataTest {

    @Test
    @Sql("/db/test-data/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun testDataPopulation() {
        /*
        Test is empty because we are only checking that the @Sql annotation
        populates the database without throwing any error.
         */
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp(@Autowired dataSource: DataSource) {
            dataSource.dropTables()
        }
    }
}
