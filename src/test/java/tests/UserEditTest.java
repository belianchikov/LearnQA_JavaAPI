package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedUser() {
        //Create User
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreateUser.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();

        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        //Edit
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //Get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @DisplayName("Negative test: changing user data as not auth user")
    public void testEditUserDataAsNotAuth() {
        //Create User
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String oldName = userData.get("firstName");

        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreateUser.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();

        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        //Edit user data not auth
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        editData);

        //Get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", oldName);
    }

    @Test
    @DisplayName("Negative test: changing user data as auth as different user")
    public void testEditUserDataAsAuthAsDifferentUser() {
        //Create User
        Map<String, String> userData1 = DataGenerator.getRegistrationData();

        Response responseCreateUser1 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData1
                );

        String userId1 = responseCreateUser1.jsonPath().getString("id");

        //Create User #2
        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        Response responseCreateUser2 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData2
                );

        String userId2 = responseCreateUser2.jsonPath().getString("id");

        //Login as user #1
        Map<String, String> authData = new HashMap<>();

        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));

        Response responseGetAuth1 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        authData
                );

        //Login as user #2
        Map<String, String> authData2 = new HashMap<>();

        authData2.put("email", userData2.get("email"));
        authData2.put("password", userData2.get("password"));

        Response responseGetAuth2 = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        authData2
                );

        //Edit user #2 logged as user #1
        String newName = "Changed name";
        String oldNameUser2 = userData2.get("firstName");
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth1, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth1, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId2)
                .andReturn();
        responseEditUser.prettyPrint();
        //Get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth2, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth2, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId2)
                .andReturn();
        Assertions.assertJsonByName(responseUserData, "firstName", oldNameUser2);
    }


    @Test
    public void testEditJustCreatedUserWithWrongEmail() {
        //Create User
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        String userId = responseCreateUser.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();

        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/login/",
                        authData
                );

        //Edit
        String newEmail = "some-email.com";
        String oldEmail = userData.get("email");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "email", oldEmail);
    }

    @Test
    public void testEditJustCreatedUserWithWrongFirstName() {
        //Create User
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        "https://playground.learnqa.ru/api/user/",
                        userData
                );

        String userId = responseCreateUser.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();

        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        //Edit
        String newFirstName = "a";
        String oldFirstName = userData.get("firstName");
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");
        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", oldFirstName);
    }
}
