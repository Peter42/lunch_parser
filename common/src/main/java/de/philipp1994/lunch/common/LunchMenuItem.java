package de.philipp1994.lunch.common;

import java.io.Serializable;

public class LunchMenuItem implements Serializable {
	
	private static final long serialVersionUID = -1796231509545846611L;
	private final String itemName;
	private final double price;
	
	public static final double PRICE_UNKOWN = -1;

	public LunchMenuItem(String itemName, double price) {
		super();
		this.itemName = itemName;
		this.price = price;
	}
	
	public String getItemName() {
		return itemName;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "LunchMenuItem [itemName=" + itemName + ", price=" + price + "]";
	}
}
