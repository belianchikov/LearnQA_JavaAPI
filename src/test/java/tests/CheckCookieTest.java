package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckCookieTest {
    @Test
    public void testCheckCookie(){
        String link = "https://playground.learnqa.ru/api/homework_cookie";

        Response response = RestAssured
                .given()
                .get(link)
                .andReturn();

        assertEquals("hw_value",response.getCookies().get("HomeWork"), "Unexpected cookie value");
    }
}
