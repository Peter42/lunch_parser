package de.philipp1994.lunch.common;

import java.io.IOException;
import java.time.LocalDate;

public interface ILunchMenuProvider {
	public default LunchMenu getMenu() throws IOException, LunchProviderException {
		return this.getMenu(LocalDate.now());
	}

	public LunchMenu getMenu(LocalDate date) throws IOException, LunchProviderException;
}