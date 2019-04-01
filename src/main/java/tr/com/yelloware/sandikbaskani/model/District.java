package tr.com.yelloware.sandikbaskani.model;

import java.util.List;

public class District extends KeyValueModel {
	
	public District(String text, String code) {
		super(text, code);
	}

	private List<BallotBox> boxList ;
	
	public List<BallotBox> getBoxList() {
		return boxList;
	}
	
	public void setBoxList(List<BallotBox> boxList) {
		this.boxList = boxList;
	}

}
