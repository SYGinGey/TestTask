package testTask.api.test;

import common.data.Todo;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.*;
import testTask.api.steps.TodoSteps;

import static common.constants.CommonConstants.BASE_URL;
import static common.constants.HTTPStatus.NOT_FOUND;
import static common.constants.HTTPStatus.UNAUTHORIZED;
import static common.constants.TodoConstants.TODO_PATH;
import static helpers.Generator.randString;
import static helpers.RestHelper.login;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Tag("regress")
public class TodoDeleteTest {

    private final TodoSteps todoSteps = new TodoSteps();
    private Todo todo;

    @Test
    @DisplayName("Delete existing todo")
    void testDelete() {
        var todo = todoSteps.createTodo();
        todoSteps.deleteTodo(todo.getId());
        assertThat(todoSteps.listTodo().stream().filter(n -> n.getId() == todo.getId()).toList())
                .as("Todo should be deleted")
                .isEmpty();
    }

    @Test
    @DisplayName("Delete non-existent todo")
    void testDeleteNonExistentTodo() {
        assertThat(catchThrowable(() -> todoSteps.deleteTodo(999999)))
                .as("Deleting non-existent todo should return 404")
                .hasMessageContaining(NOT_FOUND);
    }

    @Test
    @DisplayName("Delete todo twice")
    void testDeleteTodoTwice() {
        var todo = todoSteps.createTodo();
        todoSteps.deleteTodo(todo.getId());
        assertThat(catchThrowable(() -> todoSteps.deleteTodo(todo.getId())))
                .as("Deleting non-existent todo should return 404")
                .hasMessageContaining(NOT_FOUND);
    }

    @Test
    @DisplayName("Delete todo with wrong Authorization")
    void testDeleteWithMissingAuth() {
        var todoService = login(randString(5, 5, 4), randString(5, 5, 4)).todoService;
        todo = todoSteps.createTodo();
        assertThat(catchThrowable(() -> todoService.delete(todo.getId())))
                .as("Wrong Auth returned wrong code")
                .hasMessageContaining(UNAUTHORIZED);
    }

    @Test
    @DisplayName("Delete todo with missing Authorization")
    void testDeleteWithMissingAuth1() {
        todo = todoSteps.createTodo();
        var REQ_SPEC = new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setBaseUri(BASE_URL)
                .setBasePath(TODO_PATH)
                .setContentType(JSON)
                .build();
        assertThat(catchThrowable(() -> given().spec(REQ_SPEC)
                .delete(String.valueOf(todo.getId()))
                .then()
                .statusCode(parseInt(UNAUTHORIZED))))
                .as("Unauthorized request should return 401")
                .isNull();
    }

    @AfterEach
    void tearDown() {
        if (todo != null) {
            todoSteps.deleteTodo(todo.getId());
        }
    }
}
