import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class PasswordGuessTest {
    String getPasswordLink = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    String checkAuthLink = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";

    String[] passwords = new String[]{"123456", "123456789", "qwerty",
            "password", "1234567", "12345678", "12345", "iloveyou",
            "111111", "123123", "abc123", "qwerty123", "1q2w3e4r",
            "admin", "qwertyuiop", "654321", "555555", "lovely",
            "7777777", "welcome", "888888", "princess", "dragon",
            "password1", "123qwe"};

    @Test
    public void testPasswordGuess() {
        for (String password : passwords) {
            Map<String, String> params = new HashMap<>();
            params.put("login", "super_admin");
            params.put("password", password);

            Response response = RestAssured
                    .given()
                    .body(params)
                    .post(getPasswordLink)
                    .andReturn();

            String authCookie = response.getCookie("auth_cookie");
            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", authCookie);

            //Already now we can find the correct password
            String result = response.jsonPath().get("equals").toString();
            if (result.equals("true")) {
                System.out.println("password after /get_secret_password_homework: " + password);
            }

            Response response1 = RestAssured
                    .given()
                    .cookies(cookies)
                    .get(checkAuthLink)
                    .andReturn();

            if (response1.getBody().asString().contains("You are authorized")) {
                System.out.println("password after /check_auth_cookie: " + password);
            }

        }
    }
}
