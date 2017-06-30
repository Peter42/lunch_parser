package de.philipp1994.lunch.common.prefs;

import java.util.Collections;
import java.util.Map;

public class EnumPreference extends Preference<String> {

	private final Map<String, String> values;
	
	public EnumPreference(String name, String key, Map<String, String> values, String defaultValue) {
		super(name, key, defaultValue);
		this.values = Collections.unmodifiableMap(values);
	}

	@Override
	public String parse(String value) {
		return value;
	}

	public Map<String, String> getValues() {
		return values;
	}

}
