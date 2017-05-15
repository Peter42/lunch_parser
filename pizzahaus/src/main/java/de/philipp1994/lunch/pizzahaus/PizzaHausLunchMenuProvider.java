package de.philipp1994.lunch.pizzahaus;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import de.philipp1994.lunch.common.AbstractLunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;

public class PizzaHausLunchMenuProvider extends AbstractLunchMenuProvider {

	private static final URI URL;

	static {
		URI t = null;
		try {
			t = new URI("http://www.casinocatering.de/speiseplan/max-rubner-institut");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			URL = t;
		}
	}

	@Override
	public LunchMenu getMenu(final LocalDate date) throws LunchProviderException {
		
		LunchMenu menu = new LunchMenu("Pizzahaus");
		
		switch(date.getDayOfWeek()) {
		case MONDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta con Rocola, insalata verde"));
			menu.addLunchItem(new LunchMenuItem("Scaloppina al Vino Bianco, Pommes, insalata verde"));
			break;

		case TUESDAY:
			menu.addLunchItem(new LunchMenuItem("Agnolotti Ricotta e Spinaci, insalata verde"));
			menu.addLunchItem(new LunchMenuItem("Scaloppina con Mozzarella, Pommes, insalata verde"));
			break;

		case WEDNESDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta al Forno, insalata verde"));
			menu.addLunchItem(new LunchMenuItem("Scaloppina al Formaggio, Pommes, insalata verde"));
			break;

		case THURSDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta con Polpette, insalata verde"));
			menu.addLunchItem(new LunchMenuItem("Maiale alla Griglia, Pommes, insalata verde"));
			break;

		case FRIDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta al Salmone, insalata verde"));
			menu.addLunchItem(new LunchMenuItem("Kabeljaufilet, Pommes, insalata verde"));
			break;
			
		default:
			throw LunchProviderException.NO_LUNCH_TODAY;
		}
		
		return menu;
	}

}
