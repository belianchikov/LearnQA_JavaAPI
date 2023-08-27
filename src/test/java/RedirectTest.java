import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class RedirectTest {
    @Test
    public void testRedirect(){
        String link = "https://playground.learnqa.ru/api/long_redirect";


        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get(link)
                .andReturn();

        System.out.println(response.headers().get("location").getValue());
    }
}
