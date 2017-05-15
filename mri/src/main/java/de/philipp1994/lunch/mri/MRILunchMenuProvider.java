package de.philipp1994.lunch.mri;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.philipp1994.lunch.common.AbstractLunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.tools.Cache;

public class MRILunchMenuProvider extends AbstractLunchMenuProvider {

	private static final URI URL;
	private static final Map<LocalDate, LunchMenu> cache = Cache.getSynchronizedCache(7);

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
	public LunchMenu getMenu(final LocalDate date) throws IOException, LunchProviderException {
		
		if(cache.containsKey(date)){
			return cache.get(date);
		}
		
		LunchMenu menu = new LunchMenu();
		
		DataInputStream in = new DataInputStream(URL.toURL().openStream());

		Document document = Jsoup.parse(in, null, "");
		document.getElementsByClass("view-content").last().getElementsByClass("item-list").stream()
				.filter(node -> {
					LocalDate nodeDate = LocalDate.parse(node.child(1).child(0).attr("content"), DateTimeFormatter.ISO_DATE_TIME);
					return nodeDate.compareTo(date) == 0;
				})
				.forEach(node -> {
					for (Element element : node.child(2).children()) {
						if (!element.text().startsWith("Suppe & Dessert")) {
							Element p = element.getElementsByTag("p").first();
							menu.add(new LunchMenuItem(p.toString().replaceAll("<su[bp]>[^<]*</su[bp]>", " ").replaceAll("</?p>", "").replaceAll(" +", " ")));
						}
					}
				});
		
		if(menu.isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		
		cache.put(date, menu);
		
		return menu;
	}

}
