package de.philipp1994.lunch.common.tools;

import java.time.DayOfWeek;
import java.time.LocalDate;

import de.philipp1994.lunch.common.LunchMenuItem;

public abstract class Utils {
	public static double parsePrice(String price) {
		price = price.replaceAll("[^0-9,\\.]", "").replace(",", ".").trim();
		if(price.length() == 0) {
			return LunchMenuItem.PRICE_UNKOWN;
		}
		
		return Double.parseDouble(price);
		
	}
	
	public static boolean isWeekend(LocalDate date){
		return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
	}
}
