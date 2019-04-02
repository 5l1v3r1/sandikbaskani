package tr.com.yelloware.sandikbaskani.util;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tr.com.yelloware.sandikbaskani.http.HttpResponse;

@UtilityClass
@Slf4j
public class HTTPUtil {

  static {
    System.setProperty("http.proxyHost", "195.87.49.10");
    System.setProperty("http.proxyPort", "8080");
    System.setProperty("https.proxyHost", "195.87.49.10");
    System.setProperty("https.proxyPort", "8080");
    log.info("http proxy initialized..");
  }

  public static HttpResponse sendRequest(String url, Method method, Map<String, String> dataMap, Map<String, String> cookies) throws IOException, InterruptedException {
    Connection connection = Jsoup.connect(url).method(method).timeout(0);
    connection.header("Connection", "keep-alive");
    connection.header("Upgrade-Insecure-Requests", "1");
    connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
    connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
    connection.header("Accept-Encoding", "gzip, deflate, br");
    connection.header("Accept-Language", "en-US,en;q=0.9,tr;q=0.8");
    connection.header("Referer", "https://sts.chp.org.tr/");

    connection.header("Content-Type", "application/x-www-form-urlencoded");
    connection.header("Cache-Control", "max-age=0");
    connection.header("Origin", "https://sts.chp.org.tr");

    if (Objects.nonNull(cookies)) {
      connection.cookies(cookies);
    }

    if (Objects.nonNull(dataMap)) {
      connection.data(dataMap);
    }

    Response response = openConnection(connection);

    Document doc = response.parse();

    return new HttpResponse(doc, response.headers(), response.cookies());
  }

  private static Response openConnection(Connection connection) throws InterruptedException, IOException {
    for (int i = 0; i < 5; i++) {
      try {
        return connection.execute();
      } catch (Exception e) {
        TimeUnit.SECONDS.sleep(4);
      }
    }
    return null;
  }

}
