package com.science_o_matic.imedjelly.data;

import java.text.DateFormat;
import java.util.Date;

public class Comment {
	private String username;
	private String text;
	private Date date;
	
	public Comment(String username, String text, Date date) {
		this.username = username;
		this.text = text;
		this.date = date;
	}
	
	public Comment(String username, String text, DateFormat format, String date) {
		this.username = username;
		this.text = text;
		try {
			this.date = format.parse(date);
		}
		catch(Exception e) {}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getText() {
		return text;
	}
	
	public Date getDate() {
		return date;
	}
}