package de.philipp1994.lunch.common;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.Preference;

public interface ILunchMenuProvider {
	public default List<LunchMenu> getMenu(IUserPreferences userPreferences) throws IOException, LunchProviderException {
		return this.getMenu(LocalDate.now(), userPreferences);
	}
	
	public default List<Preference<?>> getPreferences() {
		return Collections.emptyList();
	}

	public List<LunchMenu> getMenu(LocalDate date, IUserPreferences userPreferences) throws IOException, LunchProviderException;
	public UUID getUUID();
	public String getName();
}