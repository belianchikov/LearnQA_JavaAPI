package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected");
    }

    public static void assertJsonByName(Response response, String name, String expectedValue) {
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected");
    }

    public static void assertResponseTextEquals(Response response, String expectedText) {
        assertEquals(
                expectedText,
                response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseStatusCodeEquals(Response response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                response.statusCode(),
                "Response status code is not as expected"
        );
    }

    public static void assertJsonHasKey(Response response, String expectedKey) {
        response.then().assertThat().body("$", hasKey(expectedKey));
    }

    public static void assertJsonHasNotKey(Response response, String unexpectedKey) {
        response.then().assertThat().body("$", not(hasKey(unexpectedKey)));
    }

    public static void assertJsonHasNotKeys(Response response, String[] arrayOfUnexpectedItems) {
        for (String key : arrayOfUnexpectedItems) {
            assertJsonHasNotKey(response, key);
        }
    }

    public static void assertJsonHasKeys(Response response, String[] arrayOfExpectedItems) {
        for (String key : arrayOfExpectedItems) {
            assertJsonHasKey(response, key);
        }
    }
}