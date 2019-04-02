package tr.com.yelloware.sandikbaskani.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tr.com.yelloware.sandikbaskani.model.KeyValueModel;

@UtilityClass
public class HtmlParser {

  public static List<KeyValueModel> parseHtmlOptions(Document citiesDoc, String optionsId) {
    List<KeyValueModel> retList = new ArrayList<>();
    Elements citiesElement = citiesDoc.getElementById(optionsId).getElementsByTag("option");
    for (Element cityEl : citiesElement) {
      KeyValueModel keyVal = new KeyValueModel(cityEl.text(), cityEl.attr("value"));
      if ("0".equals(keyVal.getCode())) {
        continue; // ignore select label
      }
      retList.add(keyVal);
    }
    return retList;
  }

  public static String getText(Document document, String id) {
    Element element = document.getElementById(id);
    if (Objects.nonNull(element)) {
      return element.text();
    }
    return null;
  }

  public static Long getNumericValue(Document document, String id) {
    Element element = document.getElementById(id);
    if (Objects.nonNull(element)) {
      return NumberUtil.toLong(element.attr("value"));
    }
    return 0L;
  }

}
