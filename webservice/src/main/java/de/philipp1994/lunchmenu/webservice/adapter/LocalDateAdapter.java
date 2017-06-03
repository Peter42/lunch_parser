package de.philipp1994.lunchmenu.webservice.adapter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
	@Override
	public void write(JsonWriter out, LocalDate value) throws IOException {
		if (value == null) {
			out.nullValue();
		} else {
			out.value(value.toEpochDay() * 60 * 60 * 24);
		}
	}

	@Override
	public LocalDate read(JsonReader in) throws IOException {
		throw new UnsupportedOperationException();
		// The following is probably never going be needed in this project and therefore not tested and commented out
		/*
		if (in != null) {
			return Instant.ofEpochMilli(in.nextLong()).atZone(ZoneId.systemDefault()).toLocalDate();
		} else {
			return null;
		}
		*/
	}
}