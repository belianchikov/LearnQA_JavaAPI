package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UserAuthTest extends BaseTestCase {
    String userLoginLink = "https://playground.learnqa.ru/api/user/login";
    String userAuthLink = "https://playground.learnqa.ru/api/user/auth";
    String authSidCookie;
    String xcsrftokenHeader;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post(userLoginLink)
                .andReturn();

        authSidCookie = this.getCookie(responseGetAuth, "auth_sid");
        xcsrftokenHeader = this.getHeader(responseGetAuth, "x-csrf-token");
        userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    public void testUserAuth() {
        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", xcsrftokenHeader)
                .cookie("auth_sid", authSidCookie)
                .get(userAuthLink)
                .andReturn();

        Assertions.assertJsonByName(responseCheckAuth,"user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "header"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri(userAuthLink);
        if (condition.equals("cookie")) {
            spec.cookie("auth_cookie", authSidCookie);
        } else if (condition.equals("header")) {
            spec.header("x-csrf-token", xcsrftokenHeader);
        } else throw new IllegalArgumentException("Unknown argument");
        Response responseCheck = spec.get().andReturn();
        Assertions.assertJsonByName(responseCheck,"user_id", 0);
    }
}
