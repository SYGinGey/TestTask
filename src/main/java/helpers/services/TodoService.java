package helpers.services;

import common.data.Todo;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import java.util.Map;

import static common.constants.HTTPStatus.*;
import static common.constants.TodoConstants.TODO_PATH;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.parseInt;

public class TodoService extends RestService{

    @Override
    protected String getBasePath() {
        return TODO_PATH;
    }

    public TodoService(Header token) {
        super(token);
    }

    private List<Todo> list(Map<String, String> params) {
        return List.of(given()
                .spec(REQ_SPEC)
                .queryParams(params)
                .get().then()
                .statusCode(parseInt(OK))
                .extract().as(Todo[].class));
    }

    public List<Todo> list(int offset, int limit) {
        return list(Map.of("offset", String.valueOf(offset), "limit", String.valueOf(limit)));
    }

    public List<Todo> list() {
        return list(Map.of());
    }

    // route returns empty response, can create only one entity at a time
    public ValidatableResponse create(Todo todo) {
        return given().spec(REQ_SPEC)
                .body(todo)
                .post()
                .then()
                .statusCode(parseInt(CREATED));
    }

    public ValidatableResponse update(int id, Todo todo) {
        return  given().spec(REQ_SPEC)
                .body(todo)
                .put(String.valueOf(id))
                .then()
                .statusCode(parseInt(OK));
    }

    public ValidatableResponse delete(int todoID) {
        return  given().spec(REQ_SPEC)
                .delete(String.valueOf(todoID))
                .then()
                .statusCode(parseInt(NO_CONTENT));
    }
}
