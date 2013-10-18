package com.science_o_matic.imedjelly.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import android.util.Log;

public class BeachDescription {
	private static final String TAG = "BeachDescription";

	public final static int Invalid = 0x7FFFFFFF;
	public final static String DateFormat = "yyyy-MM-dd HH:mm:ss";

	public double rating;
	public String name;
	public String description;
	public String flagStatus;
	public List<String> services;
	public String jellyFishes;
	public int windSpeed;
	public int maxUV;
	public String jellyFishStatus;
	public String skyStatusCode;
	public int maxTemperature;
	public int minTemperature;
	public String windDirection;
	public int waterTemperature;
	public List<Comment> comments;

	final String services_available[] = {
		"bike_parking",
		"blue_flag",
		"children_zone",
		"dump",
		"handicapped_access",
		"handicapped_friendly",
		"parking",
		"rent_umbrella_hammock",
		"restaurant",
		"shower",
		"slogans",
		"sport_zone",
		"surveillance_tower",
		"water_source",
		"wc"
	};
	
	
	public BeachDescription() {
		this.rating = 0.0;
		this.name = null;
		this.description = null;
		this.flagStatus = null;
		this.services = new ArrayList<String>();
		this.jellyFishes = null;
		this.windSpeed = Invalid;
		this.maxUV = Invalid;
		this.jellyFishStatus = null;
		this.skyStatusCode = null;
		this.maxTemperature = Invalid;
		this.minTemperature = Invalid;
		this.windDirection = null;
		this.waterTemperature = Invalid;
		this.comments = new ArrayList<Comment>();
	}

	private void parseJellyFishes(JsonReader reader) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		try {
			reader.beginArray();
			while(reader.hasNext()) {
				if(count > 0) {
					sb.append(", ");
				}
				sb.append(reader.nextString());
				count ++;
			}
			reader.endArray();
			jellyFishes = sb.toString();
		}
		catch(Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}
	}

	private void parseServices(JsonReader reader) {
		try {
			reader.beginArray();
			while(reader.hasNext()) {
				String service = reader.nextString();
				for(String s: services_available) {
					if(s.equals(service)) {
						services.add(service);
						break;
					}
				}
			}
			reader.endArray();
		}
		catch(Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}
	}

	private void parseComments(JsonReader reader) {
		DateFormat format = new SimpleDateFormat(DateFormat, Locale.ENGLISH);
		try {
			reader.beginObject();
			while(reader.hasNext()) {
				String section = reader.nextName();
				if(section.equals("beachComments") && reader.peek() != JsonToken.NULL) {
					reader.beginArray();
					while(reader.hasNext()) {
						String username = null;
						String text = null;
						String date = null;
						reader.beginObject();
						while (reader.hasNext()) {
							String item = reader.nextName();
							if (item.equals("userName") && reader.peek() != JsonToken.NULL) {
								username = reader.nextString();
							}
							else if (item.equals("comment") && reader.peek() != JsonToken.NULL) {
								text = reader.nextString();
							}
							else if (item.equals("commentDate") && reader.peek() != JsonToken.NULL) {
								date = reader.nextString();
							}
							else {
								reader.skipValue();
							}
						}
						comments.add(new Comment(username, text, format, date));
						reader.endObject();
					}
					reader.endArray();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		catch(Exception e) {
			Log.d(TAG, e.getLocalizedMessage());
		}
	}
	
	public void parseJson(JsonReader reader) throws IOException{
		reader.beginObject();
		while (reader.hasNext()) {
			String item = reader.nextName();
			if (item.equals("avgRating") && reader.peek() != JsonToken.NULL) {
				rating = reader.nextDouble();
			}
			else if (item.equals("name") && reader.peek() != JsonToken.NULL) {
				name = reader.nextString();
			}
			else if (item.equals("description") && reader.peek() != JsonToken.NULL) {
				description = reader.nextString();
			}
			else if (item.equals("flagStatus") && reader.peek() != JsonToken.NULL) {
				flagStatus = reader.nextString();
			}
			else if (item.equals("services") && reader.peek() != JsonToken.NULL) {
				parseServices(reader);
			}
			else if (item.equals("jellyFishes") && reader.peek() != JsonToken.NULL) {
				parseJellyFishes(reader);
			}
			else if (item.equals("beachRatingCount") && reader.peek() != JsonToken.NULL) {
				//TODO beach rating count, not even in layout
				reader.skipValue();
			}
			else if (item.equals("windSpeed") && reader.peek() != JsonToken.NULL) {
				windSpeed = reader.nextInt();
			}
			else if (item.equals("maxUV") && reader.peek() != JsonToken.NULL) {
				maxUV = reader.nextInt();
			}
			else if (item.equals("flagStatusUpdated") && reader.peek() != JsonToken.NULL) {
				//TODO flagStatusUpdated, not even in layout
				reader.skipValue();
			}
			else if (item.equals("jellyFishStatus") && reader.peek() != JsonToken.NULL) {
				jellyFishStatus = reader.nextString();
			}
			else if (item.equals("skyStatusCode") && reader.peek() != JsonToken.NULL) {
				skyStatusCode = reader.nextString();
			}
			else if (item.equals("maxTemperature") && reader.peek() != JsonToken.NULL) {
				maxTemperature = reader.nextInt();
			}
			else if (item.equals("minTemperature") && reader.peek() != JsonToken.NULL) {
				minTemperature = reader.nextInt();
			}
			else if (item.equals("windDirection") && reader.peek() != JsonToken.NULL) {
				windDirection = reader.nextString();
			}
			else if (item.equals("waterTemperature") && reader.peek() != JsonToken.NULL) {
				waterTemperature = reader.nextInt();
			}
			else if (item.equals("jellyFishStatusUpdated") && reader.peek() != JsonToken.NULL) {
				//TODO windDirection, not even in layout
				reader.skipValue();
			}
			else if (item.equals("feedbacks") && reader.peek() != JsonToken.NULL) {
				parseComments(reader);
				// Fake comments.
				//for(Comment c: MainApplication.sComments) {
				//	comments.add(c);
				//}
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
	}
}