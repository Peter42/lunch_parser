package de.philipp1994.lunch.common;

import java.io.Serializable;

public class LunchMenuItem implements Serializable {
	
	private static final long serialVersionUID = -1796231509545846611L;
	private final String itemName;
	
	public LunchMenuItem(String itemName) {
		super();
		this.itemName = itemName;
	}
	
	public String getItemName() {
		return itemName;
	}

	@Override
	public String toString() {
		return "LunchMenuItem [itemName=" + itemName + "]";
	}

}
