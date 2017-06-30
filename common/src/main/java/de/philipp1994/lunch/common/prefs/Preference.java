package de.philipp1994.lunch.common.prefs;

public abstract class Preference<T> {
	
	private final String name;
	private final String key;
	private final String type;
	private final T defaultValue;

	public Preference(String name, String key, T defaultValue) {
		String type = this.getClass().getSimpleName().replace(Preference.class.getSimpleName(), "").toLowerCase();
		this.name = name;
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	public Preference(String name, String key, T defaultValue, String type) {
		super();
		this.name = name;
		this.key = key;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}
	
	public String getType() {
		return type;
	}
	
	public abstract T parse(String value);
	public String save(T value) {
		return value.toString();
	}

	public T getDefaultValue() {
		return defaultValue;
	}
}
