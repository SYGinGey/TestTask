# Todo API Test Automation Project

Automated test suite for Todo REST API testing using Java 21, JUnit 5, and RestAssured.

## Project Overview

This project contains automated tests for a Todo API with CRUD operations and WS:
- Create todo items
- Read/List todos
- Update todo items
- Delete todo items
- Check WebSocket updates

## Tech Stack

- Java 21
- Maven
- JUnit 5
- REST Assured
- AssertJ
- Allure
- Gatling (for load testing)

## Running Tests

Run all tests:
```bash
mvn test
```

Run regression suite:
```bash
mvn test -Dtags="regress"
```

Run specific test class:
```bash
mvn test -Dtest=TodoListTest
mvn test -Dtest=TodoUpdateTest
mvn test -Dtest=TodoCreateTest
mvn test -Dtest=TodoDeleteTest
mvn test -Dtest=TodoWebSocketTest
```

To run with a locally loaded image:
```bash
docker load < todo-app.tar
```
Then run the tests:
```bash
mkdir -p target/allure-results
docker-compose -f deployments/docker-compose.yaml up --build tests
docker cp $(docker ps -aqf "name=tests"):/usr/app/target/allure-results ./target
docker-compose -f deployments/docker-compose.yaml down
```

Load Tests
```bash
mvn gatling:test
```


## Test Reports
Generate Allure report:
```bash
mvn allure:serve
```
Reports are generated in:
- Allure reports: target/allure-results/
- Gatling reports: target/gatling/