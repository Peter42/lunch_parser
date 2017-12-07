package de.philipp1994.lunch.mri;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import de.philipp1994.lunch.common.tools.Cache;

public class MRILunchMenuProvider implements ILunchMenuProvider {

	private static final URI URL;
	private static final Map<LocalDate, LunchMenu> cache = Cache.getSynchronizedCache(7);
	
	private static final EditDistance<Integer> distance = new LevenshteinDistance();
	private static final String SUPPE_AND_DESSERT = "Suppe & Dessert";

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
	public List<LunchMenu> getMenu(final LocalDate date, final IUserPreferences preferences) throws IOException, LunchProviderException {
		
		if(cache.containsKey(date)){
			return Collections.singletonList(cache.get(date));
		}
		
		LunchMenu menu = new LunchMenu("MRI", this.getUUID());
		
		DataInputStream in = new DataInputStream(URL.toURL().openStream());

		Document document = Jsoup.parse(in, null, "");
		
		document.select(".tagesplan-liste-user > div > div > div > div > ul").stream()
			.filter(node -> {
				LocalDate nodeDate = LocalDate.parse(node.previousElementSibling().child(0).attr("content"), DateTimeFormatter.ISO_DATE_TIME);
				return nodeDate.compareTo(date) == 0;
			})
			.forEach(node -> {
				for (Element element : node.getElementsByTag("p")) {
					if (isSuppeAndDessert(element.text())) {
						break;
					}
					if(element.text().matches("^\\s*$")) {
						continue;
					}
					menu.addLunchItem(new LunchMenuItem(element.toString()
						.replaceAll("<su[bp]>[^<]*</su[bp]>", " ")
						.replaceAll("</?p>", "")
						.replaceAll(" +", " "), LunchMenuItem.PRICE_UNKOWN));
				}
			}
		);
		
		if(menu.getLunchItems().isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		
		cache.put(date, menu);
		
		return Collections.singletonList(menu);
	}
	
	private boolean isSuppeAndDessert(String text) {
		final int end = Math.min(text.length(), SUPPE_AND_DESSERT.length()) - 1;
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
