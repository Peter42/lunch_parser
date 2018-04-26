package de.philipp1994.lunch.oxford_cafe;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.UserPreferences;
import de.philipp1994.lunch.common.tools.MaxAgeCache;

public class OxfordCafeLunchMenuProvider implements ILunchMenuProvider {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.GERMAN); // e.g. Mittwoch, 25.04.2018
	private static final UUID PROVIDER_UUID = UUID.fromString("60fb4548-1eeb-4c40-a46b-641ac82da651");
	private static final URI URL;
	static {
		URI t = null;
		try {
			t = new URI("https://channel.gastro-stratege.ch/web/mittagstisch.php?l=338bd8841d967da9858f2518dead2242");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			URL = t;
		}
	}

	private final MaxAgeCache<LocalDate, LunchMenu> cache = new MaxAgeCache<>(30, TimeUnit.MINUTES, this::fetchMenu);

	private LunchMenu fetchMenu(final LocalDate date) {
		LunchMenu menu = new LunchMenu(getName(), this.getUUID());

		Document document;
		try {
			DataInputStream in = new DataInputStream(URL.toURL().openStream());
			document = Jsoup.parse(in, null, "");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		document.select("#accordion > div").stream().filter(node -> {
			LocalDate nodeDate = LocalDate.parse(node.getElementsByClass("category-title").text(), DATE_FORMAT);
			return nodeDate.compareTo(date) == 0;
		})
		.flatMap(n -> n.getElementsByClass("fooditem").stream())
		.map(n -> {
			double price = Double.parseDouble(n.getElementsByClass("fooditem_price").text().split(" ")[0].replace(',', '.'));
			String name = ItemNameNormalizer.normalize(n.getElementsByClass("fooditem_title").text());
			return new LunchMenuItem(name, price);
		})
		.forEach(menu::addLunchItem);

		return menu.getLunchItems().isEmpty() ? null : menu;
	}

	@Override
	public List<LunchMenu> getMenu(LocalDate date, IUserPreferences userPreferences) throws IOException, LunchProviderException {
		LunchMenu menu = this.cache.get(date);
		if (menu == null || menu.getLunchItems().isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		return Collections.singletonList(menu);
	}

	@Override
	public UUID getUUID() {
		return PROVIDER_UUID;
	}

	@Override
	public String getName() {
		return "Oxford Cafe - Beta";
	}

	public static void main(String[] args) {
		OxfordCafeLunchMenuProvider provider = new OxfordCafeLunchMenuProvider();
		try {
			System.out.println(provider.getMenu(UserPreferences.EMPTY).get(0).getLunchItems());
		} catch (IOException | LunchProviderException e1) {
			e1.printStackTrace();
		}
	}

}
