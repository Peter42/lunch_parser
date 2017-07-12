package de.philipp1994.lunch.common.tools;

import de.philipp1994.lunch.common.LunchMenuItem;

public abstract class Utils {
	public static double parsePrice(String price) {
		price = price.replaceAll("[^0-9,\\.]", "").replace(",", ".").trim();
		if(price.length() == 0) {
			return LunchMenuItem.PRICE_UNKOWN;
		}
		
		return Double.parseDouble(price);
		
	}
}
