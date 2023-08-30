import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectTest {
    String link = "https://playground.learnqa.ru/api/long_redirect";

    @Test
    public void testLongRedirect() {
        int counter = 0;
        while (true) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(link)
                    .andReturn();
            link = response.getHeader("Location");
            if (link == null) {
                break;
            }
            counter++;
        }
        System.out.println(counter);
    }
}
