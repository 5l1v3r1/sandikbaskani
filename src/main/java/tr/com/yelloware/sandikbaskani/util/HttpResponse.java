package tr.com.yelloware.sandikbaskani.util;

import java.util.Map;

import org.jsoup.nodes.Document;

public class HttpResponse {
	
	private Document document ;
	
	private Map<String, String> headersMap ;

	public HttpResponse(Document document, Map<String, String> headersMap) {
		super();
		this.document = document;
		this.headersMap = headersMap;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Map<String, String> getHeadersMap() {
		return headersMap;
	}

	public void setHeadersMap(Map<String, String> headersMap) {
		this.headersMap = headersMap;
	}
	
	
	
	
	

}
