package tr.com.yelloware.sandikbaskani.model;

public class KeyValueModel {
	
	private String text;
	
	private String code;

	public KeyValueModel(String text, String code) {
		super();
		this.text = text;
		this.code = code;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "KeyValueModel [text=" + text + ", code=" + code + "]";
	}

}
