package tr.com.yelloware.sandikbaskani.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HTTPUtil {

	private static final String CHP_SECIM_URL = "https://sts.chp.org.tr/";
	private static Map<String, String> CHP_COOKIES = new HashMap<String, String>();
	private static Map<String, String> LAST_HIDDEN_INPUTS_MAP = new HashMap<String, String>();;

	public static void main(String[] args) throws IOException {
		// login
		sendRequest(CHP_SECIM_URL,Method.GET,null);
		System.out.println("login done");
		// list cities
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("txtTCKN", "");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "rdveriKaynagi$1");
		HttpResponse citiesDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP);
		printOptions(citiesDoc.getDocument(), "ddlIller");
		
		// list districts
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", "34");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "ddlIller$1");
		HttpResponse districtsDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP);
		printOptions(districtsDoc.getDocument(), "ddlIlceler");
		
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", "34");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIlceler", "344");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "ddlIlceler");
		HttpResponse sandikListDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP);
		printOptions(sandikListDoc.getDocument(), "ddlSandiklar");
		
		LAST_HIDDEN_INPUTS_MAP.put("rdveriKaynagi", "2");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIller", "34");
		LAST_HIDDEN_INPUTS_MAP.put("ddlIlceler", "344");
		LAST_HIDDEN_INPUTS_MAP.put("ddlSandiklar", "3225201");
		LAST_HIDDEN_INPUTS_MAP.put("btnSorgula", "SORGULA");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTTARGET", "");
		LAST_HIDDEN_INPUTS_MAP.put("__EVENTARGUMENT", "");
		LAST_HIDDEN_INPUTS_MAP.put("__LASTFOCUS", "");
		HttpResponse sandikDoc = sendRequest(CHP_SECIM_URL, Method.POST, LAST_HIDDEN_INPUTS_MAP);
		
		System.out.println(sandikDoc.getDocument().html());
		System.out.println(sandikDoc.getHeadersMap().get("Location"));
		
	}

	private static void printOptions(Document citiesDoc, String optionsId) {
		Elements citiesElement = citiesDoc.getElementById(optionsId).getElementsByTag("option");
		for(Element cityEl : citiesElement){
			System.out.println(cityEl.text() + "-" + cityEl.attr("value"));
		}
	}

	private static HttpResponse sendRequest(String url,Method method, Map<String, String> dataMap) throws IOException {
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
		CHP_COOKIES = response.cookies();
		
		Document doc = response.parse();
		LAST_HIDDEN_INPUTS_MAP.clear();
		Elements hiddenElements = doc.getElementsByAttributeValue("type", "hidden");
		for (Element hiddenEl : hiddenElements) {
			LAST_HIDDEN_INPUTS_MAP.put(hiddenEl.attr("id"), hiddenEl.attr("value"));
		}
		
		return new HttpResponse(doc,response.headers());
	}

}
