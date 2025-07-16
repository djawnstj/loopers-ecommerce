package com.loopers.support

import com.loopers.utils.DatabaseCleanUp
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.TestConstructor

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class E2ETestSupport {

    @Autowired
    protected lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var databaseCleanUp: DatabaseCleanUp

    @AfterEach
    fun cleanUp() {
        databaseCleanUp.truncateAllTables()
    }
}
