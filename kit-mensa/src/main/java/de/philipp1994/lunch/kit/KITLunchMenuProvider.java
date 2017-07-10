package de.philipp1994.lunch.kit;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.EnumPreference;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.Preference;
import de.philipp1994.lunch.common.tools.Cache;

public class KITLunchMenuProvider implements ILunchMenuProvider {

	private static final Map<LocalDate, Map<String, List<LunchMenuItem>>> cache = Cache.getSynchronizedCache(7);
	private static final String MENSA_NAME = "KIT Mensa";
	
	private static final EnumPreference PREF_DISPLAY_MODE;
	private static final UUID KIT_MENSA_UUID =  UUID.fromString("997c3f16-3801-417f-88cb-50ab7f6cd8d1");

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EE dd.MM.", Locale.GERMANY);

	static {
		Map<String, String> displayModes = new HashMap<>();
		displayModes.put("1","All-in-one");
		displayModes.put("2","L6 Update separate");
		displayModes.put("n","Every Line separate");
		PREF_DISPLAY_MODE = new EnumPreference("KIT Mensa Displaymode", KIT_MENSA_UUID + ".displaymode", displayModes, "1");
	}

	@Override
	public List<Preference<?>> getPreferences() {
		List<Preference<?>> prefs = new LinkedList<>();
		prefs.add(PREF_DISPLAY_MODE);
		return prefs;
	}
	
	private static URI getURI(LocalDate date) throws IOException {
		try {
			return new URI("http://www.sw-ka.de/de/essen/?view=ok&STYLE=popup_plain&c=adenauerring&p=1&kw=" + date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
	
	private static URI getFallbackURI(LocalDate date) throws IOException {
		final String formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(date);
		try {
			return new URI("http://mensa.akk.uni-karlsruhe.de/?DATUM=" + formattedDate + "&uni=1&schnell=1");
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}
	
	private Map<String, List<LunchMenuItem>> getMenuForLines(final LocalDate date) throws IOException, LunchProviderException {
		if(cache.containsKey(date)){
			return cache.get(date);
		}

		try {
			Map<String, List<LunchMenuItem>> menu;
			
			DataInputStream in = new DataInputStream(getURI(date).toURL().openStream());
			Document document = Jsoup.parse(in, null, "");
			
			String expectedDateString = FORMATTER.format(date);
			
			Element table = document.select("#platocontent > h1").stream()
					.filter(e -> {
						return e.text().equals(expectedDateString);
					})
					.findAny()
					.orElseThrow(() -> LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET)
					.nextElementSibling();
			
			Map<String, Element> lines = table.select("tbody > tr").stream()
			.filter(tr -> tr.text().length() > 0)
			.filter(tr -> tr.child(0).text().startsWith("L"))
			.collect(Collectors.toMap(tr -> tr.child(0).text(), tr -> tr.child(1).child(0).child(0)));
			
			menu = lines.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
				return e.getValue().children().stream()
				.map(tr -> {
					try {
						double price = parsePrice(tr.child(2).text());
						if(price <= 1.5) {
							return null;
						}
						String name = tr.child(1).text().replaceAll("\\([^\\)]*\\)", "").trim();
						return new LunchMenuItem(name, price);
					}
					catch(IndexOutOfBoundsException ex) {
						// if line is closed
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
			}));
			
			cache.put(date, menu);
			
			return menu;
		}
		catch(Exception e) {
			e.printStackTrace();
			return getFallbackMenuForLines(date);
		}
	}

	@Override
	public List<LunchMenu> getMenu(final LocalDate date, final IUserPreferences preferences) throws IOException, LunchProviderException {
		
		String displayMode = preferences.getValueOrDefault(PREF_DISPLAY_MODE);
		
		Function<Entry<String, ?>, String> classifier;

		if(displayMode.equals("n")) {
			classifier = e -> " " + e.getKey();
		}
		else if(displayMode.equals("2")) {
			classifier = e -> e.getKey().contains("6") ? " L6 Update" : "";
		}
		else {
			classifier = e -> "";
		}
		
		List<LunchMenu> lunchMenus = new LinkedList<>();
		
		getMenuForLines(date).entrySet().stream()
		.collect(Collectors.groupingBy(classifier, Collectors.mapping(Entry::getValue, Collectors.toList())))
		.entrySet().stream().collect(Collectors.toMap( e -> e.getKey(),
				e -> {
					return e.getValue().stream().flatMap(List::stream).collect(Collectors.toList());
				}
		))
		.entrySet().forEach(e -> {
			LunchMenu lunchMenu = new LunchMenu(MENSA_NAME + e.getKey(), KIT_MENSA_UUID);
			e.getValue().forEach(lunchMenu::addLunchItem);
			lunchMenus.add(lunchMenu);
		});
		
		return lunchMenus;
	}
	
	private Map<String, List<LunchMenuItem>> getFallbackMenuForLines(final LocalDate date) throws IOException, LunchProviderException {
		
		Map<String, List<LunchMenuItem>> menu = new HashMap<>();
		
		DataInputStream in = new DataInputStream(getFallbackURI(date).toURL().openStream());

		Document document = Jsoup.parse(in, null, "");
		Element table = document.getElementsByTag("tbody").get(0);
		
		Element e = table.child(0);
		String currentLine = "";
		while( (e = e.nextElementSibling()) != null ) {
			String text = e.text();
			if(text.startsWith("Linie")) {
				currentLine = text.substring(0, text.length() - 1);
				menu.put(currentLine, new LinkedList<>());
			} else {
				menu.get(currentLine).add(new LunchMenuItem(e.child(0).text(), parsePrice(e.child(2).text())));
			}
		}
		
		if(menu.isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		
		cache.put(date, menu);
		
		return menu;
	}
	
	private double parsePrice(String price) {
		price = price.replaceAll("[^0-9,]", "").replace(",", ".").trim();
		if(price.length() == 0) {
			return LunchMenuItem.PRICE_UNKOWN;
		}
		
		return Double.parseDouble(price);
		
	}

	@Override
	public UUID getUUID() {
		return KIT_MENSA_UUID;
	}

	@Override
	public String getName() {
		return MENSA_NAME;
	}
}
