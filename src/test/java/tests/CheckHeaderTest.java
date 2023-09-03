package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckHeaderTest {
    @Test
    public void testCheckHeader() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        assertEquals("Some secret value", response.getHeader("x-secret-homework-header"), "Unexpected header value");
    }
}
