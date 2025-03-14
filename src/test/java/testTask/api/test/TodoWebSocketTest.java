package testTask.api.test;

import common.data.Todo;
import helpers.WebSocketHelper;
import io.qameta.allure.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import testTask.BaseTest;
import testTask.api.steps.TodoSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static helpers.Generator.randInt;
import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Tag("regress")
@TestInstance(PER_CLASS)
@Isolated
public class TodoWebSocketTest extends BaseTest {

    private final TodoSteps todoSteps = new TodoSteps();
    private WebSocketHelper wsClient;

    @BeforeAll
    void setupWebSocket() {
        wsClient = todoSteps.connectWS();
    }

    @Test
    @Description("Verify WebSocket notification when creating a new todo")
    void testReceiveNotificationOnNewTodo() {
        var todo = todoSteps.createTodo();

        await().atMost(5, SECONDS)
                .until(() -> !wsClient.getMessages().isEmpty());

        assertThat(wsClient.getMessages().getFirst())
                .as("WebSocket message should have correct format")
                .matches("\\{\"data\":\\{.*},\"type\":\"new_todo\"}");
        assertThat(todoSteps.convertMessageToTodo(wsClient.getMessages()))
                .as("New todo should be received via WebSocket")
                .containsExactlyInAnyOrder(todo);
    }

    @Test
    @Description("Verify WebSocket connection status after multiple operations")
    void testMaintainConnectionAfterOperations() {
        var todo = todoSteps.createTodo(Todo.builder().id(randInt(0, MAX_VALUE)).text("").completed(false).build());

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> !wsClient.getMessages().isEmpty());

        todoSteps.deleteTodo(todo.getId());
        var secondTodo = todoSteps.createTodo();

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> wsClient.getMessages().size() == 2);

        assertThat(todoSteps.convertMessageToTodo(wsClient.getMessages()))
                .as("WebSocket should receive messages after multiple operations")
                .containsExactlyInAnyOrder(todo, secondTodo);
    }

    @Test
    @Description("Verify WebSocket notification with Unicode characters")
    void testUnicodeNotification() {
        var todo = todoSteps.createTodo(Todo.builder()
                .id(randInt(0, MAX_VALUE))
                .text("Unicode ♠♣♥♦ emoji 😀🎉")
                .completed(true)
                .build());

        await().atMost(5, SECONDS)
                .until(() -> !wsClient.getMessages().isEmpty());

        assertThat(todoSteps.convertMessageToTodo(wsClient.getMessages()))
                .as("WebSocket should receive messages with Unicode characters")
                .containsExactly(todo);
    }

    @Test
    @DisplayName("Verify WebSocket under concurrent todo creation")
    void testConcurrentTodoCreation() throws InterruptedException {
        int todoCount = 100;
        var latch = new CountDownLatch(todoCount);
        List<Todo> createdTodos = Collections.synchronizedList(new ArrayList<>());

        var executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < todoCount; i++) {
            executor.submit(() -> {
                try {
                    createdTodos.add(todoSteps.createTodo());
                } finally {
                    latch.countDown();
                }
            });
        }

        assertThat(latch.await(30, SECONDS)).isTrue();
        executor.shutdown();
        assertThat(executor.awaitTermination(10, SECONDS)).isTrue();

        await().atMost(10, SECONDS).until(() -> wsClient.getMessages().size() == todoCount);

        List<Todo> receivedTodos = todoSteps.convertMessageToTodo(wsClient.getMessages());
        assertThat(receivedTodos)
                .as("All created todos should be received via WebSocket")
                .containsExactlyInAnyOrderElementsOf(createdTodos);
    }

    @Test
    @Description("Verify WebSocket notifications order")
    void testNotificationsOrder() {
        var first = todoSteps.createTodo();
        var second = todoSteps.createTodo();
        var third = todoSteps.createTodo();

        await().atMost(5, SECONDS)
                .until(() -> wsClient.getMessages().size() == 3);

        assertThat(todoSteps.convertMessageToTodo(wsClient.getMessages()))
                .as("WebSocket messages should be received in the same order as todos were created")
                .containsExactly(first, second, third);
    }

    @AfterEach
    void afterEachTest() {
        wsClient.clear();
    }

    @AfterAll
    void afterAllTests() {
        todoSteps.deleteAllTodo();
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.close();
        }
    }
}
