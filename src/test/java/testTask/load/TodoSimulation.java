package testTask.load;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import testTask.api.steps.TodoSteps;
import io.gatling.javaapi.core.*;

import java.time.Duration;

import static common.constants.CommonConstants.BASE_URL;
import static common.constants.HTTPStatus.CREATED;
import static common.constants.TodoConstants.TODO_PATH;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static java.lang.Integer.parseInt;

public class TodoSimulation extends Simulation {

    private final TodoSteps todoSteps = new TodoSteps();
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .contentTypeHeader("application/json");

    private final ScenarioBuilder createTodo = scenario("Create TODOs")
            .exec(session -> {
                var todo = todoSteps.getRandomTodo();
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonBody;
                try {
                    jsonBody = objectMapper.writeValueAsString(todo);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                return session.set("jsonBody", jsonBody);
            })
            .exec(http("POST /todos")
                    .post(TODO_PATH)
                    .body(StringBody("#{jsonBody}"))
                    .asJson()
                    .check(status().is(parseInt(CREATED))).check(responseTimeInMillis().lt(2000))
            );

    {
        setUp(
                createTodo.injectOpen(
                        rampUsers(100).during(Duration.ofSeconds(10)),  // 100 users per 10 seconds
                        constantUsersPerSec(50).during(Duration.ofSeconds(30)) // 50 requests per sec during 30 sec
                )
        ).protocols(httpProtocol);
    }
}