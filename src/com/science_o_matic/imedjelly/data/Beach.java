package com.science_o_matic.imedjelly.data;

public class Beach {
	
	private long id;
	private String name;
	private long api_id;
	private double latitude;
	private double longitude;
	private String jellyfish_status;
	private String municipality_name;
	
	public Beach() {
		clean();
	}

	public Beach(long id, String name, long api_id,
		double latitude, double longitude,
		String jellyfish_status, String municipality_name) {
		this.id = id;
		this.name = name;
		this.api_id = api_id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.jellyfish_status = jellyfish_status;
		this.municipality_name = municipality_name;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getApi_Id() {
		return api_id;
	}
	
	public void setApi_Id(long api_id) {
		this.api_id = api_id;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getJellyfish_Status() {
		return jellyfish_status;
	}
	
	public void setJellyfish_Status(String jellyfish_status) {
		this.jellyfish_status = jellyfish_status;
	}
	
	public String getMunicipality_Name() {
		return municipality_name;
	}
	
	public void setMunicipality_Name(String municipality_name) {
		this.municipality_name = municipality_name;
	}
	
	public void clean() {
		this.id = -1;
		this.name = null;
		this.api_id = -1;
		this.latitude = -1.0;
		this.longitude = -1.0;
		this.jellyfish_status = null;
		this.municipality_name = null;
	}
}