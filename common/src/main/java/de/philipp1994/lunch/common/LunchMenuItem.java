package de.philipp1994.lunch.common;

import java.io.Serializable;

public class LunchMenuItem implements Serializable {
	
	private static final long serialVersionUID = -1796231509545846611L;
	private final String itemName, comment;
	private final double price;
	
	public static final double PRICE_UNKOWN = -1;

	public LunchMenuItem(String itemName, double price) {
		this.itemName = itemName;
		this.price = price;
		this.comment = null;
	}

	public LunchMenuItem(String itemName, double price, String comment) {
		this.itemName = itemName;
		this.price = price;
		this.comment = comment;
	}
	
	public String getItemName() {
		return itemName;
	}

	public double getPrice() {
		return price;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public String toString() {
		return "LunchMenuItem [itemName=" + itemName + ", comment=" + comment + ", price=" + price + "]";
	}

}
