package tr.com.yelloware.sandikbaskani.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tr.com.yelloware.sandikbaskani.model.BallotBox;
import tr.com.yelloware.sandikbaskani.model.BallotBoxResult;
import tr.com.yelloware.sandikbaskani.model.BallotBoxResult.BallotBoxResultBuilder;
import tr.com.yelloware.sandikbaskani.model.City;
import tr.com.yelloware.sandikbaskani.model.District;
import tr.com.yelloware.sandikbaskani.model.KeyValueModel;
import tr.com.yelloware.sandikbaskani.util.HTTPUtil;
import tr.com.yelloware.sandikbaskani.util.HtmlParser;
import tr.com.yelloware.sandikbaskani.util.NumberUtil;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ChpSession extends AbsHttpSession {

  private static final String LASTFOCUS = "__LASTFOCUS";

  private static final String EVENTARGUMENT = "__EVENTARGUMENT";

  public static final String CHP_SECIM_URL = "https://sts.chp.org.tr/";

  private static final String DDL_SANDIKLAR = "ddlSandiklar";
  private static final String DDL_ILCELER = "ddlIlceler";
  private static final String DDL_ILLER = "ddlIller";
  private static final String EVENTTARGET = "__EVENTTARGET";
  private static final String TXT_TCKN = "txtTCKN";
  private static final String RDVERI_KAYNAGI = "rdveriKaynagi";

  private Map<String, String> hiddenInputsMap = new HashMap<>();

  public void openMainPage() throws IOException, InterruptedException {
    HttpResponse response = HTTPUtil.sendRequest(CHP_SECIM_URL, Method.GET, null, cookies);
    updateHiddenInputs(response);
    updateCookies(response);
    log.info("Giriş yapıldı");
  }

  public List<City> listCities() throws IOException, InterruptedException {
    hiddenInputsMap.put(RDVERI_KAYNAGI, "2");
    hiddenInputsMap.put(TXT_TCKN, "");
    hiddenInputsMap.put(EVENTTARGET, "rdveriKaynagi$1");
    HttpResponse response = HTTPUtil.sendRequest(CHP_SECIM_URL, Method.POST, hiddenInputsMap, cookies);
    updateHiddenInputs(response);
    updateCookies(response);
    List<KeyValueModel> options = HtmlParser.parseHtmlOptions(response.getDocument(), DDL_ILLER);
    return options.stream().map(o -> new City(o.getText(), o.getCode())).collect(Collectors.toList());
  }

  public List<District> listDistricts(String cityCode) throws IOException, InterruptedException {
    hiddenInputsMap.put(RDVERI_KAYNAGI, "2");
    hiddenInputsMap.put(DDL_ILLER, cityCode);
    hiddenInputsMap.put(EVENTTARGET, "ddlIller$1");
    HttpResponse response = HTTPUtil.sendRequest(CHP_SECIM_URL, Method.POST, hiddenInputsMap, cookies);
    List<KeyValueModel> options = HtmlParser.parseHtmlOptions(response.getDocument(), DDL_ILCELER);
    updateHiddenInputs(response);
    updateCookies(response);
    return options.stream().map(o -> new District(o.getText(), o.getCode())).collect(Collectors.toList());
  }

  public List<BallotBox> listBallotBoxes(String cityCode, String districtCode) throws IOException, InterruptedException {
    hiddenInputsMap.put(RDVERI_KAYNAGI, "2");
    hiddenInputsMap.put(DDL_ILLER, cityCode);
    hiddenInputsMap.put(DDL_ILCELER, districtCode);
    hiddenInputsMap.put(EVENTTARGET, DDL_ILCELER);
    HttpResponse response = HTTPUtil.sendRequest(CHP_SECIM_URL, Method.POST, hiddenInputsMap, cookies);
    updateHiddenInputs(response);
    updateCookies(response);
    List<KeyValueModel> options = HtmlParser.parseHtmlOptions(response.getDocument(), DDL_SANDIKLAR);
    return options.stream().map(o -> new BallotBox(o.getText(), o.getCode())).collect(Collectors.toList());
  }

  public BallotBoxResult getBallotBoxResult(String cityCode, String districtCode, BallotBox ballotBox) throws IOException, InterruptedException {
    hiddenInputsMap.put(RDVERI_KAYNAGI, "2");
    hiddenInputsMap.put(DDL_ILLER, cityCode);
    hiddenInputsMap.put(DDL_ILCELER, districtCode);
    hiddenInputsMap.put(DDL_SANDIKLAR, ballotBox.getCode());
    hiddenInputsMap.put("btnSorgula", "SORGULA");
    hiddenInputsMap.put(EVENTTARGET, "");
    hiddenInputsMap.put(EVENTARGUMENT, "");
    hiddenInputsMap.put(LASTFOCUS, "");
    HttpResponse response = HTTPUtil.sendRequest(CHP_SECIM_URL, Method.POST, hiddenInputsMap, cookies);

    BallotBoxResultBuilder resultBuilder = BallotBoxResult.builder();
    Document document = response.getDocument();
    String title = HtmlParser.getText(document, "lblIlIlceBaslik");
    resultBuilder.title(title);
    resultBuilder.address(HtmlParser.getText(document, "lblMvOzetSandikAlani"));
    resultBuilder.yskReceiveTime(HtmlParser.getText(document, "lblMvGelisZamani"));
    resultBuilder.registeredVoterCount(HtmlParser.getNumericValue(document, "tbMvKayitliSecmenSayisi"));
    resultBuilder.totalVoteCount(HtmlParser.getNumericValue(document, "tbMvKullanilanToplamOy"));
    Long validVoteCount = HtmlParser.getNumericValue(document, "tbMvGecerliOySayisi");
    resultBuilder.validVoteCount(validVoteCount);
    resultBuilder.notValidVoteCount(HtmlParser.getNumericValue(document, "tbMvGecersizOySayisi"));

    resultBuilder.chpVoteCount(HtmlParser.getNumericValue(document, "txtCHP"));
    resultBuilder.ampulVoteCount(HtmlParser.getNumericValue(document, "txtAKP"));
    resultBuilder.saadetVoteCount(HtmlParser.getNumericValue(document, "txtSAADET"));
    resultBuilder.dspVoteCount(HtmlParser.getNumericValue(document, "txtDSP"));
    resultBuilder.independentVoteCount(HtmlParser.getNumericValue(document, "txtBagimsiz"));

    Elements voteElements = document.getElementsByAttributeValue("class", "chp-vote-field");
    List<Long> partiesSumList = new ArrayList<>();
    for (Element voteEl : voteElements) {
      partiesSumList.add(HtmlParser.getNumericValue(document, voteEl.attr("id")));
    }

    Long partiesSum = NumberUtil.sum(partiesSumList.stream().toArray(Long[]::new));
    resultBuilder.partiesSum(partiesSum);

    resultBuilder.notMatchVoteCount(validVoteCount - partiesSum);

    return resultBuilder.build();
  }

  private void updateHiddenInputs(HttpResponse response) {
    hiddenInputsMap.clear();
    Elements hiddenElements = response.getDocument().getElementsByAttributeValue("type", "hidden");
    for (Element hiddenEl : hiddenElements) {
      hiddenInputsMap.put(hiddenEl.attr("id"), hiddenEl.attr("value"));
    }
  }

  private void updateCookies(HttpResponse response) {
    cookies = response.getCookies();
  }

}
