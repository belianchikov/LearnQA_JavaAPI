import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

public class JsonParsingTest {
    @Test
    public void testJSONParsing() {
        String link = "https://playground.learnqa.ru/api/get_json_homework";

        JsonPath response = RestAssured
                .given()
                .get(link)
                .jsonPath();

        ArrayList<Array> answer = response.get("messages");
        System.out.println(answer.get(1));

    }
}