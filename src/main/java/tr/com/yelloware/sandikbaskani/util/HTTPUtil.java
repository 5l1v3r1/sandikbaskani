package tr.com.yelloware.sandikbaskani.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.experimental.UtilityClass;
import tr.com.yelloware.sandikbaskani.model.BallotBox;
import tr.com.yelloware.sandikbaskani.model.City;
import tr.com.yelloware.sandikbaskani.model.District;
import tr.com.yelloware.sandikbaskani.model.KeyValueModel;

@UtilityClass
public class HTTPUtil {

	private static final String CHP_SECIM_URL = "https://sts.chp.org.tr/";
	private static Map<String, String> CHP_COOKIES = new HashMap<String, String>();
	private static Map<String, String> LAST_HIDDEN_INPUTS_MAP = new HashMap<String, String>();;

	public static void main(String[] args) throws IOException {
		openMainPage();
		List<City> cities = listCities();
		System.out.println("Fetched " + cities.size() + " city code");
		List<District> districtList = listDistricts("34");
		for(District district : districtList){
			System.out.println("Processing " + district);
			List<BallotBox> ballotBoxList = listBallotBoxes("34",district.getCode());
			System.out.println("Fetched " + ballotBoxList.size() + " box code");
			for(BallotBox bb : ballotBoxList){
				System.out.println("Processing " + bb);
				try {
//					if(bb.getCode().equals("3215055")){
						BallotBox ballotBox = getBallotBoxResult("34",district.getCode(),bb.getCode());
//					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		
		
		
	}

	private static BallotBox getBallotBoxResult(String cityCode, String districtCode, String ballotBoxId) throws IOException {
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", cityCode);
		LAST_HIDDEN_INPUTS_MAP.put("ddlIlceler", districtCode);
		LAST_HIDDEN_INPUTS_MAP.put("ddlSandiklar", ballotBoxId);
		LAST_HIDDEN_INPUTS_MAP.put("btnSorgula", "SORGULA");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTARGUMENT", "");
		LAST_HIDDEN_INPUTS_MAP.put("__LASTFOCUS", "");
		HttpResponse sandikDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP,false);
		BallotBox ballotBox = new BallotBox("", ballotBoxId);
		return ballotBox ;
	}

	private static List<BallotBox> listBallotBoxes(String cityCode, String districtCode) throws IOException {
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", cityCode);
		LAST_HIDDEN_INPUTS_MAP.put("ddlIlceler", districtCode);
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "ddlIlceler");
		HttpResponse sandikListDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP,true);
		List<KeyValueModel> options = parseHtmlOptions(sandikListDoc.getDocument(), "ddlSandiklar");
		return options.stream().map(o -> new BallotBox(o.getText(), o.getCode())).collect(Collectors.toList());
	}

	private static List<District> listDistricts(String cityCode) throws IOException {
		// list districts
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", cityCode);
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "ddlIller$1");
		HttpResponse districtsDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP,true);
		List<KeyValueModel> options = parseHtmlOptions(districtsDoc.getDocument(), "ddlIlceler");
		return options.stream().map(o -> new District(o.getText(), o.getCode())).collect(Collectors.toList());
	}

	private static List<City> listCities() throws IOException {
		List<City> cities = new ArrayList<>();
		// list cities
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("txtTCKN", "");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "rdveriKaynagi$1");
		HttpResponse citiesDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP,true);
		List<KeyValueModel> options = parseHtmlOptions(citiesDoc.getDocument(), "ddlIller");
		return options.stream().map(o -> new City(o.getText(), o.getCode())).collect(Collectors.toList());
	}

	private static void openMainPage() throws IOException {
		// login
		sendRequest(CHP_SECIM_URL,Method.GET,null,true);
		System.out.println("login done");
	}

	private static List<KeyValueModel> parseHtmlOptions(Document citiesDoc, String optionsId) {
		List<KeyValueModel> retList = new ArrayList<KeyValueModel>();
		Elements citiesElement = citiesDoc.getElementById(optionsId).getElementsByTag("option");
		for(Element cityEl : citiesElement){
			KeyValueModel keyVal = new KeyValueModel(cityEl.text(),cityEl.attr("value"));
			if("0".equals(keyVal.getCode())){
				continue; // ignore select label
			}
			retList.add(keyVal);
		}
		return retList;
	}

	private static HttpResponse sendRequest(String url,Method method, Map<String, String> dataMap,boolean overrideCookies) throws IOException {
		Connection connection = Jsoup.connect(url).method(method).timeout(0);
		connection.header("Connection", "keep-alive");
		connection.header("Upgrade-Insecure-Requests", "1");
		connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
		connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
		connection.header("Accept-Encoding", "gzip, deflate, br");
		connection.header("Accept-Language", "keep-alive");
		connection.header("Connection", "en-US,en;q=0.9,tr;q=0.8");
		connection.cookies(CHP_COOKIES);
		
		if(Objects.nonNull(dataMap)){
			connection.data(dataMap);
		}
		
		Response response = connection.execute();
		if(overrideCookies){
			CHP_COOKIES = response.cookies();
		}
		
		Document doc = response.parse();
		LAST_HIDDEN_INPUTS_MAP.clear();
		Elements hiddenElements = doc.getElementsByAttributeValue("type", "hidden");
		for (Element hiddenEl : hiddenElements) {
			LAST_HIDDEN_INPUTS_MAP.put(hiddenEl.attr("id"), hiddenEl.attr("value"));
		}
		
		return new HttpResponse(doc,response.headers());
	}

}
