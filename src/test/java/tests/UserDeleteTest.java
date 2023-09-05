package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Negative test: delete user with id = 2")
    public void testDeleteUserWithId2() {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        //Login as user
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        userData
                );

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/2/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        Response responseGetUser = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/2/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        String[] expectedKeys = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasKeys(responseGetUser, expectedKeys);
    }

    @Test
    @DisplayName("Positive test: create user and delete it")
    public void testDeleteJustCreatedUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasKey(responseCreateUser, "id");

        String userId = responseCreateUser.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));


        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        authData
                );

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Response responseGetUser = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertResponseTextEquals(responseGetUser, "User not found");
    }


    @Test
    @DisplayName("Negative test: delete user auth as different user")
    public void testDeleteUserAuthAsDifferentUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasKey(responseCreateUser, "id");

        String userId = responseCreateUser.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        authData
                );

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );


        Response responseGetUser = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid")
                );

        Assertions.assertJsonByName(responseGetUser, "username", userData.get("username"));
    }


}
