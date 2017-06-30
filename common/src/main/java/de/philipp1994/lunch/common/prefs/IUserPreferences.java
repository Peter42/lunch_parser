package de.philipp1994.lunch.common.prefs;

import java.util.NoSuchElementException;

public interface IUserPreferences {
	
	public <T> T getValue(Preference<T> preference) throws ClassCastException, NoSuchElementException;

	public default <T> T getValueOrDefault(Preference<T> preference) throws ClassCastException {
		try {
			return this.getValue(preference);
		}
		catch (NoSuchElementException e) {
			return preference.getDefaultValue();
		}
	}
	
	public default <T> T getValueOrDefault(Preference<T> preference, T defaultValue) throws ClassCastException {
		try {
			return this.getValue(preference);
		}
		catch (NoSuchElementException e) {
			return defaultValue;
		}
	}
}
