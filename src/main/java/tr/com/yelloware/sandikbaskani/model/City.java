package tr.com.yelloware.sandikbaskani.model;

import java.util.List;

public class City extends KeyValueModel {
	
	public City(String text, String code) {
		super(text, code);
	}

	private List<District> districtList;

	public List<District> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<District> districtList) {
		this.districtList = districtList;
	}
	
	

}
