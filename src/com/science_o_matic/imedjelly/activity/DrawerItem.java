package com.science_o_matic.imedjelly.activity;

public class DrawerItem {
	private String title;
	private int icon;
	
	public DrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIcon() {
		return this.icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
}
