import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testRestAssured() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Jonny");
        payload.put("name2", "Margo");

        Response response = RestAssured
                .given()
                .body(payload)
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        int statusCode = response.statusCode();
        System.out.println(statusCode);
//        String answer = response.get("answer2");
//        if (answer. == null) {
//            System.out.println("The key 'answer2' is absence");
//        } else {
//            System.out.println(answer);
//        }
    }
}
