package de.philipp1994.lunch.common;

import java.io.IOException;
import java.time.LocalDate;

public abstract class AbstractLunchMenuProvider implements ILunchMenuProvider {

	public LunchMenu getMenu() throws IOException, LunchProviderException {
		return this.getMenu(LocalDate.now());
	}

	public abstract LunchMenu getMenu(LocalDate date) throws IOException, LunchProviderException;

}
