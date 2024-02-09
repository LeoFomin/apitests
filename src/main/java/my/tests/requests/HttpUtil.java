package my.tests.requests;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import org.slf4j.Logger;
import org.testng.TestNGException;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.slf4j.LoggerFactory.getLogger;

public class HttpUtil {
    private final static Logger logger = getLogger(HttpUtil.class);

    public static String getRequest(String url) {
        try {
            List<Header> headerList = new ArrayList<>();
            headerList.add(new Header("Connection", "keep-alive"));
            headerList.add(new Header("Content-Type", "application/json;charset=utf-8"));

            logger.info("Выполняем Get-запрос на {}", url);

            String response = given()
                    .relaxedHTTPSValidation()
                    .headers(new Headers(headerList))
                    .get(url)
                    .getBody()
                    .asString();

            logger.info("Получили ответ {}", response);

            return response;
        } catch (Throwable e) {
            throw new TestNGException("Ошибка при выполнении GET-запроса", e);
        }
    }
}