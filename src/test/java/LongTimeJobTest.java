import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class LongTimeJobTest {

    String link = "https://playground.learnqa.ru/ajax/api/longtime_job";

    @Test
    public void testLongTimeJob() throws InterruptedException {
        JsonPath response = RestAssured
                .given()
                .get(link)
                .jsonPath();

        response.prettyPrint();

        String token = response.get("token");
        int seconds = response.get("seconds");
        int millis = seconds * 1000;

        JsonPath response2 = RestAssured
                .given()
                .queryParam("token", token)
                .get(link)
                .jsonPath();

        response2.prettyPrint();

        String responseStatus = response2.get("status");

        assert responseStatus.equals("Job is NOT ready") : "Wrong status after job creation";

        Thread.sleep(millis);

        JsonPath response3 = RestAssured
                .given()
                .queryParam("token", token)
                .get(link)
                .jsonPath();
        response3.prettyPrint();

        String responseStatus2 = response3.get("status");
        String result = response3.get("result");

        assert result != null : "Wrong result after job completion";
        assert responseStatus2.equals("Job is ready") : "Wrong status after job completion";
    }
}
