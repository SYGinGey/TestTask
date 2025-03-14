package testTask.api.test;

import common.data.Todo;
import org.junit.jupiter.api.*;
import testTask.BaseTest;
import testTask.api.steps.TodoSteps;

import static common.constants.HTTPStatus.BAD_REQUEST;
import static common.constants.HTTPStatus.NOT_FOUND;
import static helpers.Generator.randInt;
import static helpers.Generator.randString;
import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Tag("regress")
public class TodoUpdateTest extends BaseTest {

    private final TodoSteps todoSteps = new TodoSteps();
    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = todoSteps.createTodo();
    }

    @Test
    @DisplayName("Update both text and status")
    void testUpdateTodo() {
        var newText = randString(10, 10, 4);
        var newStatus = !todo.isCompleted();
        todo = todoSteps.updateTodo(todo.getId(), newText, newStatus);

        var updatedTodo = todoSteps.getTodo(todo.getId());
        assertThat(updatedTodo).as("").isEqualTo(todo);
    }

    @Test
    @DisplayName("Update non-existent todo")
    void testUpdateNonExistentTodo() {
        var nonExistentId = randInt(10000, MAX_VALUE);
        assertThat(catchThrowable(() ->
                todoSteps.updateTodo(nonExistentId, "New text", false)))
                .as("Should throw error when updating non-existent todo")
                .hasMessageContaining(NOT_FOUND);
    }

    @Test
    @DisplayName("Update with wrong todo body (ID mismatch)")
    void testUpdateTodoWithWrongID() {
        var todo1 = todoSteps.createTodo();
        assertThat(catchThrowable(() ->
                todoSteps.updateTodo(todo1.getId(), todo)))
                .as("Should throw error when updating todo entity with different ID " +
                        "(now it's possible to create duplicates - bug)")
                .hasMessageContaining(BAD_REQUEST);
    }

    @Test
    @DisplayName("Update with SQL injection attempt")
    void testUpdateWithSqlInjection() {
        var sqlInjection = "'; DROP TABLE todos; --";
        todo = todoSteps.updateTodo(todo.getId(), sqlInjection, todo.isCompleted());

        var updatedTodo = todoSteps.getTodo(todo.getId());
        assertThat(updatedTodo.getText()).as("SQL injection should not be possible")
                .isEqualTo(sqlInjection);
    }

    @Test
    @DisplayName("Rapid sequential updates")
    void testRapidSequentialUpdates() {
        for (int i = 0; i < 5; i++) {
            var newText = randString(5, 5, 4);
            todo = todoSteps.updateTodo(todo.getId(), newText, todo.isCompleted());

            var updatedTodo = todoSteps.getTodo(todo.getId());
            assertThat(updatedTodo.getText())
                    .as("Text should be updated immediately")
                    .isEqualTo(newText);
        }
    }

    @AfterEach
    void tearDown() {
        if (todo != null) {
            todoSteps.deleteTodo(todo.getId());
        }
    }
}
