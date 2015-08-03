package com.science_o_matic.imedjelly.data;

public class Table {
	public String mName;
	public String[] mFieldsName;
	public String[] mFieldsDefinition;

	public Table(String name, String[] fieldsName, String[] fieldsDefinition) {
		mName = name;
		mFieldsName = fieldsName;
		mFieldsDefinition = fieldsDefinition;
	}

	public String getPrimaryKey() {
		String result = null;
		for(int i=0; i<mFieldsDefinition.length; i++) {
			if(mFieldsDefinition[i].contains("primary key")) {
				result = mFieldsName[i];
			}
		}
		return result;
	}

	public int searchField(String name) {
		int result = -1;
		for (int i=0; result == -1 && i<mFieldsName.length; i++) {
			if (mFieldsName[i].equals(name)) {
				result = i;
			}
		}
		return result;
	}

	public static Table zone = new Table(
		"zones",
		new String[] {
			"zoneId",
			"name",
			"code",
			"predictionAvailable"
		},
		new String[] {
			"integer primary key",
			"text",
			"text",
			"integer"
		}
	);

	public static Table beach = new Table(
		"beach",
		new String[] {
			"id",
			"name",
			"latitude",
			"longitude",
			"jellyFishStatus",
			"municipalityName",
			"zoneId"
			
		},
		new String[] {
			"integer primary key",
			"text",
			"real",
			"real",
			"text",
			"text",
			"integer"
		}
	);
	
	public static Table prediction = new Table(
		"prediction",
		new String[] { 
			"zoneId", 
			"day", 
			"url", 
			"jellyFishName"
		}, 
		new String[] {
			"integer",
			"text",
			"text",
			"text"
		}
	);
}
