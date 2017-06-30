package de.philipp1994.lunch.pizzahaus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.IUserPreferences;

public class PizzaHausLunchMenuProvider implements ILunchMenuProvider {

	@Override
	public List<LunchMenu> getMenu(final LocalDate date, final IUserPreferences preferences) throws LunchProviderException {
		
		LunchMenu menu = new LunchMenu("Pizzahaus", this.getUUID());
		
		switch(date.getDayOfWeek()) {
		case MONDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta con Rocola, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem("Scaloppina al Vino Bianco, Pommes, insalata verde", 8.00));
			break;

		case TUESDAY:
			menu.addLunchItem(new LunchMenuItem("Agnolotti Ricotta e Spinaci, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem("Scaloppina con Mozzarella, Pommes, insalata verde", 8.00));
			break;

		case WEDNESDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta al Forno, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem("Scaloppina al Formaggio, Pommes, insalata verde", 8.00));
			break;

		case THURSDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta con Polpette, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem("Maiale alla Griglia, Pommes, insalata verde", 8.00));
			break;

		case FRIDAY:
			menu.addLunchItem(new LunchMenuItem("Pasta al Salmone, insalata verde", 5.50));
			menu.addLunchItem(new LunchMenuItem("Kabeljaufilet, Pommes, insalata verde", 8.00));
			break;
			
		default:
			throw LunchProviderException.NO_LUNCH_TODAY;
		}
		
		return Collections.singletonList(menu);
	}

	@Override
	public UUID getUUID() {
		return UUID.fromString("3f65d5f0-adb9-476c-b4a4-11069927b375");
	}

	@Override
	public String getName() {
		return "Pizzahaus";
	}
	
}
