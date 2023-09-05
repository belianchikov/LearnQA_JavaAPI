package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2/")
                .andReturn();

        String[] unexpectedKeys = {"email", "password", "firstName", "lastName"};
        Assertions.assertJsonHasKey(responseUserData, "username");
        Assertions.assertJsonHasNotKeys(responseUserData, unexpectedKeys);
    }

    @Test
    public void testGetUserDataAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseGetUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2/")
                .andReturn();

        String[] expectedKeys = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasKeys(responseGetUserData, expectedKeys);
    }

    @Test
    @DisplayName("Test negative get user info logged as another user")
    public void testGetUserDataAsAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseGetUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/1/",
                        header,
                        cookie);

        String[] unexpectedKeys = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasKey(responseGetUserData, "username");
        Assertions.assertJsonHasNotKeys(responseGetUserData, unexpectedKeys);
    }

}
