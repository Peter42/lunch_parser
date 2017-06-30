package de.philipp1994.lunch.common.prefs;

public class BooleanPreference extends Preference<Boolean> {

	public BooleanPreference(String name, String key, boolean defaultValue) {
		super(name, key, defaultValue);
	}

	@Override
	public Boolean parse(String value) {
		return Boolean.parseBoolean(value);
	}

}
