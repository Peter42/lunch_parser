package de.philipp1994.lunchmenu.webservice.adapter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
	@Override
	public void write(JsonWriter out, LocalDateTime value) throws IOException {
		if (value == null) {
			out.nullValue();
		} else {
			out.value(value.atZone(ZoneOffset.systemDefault()).toEpochSecond());
		}
	}

	@Override
	public LocalDateTime read(JsonReader in) throws IOException {
		throw new UnsupportedOperationException();
		// The following is probably never going be needed in this project and therefore not tested and commented out
		/*if (in != null) {
			return Instant.ofEpochMilli(in.nextLong()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		} else {
			return null;
		}*/
	}
}