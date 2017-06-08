package de.philipp1994.lunch.kit;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.philipp1994.lunch.common.AbstractLunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.tools.Cache;

public class KITLunchMenuProvider extends AbstractLunchMenuProvider {

	private static final URI FALLBACK_URL;
	private static final URI URL;
	private static final Map<LocalDate, LunchMenu> cache = Cache.getSynchronizedCache(7);
	private static final String MENSA_NAME = "KIT Mensa";

	static {
		URI t = null;
		try {
			t = new URI("http://mensa.akk.uni-karlsruhe.de/?DATUM=heute&uni=1&schnell=1");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			FALLBACK_URL = t;
		}
		
		t = null;
		try {
			t = new URI("http://www.sw-ka.de/de/essen/?view=ok&STYLE=popup_plain&c=adenauerring&p=1");
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
		
		try {
			LunchMenu menu = new LunchMenu(MENSA_NAME);
			
			DataInputStream in = new DataInputStream(URL.toURL().openStream());

			Document document = Jsoup.parse(in, null, "");
			
			Map<String, Element> lines = document.select("#platocontent > table:nth-child(5) > tbody > tr").stream()
			.filter(tr -> tr.text().length() > 0)
			.filter(tr -> tr.child(0).text().startsWith("L"))
			.collect(Collectors.toMap(tr -> tr.child(0).text(), tr -> tr.child(1).child(0).child(0)));
			
			lines.entrySet().stream().flatMap(e -> {
				return e.getValue().children().stream()
						.filter(tr -> {
							final String text = tr.child(2).text().replace("€", "").replace(",", ".").trim();
							if(text.length() == 0) {
								return false;
							}
							return Double.parseDouble(text) > 1.5;
						})
						.map(tr -> tr.child(1).text())
						.map(name -> name.replaceAll("\\([^\\)]*\\)", "").trim())
						.map(name -> new LunchMenuItem(name));
				
			}).forEach(menu::addLunchItem);
			
			return menu;
		}
		catch(Exception e) {
			e.printStackTrace();
			return getFallbackMenu(date);
		}
		
	}
	
	private LunchMenu getFallbackMenu(final LocalDate date) throws IOException, LunchProviderException {
		
		LunchMenu menu = new LunchMenu(MENSA_NAME);
		
		DataInputStream in = new DataInputStream(FALLBACK_URL.toURL().openStream());

		Document document = Jsoup.parse(in, null, "");
		Element table = document.getElementsByTag("tbody").get(0);
		
		Element e = table.child(0);
		String currentLine = "";
		while( (e = e.nextElementSibling()) != null ) {
			String text = e.text();
			if(text.startsWith("Linie")) {
				currentLine = text.substring(0, text.length() - 1);
			} else {
				menu.addLunchItem(new LunchMenuItem(e.child(0).text()));
			}
		}
		
		if(menu.getLunchItems().isEmpty()) {
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
		
		cache.put(date, menu);
		
		return menu;
	}

}
