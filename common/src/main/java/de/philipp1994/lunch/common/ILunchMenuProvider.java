package de.philipp1994.lunch.common;

import java.io.IOException;
import java.time.LocalDate;

public interface ILunchMenuProvider {
	public LunchMenu getMenu() throws IOException, LunchProviderException;
	public LunchMenu getMenu(LocalDate date) throws IOException, LunchProviderException;
}