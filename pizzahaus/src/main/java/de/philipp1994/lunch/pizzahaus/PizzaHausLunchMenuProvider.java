package de.philipp1994.lunch.pizzahaus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.EnumPreference;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.Preference;

public class PizzaHausLunchMenuProvider implements ILunchMenuProvider {
	
	private static final Preference<?> PREF_LANGUAGE;
	private static final UUID PIZZA_HAUS_UUID = UUID.fromString("3f65d5f0-adb9-476c-b4a4-11069927b375");
	
	static {
		Map<String, String> languages = new HashMap<>();
		languages.put("it","Italienisch (original)");
		languages.put("de","Deutsch (übersetzt)");
		PREF_LANGUAGE = new EnumPreference("Pizza Haus Sprache", PIZZA_HAUS_UUID + ".lang", languages, "de");
	}

	@Override
	public List<Preference<?>> getPreferences() {
		List<Preference<?>> prefs = new LinkedList<>();
		prefs.add(PREF_LANGUAGE);
		return prefs;
	}
	

	@Override
	public List<LunchMenu> getMenu(final LocalDate date, final IUserPreferences preferences) throws LunchProviderException {
		
		LunchMenu menu = new LunchMenu("Pizzahaus", this.getUUID());
		
		boolean translated = preferences.getValueOrDefault(PREF_LANGUAGE).equals("de");
		
		switch(date.getDayOfWeek()) {
		case MONDAY:
			menu.addLunchItem(new LunchMenuItem(translated ? "Pasta mit Rucola und grünem Salat" : "Pasta con Rucola, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem(translated ? "Kalbsschnitzel mit Weißwein dazu Pommes und grüner Salat" : "Scaloppina al Vino Bianco, Pommes, insalata verde", 8.00));
			break;

		case TUESDAY:
			menu.addLunchItem(new LunchMenuItem(translated ? "Ravioli Ricotta und Spinat mit grünem Salat" : "Agnolotti Ricotta e Spinaci, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem(translated ? "Kalbsschnitzel mit Mozzarella dazu Pommes und grüner Salat" : "Scaloppina con Mozzarella, Pommes, insalata verde", 8.00));
			break;

		case WEDNESDAY:
			menu.addLunchItem(new LunchMenuItem(translated ? "Überbackene Nudeln mit grünem Salat" : "Pasta al Forno, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem(translated ? "Kalbsschnitzel mit Käse dazu Pommes und grüner Salat" : "Scaloppina al Formaggio, Pommes, insalata verde", 8.00));
			break;

		case THURSDAY:
			menu.addLunchItem(new LunchMenuItem(translated ? "Nudeln mit Fleischbällchen dazu grüner Salat" : "Pasta con Polpette, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem(translated ? "Schweinefleisch vom Grill dazu Pommes und grüner Salat" : "Maiale alla Griglia, Pommes, insalata verde", 8.00));
			break;

		case FRIDAY:
			menu.addLunchItem(new LunchMenuItem(translated ? "Lachsnudeln mit grünem Salat" : "Pasta al Salmone, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem(translated ? "Kabeljaufilet mit Pommes und grünem Salat" : "Kabeljaufilet, Pommes, insalata verde", 8.00));
			break;
			
		default:
			throw LunchProviderException.NO_LUNCH_TODAY;
		}
		
		return Collections.singletonList(menu);
	}

	@Override
	public UUID getUUID() {
		return PIZZA_HAUS_UUID;
	}

	@Override
	public String getName() {
		return "Pizzahaus";
	}
	
}
