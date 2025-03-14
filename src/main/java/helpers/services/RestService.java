package helpers.services;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static common.constants.CommonConstants.BASE_URL;
import static io.restassured.http.ContentType.JSON;

public abstract class RestService {
  ;
    protected RequestSpecification REQ_SPEC;

    protected abstract String getBasePath();

    public RestService(Header token) {

        REQ_SPEC = new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .addHeader(token.getName(), token.getValue())
                .setBaseUri(BASE_URL)
                .setBasePath(getBasePath())
                .setContentType(JSON)
                .build();
    }
}
