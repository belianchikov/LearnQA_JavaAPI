import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "Sobaka", "Koshka"})
    public void testHelloMethodWithoutName(String name) {
        String link = "https://playground.learnqa.ru/api/hello";

        Map<String, String> queryParams = new HashMap<>();
        if (!name.isEmpty()) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get(link)
                .jsonPath();

        String answer = response.getString("answer");
        String expectedName = (name.isEmpty()) ? "someone" : name;
        assertEquals("Hello, " + expectedName, answer, "Wrong response");
    }
}