package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User creation cases")
@Feature("User creation")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String createUserLink = "https://playground.learnqa.ru/api/user/";


    @Test
    @DisplayName("Test negative create user with existing email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post(createUserLink)
                .andReturn();

        Assertions.assertResponseTextEquals(responseCreateUser, "Users with email '" + email + "' already exists");
        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 400);
    }

    @Test
    @DisplayName("Test positive create user")
    public void testCreateNewUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post(createUserLink)
                .andReturn();

        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasKey(responseCreateUser, "id");
    }

    @Test
    @DisplayName("Negative case user creation with wrong email (w/o @ sign)")
    public void testCreateUserWithWrongEmail() {
        String wrongEmail = "emailexample.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", wrongEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        createUserLink,
                        userData
                );

        Assertions.assertResponseTextEquals(responseCreateUser, "Invalid email format");
        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 400);
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "username", "password", "firstName", "lastName"})
    @DisplayName("Negative case user creation without one of mandatory field")
    public void testCreateUserWithoutOneField(String missedKey) {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        userData.remove(missedKey);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        createUserLink,
                        userData
                );

        Assertions.assertResponseTextEquals(responseCreateUser, "The following required params are missed: " + missedKey);
        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 400);
    }

    @Test
    @DisplayName("Negative case user creation with wrong firstName (length = 1 char)")
    public void testCreateUserWithShortFirstName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "1");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        createUserLink,
                        userData
                );

        Assertions.assertResponseTextEquals(responseCreateUser, "The value of 'firstName' field is too short");
        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 400);
    }
    @Test
    @DisplayName("Negative case user creation with wrong firstName (length = 255 chars)")
    public void testCreateUserWithLongFirstName() {
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(
                        createUserLink,
                        userData
                );

        Assertions.assertResponseTextEquals(responseCreateUser, "The value of 'firstName' field is too long");
        Assertions.assertResponseStatusCodeEquals(responseCreateUser, 400);
    }
}
