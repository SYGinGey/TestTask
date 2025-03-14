package testTask.api.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.data.Todo;
import helpers.WebSocketHelper;
import helpers.services.TodoService;
import io.qameta.allure.Step;

import java.net.URISyntaxException;
import java.util.List;

import static common.constants.CommonConstants.*;
import static helpers.Generator.*;
import static helpers.RestHelper.login;
import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class TodoSteps {

    private final TodoService todoService = login(ADMIN_LOGIN, ADMIN_PASSWORD).todoService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Step("Create TODO")
    public Todo createTodo(Todo todo) {
        todoService.create(todo);
        return todo;
    }

    @Step("Update TODO")
    public Todo updateTodo(int id, Todo todo) {
        todoService.update(id, todo);
        return todo;
    }

    @Step("List TODOs")
    public List<Todo> listTodo() {
        return todoService.list();
    }

    @Step("List TODOs")
    public List<Todo> listTodo(int offset, int limit) {
        return todoService.list(offset, limit);
    }

    @Step("Delete TODO")
    public void deleteTodo(int id) {
        todoService.delete(id);
    }

    public void deleteAllTodo() {
        listTodo().forEach(n -> deleteTodo(n.getId()));
    }

    public Todo getTodo(int id) {
        return listTodo().stream().filter(n -> n.getId() == id).toList().getFirst();
    }

    public Todo updateTodo(int id, String title, boolean completed) {
        return updateTodo(id, Todo.builder().id(id).text(title).completed(completed).build());
    }

    public Todo createTodo(int id, String title, boolean completed) {
        return createTodo(Todo.builder().id(id).text(title).completed(completed).build());
    }

    public Todo createTodo() {
        return createTodo(getRandomTodo());
    }

    @Step("Connect to WebSocket")
    public WebSocketHelper connectWS() {
        WebSocketHelper wsClient;
        try {
            wsClient = new WebSocketHelper(ADDRESS);
            wsClient.connectBlocking();
            assertThat(wsClient.waitForConnection(5, SECONDS)).isTrue();
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return wsClient;
    }

    @Step("Convert messages to TODO")
    public List<Todo> convertMessageToTodo(List<String> messages) {
        return messages.stream()
                .map(n -> {
                    try {
                        JsonNode node = mapper.readTree(n);
                        JsonNode data = node.get("data");
                        return Todo.builder()
                                .id(data.get("id").asInt())
                                .text(data.get("text").asText())
                                .completed(data.get("completed").asBoolean())
                                .build();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .toList();
    }

    public Todo getRandomTodo() {
        return Todo.builder()
                .id(randInt(0, MAX_VALUE))
                .text(randString(6, 6, 4))
                .completed(randBool())
                .build();
    }
}
