package de.philipp1994.lunch.common.prefs;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserPreferences implements IUserPreferences {
	
	public static final UserPreferences EMPTY = new UserPreferences(Collections.emptyMap());
	
	private final Map<String, String> preferenceValues;
	
	public UserPreferences(Map<String, String> preferenceValues) {
		this.preferenceValues = Collections.unmodifiableMap(preferenceValues);
	}

	@Override
	public <T> T getValue(Preference<T> preference) throws ClassCastException, NoSuchElementException {
		if(this.preferenceValues.containsKey(preference.getKey())) {
			return preference.parse(this.preferenceValues.get(preference.getKey()));
		}
		else {
			throw new NoSuchElementException();
		}
	}

}
