package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorisation cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestCase {

    String userLoginLink = "https://playground.learnqa.ru/api/user/login";
    String userAuthLink = "https://playground.learnqa.ru/api/user/auth";
    String authSidCookie;
    String xcsrftokenHeader;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(userLoginLink, authData);

        authSidCookie = this.getCookie(responseGetAuth, "auth_sid");
        xcsrftokenHeader = this.getHeader(responseGetAuth, "x-csrf-token");
        userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorise user by email and password")
    @DisplayName("Test positive auth user")
    public void testUserAuth() {
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        userAuthLink,
                        xcsrftokenHeader,
                        authSidCookie
                );
        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @Description("This test checks authorisation status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ValueSource(strings = {"cookie", "header"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri(userAuthLink);
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.
                    makeGetRequestWithCookie(
                            userAuthLink,
                            authSidCookie
                    );

            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests.
                    makeGetRequestWithToken(
                            userAuthLink,
                            xcsrftokenHeader
                    );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);

        } else throw new IllegalArgumentException("Unknown argument: " + condition);
    }
}
