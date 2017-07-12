package de.philipp1994.lunch.oxford_cafe;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchMenuItem;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.UserPreferences;
import de.philipp1994.lunch.common.tools.Cache;
import de.philipp1994.lunch.common.tools.Utils;

public class OxfordCafeLunchMenuProvider implements ILunchMenuProvider {
	private static final UUID PROVIDER_UUID = UUID.fromString("60fb4548-1eeb-4c40-a46b-641ac82da651");
	private static final URI URL;
	static {
		URI t = null;
		try {
			t = new URI("http://oxford-cafe.de/tageskarte/");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {
			URL = t;
		}
	}
	
	private final ScheduledExecutorService scheduler;
	
	public OxfordCafeLunchMenuProvider() {
		scheduler = Executors.newScheduledThreadPool(1, runnable -> {
			Thread runner = new Thread(runnable);
			runner.setDaemon(true);
			return runner;
		});
		
		scheduler.scheduleAtFixedRate(() -> {
			try {
				tryLoad();
			} catch (Exception e) {
				System.out.println("Could not load Oxford Menu:");
				e.printStackTrace(System.out);
			}
		}, 0, 1, TimeUnit.HOURS);
	}
	
	private static final int[] ROW = new int[]{1100, 1500, 1900};
	private static final int[] COL = new int[]{110, 850};
	private static final Map<LocalDate, LunchMenu> cache = Cache.getSynchronizedCache(7);
	
	private void tryLoad() throws MalformedURLException, IOException, LunchProviderException {
		// FIXME: check if data is correct in image
		LocalDate now = LocalDate.now();
		
		if(cache.containsKey(now.with(DayOfWeek.MONDAY))) {
			return;
		}
		
		DataInputStream in = new DataInputStream(URL.toURL().openStream());
		Document document = Jsoup.parse(in, null, "");
		
		String imgSrc = document.select("#post-218 > div.fusion-flexslider.flexslider.post-slideshow > ul.slides > li > a > img").stream()
		.findAny()
		.orElseThrow(() -> LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET ).attr("src");
		
		BufferedImage inputImage = ImageIO.read(new URL(imgSrc).openStream());
		
		
		final double SIZE_FAKTOR = inputImage.getHeight() / 2384.0;
		System.out.println("SIZE_FAKTOR: " + SIZE_FAKTOR);
		OCR stringOCR = new OCR(new Font("Abel", 0, 100), OCR.CHARS_STRING, SIZE_FAKTOR);
		OCR priceOCR = new OCR(new Font("Abel", 0, 100), OCR.CHARS_PRICE, SIZE_FAKTOR);
		
		process(stringOCR, priceOCR, inputImage, COL[0], ROW[0], SIZE_FAKTOR, now.with(DayOfWeek.MONDAY));
		process(stringOCR, priceOCR, inputImage, COL[0], ROW[1], SIZE_FAKTOR, now.with(DayOfWeek.TUESDAY));
		process(stringOCR, priceOCR, inputImage, COL[0], ROW[2], SIZE_FAKTOR, now.with(DayOfWeek.WEDNESDAY));
		process(stringOCR, priceOCR, inputImage, COL[1], ROW[0], SIZE_FAKTOR, now.with(DayOfWeek.THURSDAY));
		process(stringOCR, priceOCR, inputImage, COL[1], ROW[1], SIZE_FAKTOR, now.with(DayOfWeek.FRIDAY));
	}
	
	private void process(OCR stringOCR, OCR priceOCR, BufferedImage image, int x, int y, final double SIZE_FAKTOR, LocalDate date){
		LunchMenu menu = new LunchMenu(getName(), getUUID());

		List<String> names  = process(stringOCR, image.getSubimage((int)(x       * SIZE_FAKTOR), (int)(y * SIZE_FAKTOR), (int)(600 * SIZE_FAKTOR), (int)(230 * SIZE_FAKTOR)));
		List<String> prices = process(priceOCR , image.getSubimage((int)(x + 610 * SIZE_FAKTOR), (int)(y * SIZE_FAKTOR), (int)(100 * SIZE_FAKTOR), (int)(230 * SIZE_FAKTOR)));
		
		boolean priceAvailable = names.size() == prices.size();
		for(int i = 0; i < names.size(); ++i) {
			menu.addLunchItem(new LunchMenuItem(names.get(i), priceAvailable ? Utils.parsePrice( prices.get(i) ) : LunchMenuItem.PRICE_UNKOWN ));
		}
		
		cache.put(date, menu);
	}
	
	private List<String> process(OCR ocr, BufferedImage subimage) {
		prepare(subimage);
		return ocr.detect(subimage);
	}

	private static void prepare(BufferedImage image) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgba = image.getRGB(x, y);
				Color col = new Color(rgba);
				if((col.getRed() + col.getBlue() + col.getGreen())/3 > 80) {
					col = Color.BLACK;
				}
				else {
					col = Color.WHITE;
				}
				
				image.setRGB(x, y, col.getRGB());
			}
		}
	}
	
	@Override
	public List<LunchMenu> getMenu(LocalDate date, IUserPreferences userPreferences) throws IOException, LunchProviderException {
		if(cache.containsKey(date)) {
			return Collections.singletonList(cache.get(date));
		}
		else {
			// FIXME: No lunch today on weekends
			throw LunchProviderException.LUNCH_MENU_NOT_AVAILABLE_YET;
		}
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
		try {
			OxfordCafeLunchMenuProvider provider = new OxfordCafeLunchMenuProvider();
			try {
				System.out.println(provider.getMenu(UserPreferences.EMPTY).get(0).getLunchItems());
			} catch (LunchProviderException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println(provider.getMenu(UserPreferences.EMPTY).get(0).getLunchItems());
			} catch (LunchProviderException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(180000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
