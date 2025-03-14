package testTask.api.test;

import common.data.Todo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import testTask.BaseTest;
import testTask.api.steps.TodoSteps;

import static common.constants.HTTPStatus.BAD_REQUEST;
import static helpers.Generator.randInt;
import static helpers.Generator.randString;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.assertj.core.api.Assertions.*;

@Tag("regress")
public class TodoCreateTest extends BaseTest {

    private final TodoSteps todoSteps = new TodoSteps();
    private Todo todo;

    @Test
    @DisplayName("Create todo with valid data")
    public void testCreateTodo() {
        todo = todoSteps.createTodo();
        assertThat(todo).as("Created todo should match retrieved todo")
                .isEqualTo(todoSteps.getTodo(todo.getId()));
    }

    @Test
    @DisplayName("Create todo with empty text")
    void testCreateTodoWithEmptyText() {
        todo = todoSteps.createTodo();
        assertThat(todo).as("Todo with empty text should be created")
                .isEqualTo(todoSteps.getTodo(todo.getId()));
    }

    @Test
    @DisplayName("Create todo with duplicate ID")
    void testCreateTodoWithDuplicateId() {
        todo = todoSteps.createTodo();
        assertThat(catchThrowable(() ->
                todoSteps.createTodo(todo.getId(), randString(1, 10, 4), true)))
                .as("Should throw 400 error for duplicate ID")
                .hasMessageContaining(BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, MIN_VALUE})
    @DisplayName("Create todo with negative IDs")
    void testCreateTodoWithNegativeIds(int id) {
        assertThat(catchThrowable(() ->
                todoSteps.createTodo(id, randString(1, 10, 4), false)))
                .as("Should throw error for negative ID")
                .hasMessageContaining(BAD_REQUEST);
    }

    @Test
    @DisplayName("Create todo with very long text")
    void testCreateTodoWithLongText() {
        todo = todoSteps.createTodo(
                randInt(0, MAX_VALUE),
                randString(1000, 1000, 4),
                false
        );
        assertThat(todo).as("Todo with long text should be created")
                .isEqualTo(todoSteps.getTodo(todo.getId()));
    }


    @AfterEach
    void tearDown() {
        if (todo != null) {
            todoSteps.deleteTodo(todo.getId());
        }
    }
}
