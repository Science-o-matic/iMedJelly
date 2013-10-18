package com.science_o_matic.imedjelly.data;

public class JellyFish {
	
	private long id;
	private String title;
	private String name;
	private String subname;
	private String level;
	private String danger;
	private String environment;
	private String frequency;
	private String characteristics;
	private int picture;

	public JellyFish() {
		clean();
	}

	public JellyFish(long id, String title,
			String name, String subname, String level,
			String danger, String environment,
			String frequency, String characteristics,
			int picture) {
		this.id = id;
		this.title = title;
		this.name = name;
		this.subname = subname;
		this.level = level;
		this.danger = danger;
		this.environment = environment;
		this.frequency = frequency;
		this.characteristics = characteristics;
		this.picture = picture;
	}
	
	public JellyFish(long id, int picture) {
		this.id = id;
		this.picture = picture;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSubname() {
		return subname;
	}
	
	public void setSubname(String subname) {
		this.subname = subname;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getDanger() {
		return danger;
	}
	
	public void setDanger(String danger) {
		this.danger = danger;
	}
	
	public String getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getFrequency() {
		return frequency;
	}
	
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getCharacteristics() {
		return characteristics;
	}
	
	public void setCharacteristics(String characteristics) {
		this.characteristics = characteristics;
	}
	
	public int getPicture() {
		return picture;
	}
	
	public void setPicture(int picture) {
		this.picture = picture;
	}
	
	public void clean() {
		this.id = -1;
		this.title = null;
		this.name = null;
		this.subname = null;
		this.level = null;
		this.danger = null;
		this.environment = null;
		this.frequency = null;
		this.characteristics = null;
		this.picture = -1;
	}
}