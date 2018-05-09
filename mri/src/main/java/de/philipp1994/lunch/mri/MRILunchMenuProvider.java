package de.philipp1994.lunch.mri;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.tools.MaxAgeCache;

public class MRILunchMenuProvider implements ILunchMenuProvider {

	private static final URI URL;
	private static final EditDistance<Integer> distance = new LevenshteinDistance();
	private static final String SUPPE_AND_DESSERT = "Suppe & Dessert";
	private static final Pattern PRICE_PATTERN = Pattern.compile("([0-9,.]+) *€$");

	private final MaxAgeCache<LocalDate, LunchMenu> cache = new MaxAgeCache<>(30, TimeUnit.MINUTES, this::fetchMenu);

	static {
		URI t = null;
		try {
			t = new URI("https://www.casinocatering.de/speiseplan/max-rubner-institut");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			URL = t;
		}
	}
	
	private LunchMenu fetchMenu(final LocalDate date) {
		LunchMenu menu = new LunchMenu("MRI", this.getUUID());

		Document document;
		try {
			DataInputStream in = new DataInputStream(URL.toURL().openStream());
			document = Jsoup.parse(in, null, "");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		List<String> menuItemNames = document.select(".tagesplan-liste-user > div > div > div > div > ul").stream()
			.filter(node -> {
				LocalDate nodeDate = LocalDate.parse(node.previousElementSibling().child(0).attr("content"), DateTimeFormatter.ISO_DATE_TIME);
				return nodeDate.compareTo(date) == 0;
			})
			.flatMap(node -> node.getElementsByTag("p").stream())
			.filter(element -> !element.text().matches("^(\\s|\\h|\\v)*$"))
			.sequential()
			.filter(new Predicate<Element>() {
				boolean found = false;
				@Override
				public boolean test(Element element) {
					if(found) {
						return false;
					}
					found = isSuppeAndDessert(element.text());
					return !found;
				}
			})
			.map(element -> element.toString()
					.replaceAll("<su[bp]>[^<]*</su[bp]>", " ")
					.replaceAll("</?[A-z]*>", "")
					.replaceAll("&nbsp;", " ")
					.replaceAll(" +", " ")
					.trim()
			).collect(Collectors.toList());
		
		for(int i = 0; i < menuItemNames.size(); ++i) {
			double price = LunchMenuItem.PRICE_UNKOWN;
			String name = menuItemNames.get(i);
			
			switch (i) {
			case 0:
				price = 4.90;
				break;
			case 1:
				price = 5.40;
				break;
			}
			
			Matcher priceMatcher = PRICE_PATTERN.matcher(name);
			if(priceMatcher.find()) {
				price = Double.parseDouble(priceMatcher.group(1).replace(',', '.'));
				name = name.substring(0, priceMatcher.start());
			}
			
			menu.addLunchItem(new LunchMenuItem(name.trim(), price));
		}
		
		return menu;
	}

	@Override
	public List<LunchMenu> getMenu(final LocalDate date, final IUserPreferences preferences) throws IOException, LunchProviderException {
		LunchMenu menu = this.cache.get(date);
		if(menu == null || menu.getLunchItems().isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		return Collections.singletonList(menu);
	}
	
	private boolean isSuppeAndDessert(String text) {
		final int end = Math.min(text.length(), SUPPE_AND_DESSERT.length());
		return distance.apply(SUPPE_AND_DESSERT, text.substring(0, end)) < 2;
	}

	@Override
	public UUID getUUID() {
		return UUID.fromString("64f56b8d-1fb2-4a22-82b2-0025a3c87433");
	}

	@Override
	public String getName() {
		return "MRI";
	}

}
