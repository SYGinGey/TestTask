package helpers;

import helpers.services.*;
import io.restassured.RestAssured;
import io.restassured.http.Header;

import java.util.Base64;

import static io.restassured.parsing.Parser.JSON;

public class RestHelper {

    public TodoService todoService;

    private RestHelper(Header token) {
        RestAssured.defaultParser = JSON;
        todoService = new TodoService(token);
    }

    public static RestHelper login(String login, String password) {
        return new RestHelper(new Header("Authorization", "Basic " + Base64.getEncoder().encodeToString((login + ":" + password).getBytes())));
    }
}
