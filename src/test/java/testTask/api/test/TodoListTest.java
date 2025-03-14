package testTask.api.test;

import common.data.Todo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import testTask.BaseTest;
import testTask.api.steps.TodoSteps;

import java.util.ArrayList;
import java.util.List;

import static common.constants.HTTPStatus.BAD_REQUEST;
import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Isolated
@Tag("regress")
public class TodoListTest extends BaseTest {
    private final TodoSteps todoSteps = new TodoSteps();
    private final List<Todo> todosToCleanup = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 5; i++) {
            todosToCleanup.add(todoSteps.createTodo());
        }
    }

    @Test
    @DisplayName("List all todos without pagination")
    void testListAllTodos() {
        var todos = todoSteps.listTodo();
        assertThat(todos).as("List of todos should contain all created todos")
                .hasSize(5)
                .containsAll(todosToCleanup);
    }

    @Test
    @DisplayName("List todos with pagination")
    void testListTodosWithPagination() {
        assertThat(todoSteps.listTodo(1, 3))
                .as("List of todos should contain 3 todos, according to pagination")
                .hasSize(3)
                .containsExactlyElementsOf(todosToCleanup.subList(1, 4));
    }

    @Test
    @DisplayName("List todos with offset beyond size")
    void testListTodosWithLargeOffset() {
        assertThat(todoSteps.listTodo(MAX_VALUE, 5))
                .as("List of todos should be empty")
                .isEmpty();
    }

    @Test
    @DisplayName("List empty todos")
    void testListEmptyTodos() {
        todosToCleanup.forEach(todo -> todoSteps.deleteTodo(todo.getId()));
        todosToCleanup.clear();

        assertThat(todoSteps.listTodo()).as("List of todos should be empty").isEmpty();
    }

    @Test
    @DisplayName("List todos with negative pagination values")
    void testListTodosWithNegativeValues() {
        assertThat(catchThrowable(() -> todoSteps.listTodo(-1, 5)))
                .as("Negative offset value should return 400")
                .hasMessageContaining(BAD_REQUEST);

        assertThat(catchThrowable(() -> todoSteps.listTodo(0, -5)))
                .as("Negative limit value should return 400")
                .hasMessageContaining(BAD_REQUEST);

        assertThat(catchThrowable(() -> todoSteps.listTodo(-1, -5)))
                .as("Negative pagination values should return 400")
                .hasMessageContaining(BAD_REQUEST);
    }

    @AfterEach
    void tearDown() {
        todosToCleanup.forEach(todo -> todoSteps.deleteTodo(todo.getId()));
    }

    /*
     * Test non-numeric pagination parameters
     * Test very large limit values
     * Test with special characters in parameters
     * Test empty/null pagination parameters
     * Test list performance with large datasets
     */
}