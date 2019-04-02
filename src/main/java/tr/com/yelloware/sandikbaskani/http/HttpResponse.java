package tr.com.yelloware.sandikbaskani.http;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.nodes.Document;

@Data
@AllArgsConstructor
public class HttpResponse {

  private Document document;

  private Map<String, String> headersMap;

  private Map<String, String> cookies;

}
