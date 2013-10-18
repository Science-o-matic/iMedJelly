package com.science_o_matic.imedjelly.data;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

public interface JsonObject {
	void parseJson(JsonReader reader) throws IOException;
}