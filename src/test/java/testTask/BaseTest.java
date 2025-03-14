package testTask;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    static void beforeAll() {
    }

    @AfterAll
    static void afterAll() {
    }


    //We can add common methods here (e.g. setup, teardown, etc.)
    //didn't add it because in real life it would be more complex and I would put all these tests in one class
}
