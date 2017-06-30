package de.philipp1994.lunchmenu.webservice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.common.prefs.BooleanPreference;
import de.philipp1994.lunch.common.prefs.IUserPreferences;
import de.philipp1994.lunch.common.prefs.Preference;
import de.philipp1994.lunch.common.prefs.UserPreferences;
import de.philipp1994.lunch.kit.KITLunchMenuProvider;
import de.philipp1994.lunch.mri.MRILunchMenuProvider;
import de.philipp1994.lunch.pizzahaus.PizzaHausLunchMenuProvider;
import de.philipp1994.lunchmenu.webservice.adapter.LocalDateAdapter;
import de.philipp1994.lunchmenu.webservice.adapter.LocalDateTimeAdapter;

public class LunchMenuServlet extends HttpServlet {

	private static final long serialVersionUID = 2531284175314743260L;
	
	private static final Type TYPE_HASHMAP_STRING_STRING = new TypeToken<HashMap<String, String>>(){}.getType();
	
	private final LinkedList<Preference<?>> preferences;
	
	private final Gson gson;
	public LunchMenuServlet() {
		gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		
		preferences = new LinkedList<>();
		
		preferences.addAll(getPreferences());
		
		for(int i = 0; i < PROVIDER.length; ++i ) {
			final int currentI = i;
			preferences.addAll(
				PROVIDER[i].getPreferences().stream()
					.filter(pref -> pref.getKey().startsWith(PROVIDER[currentI].getUUID().toString() ))
					.collect(Collectors.toList())
			);
		}
	}
	
	private static class Result {
		LinkedList<LunchMenu> menus;
		
		@SuppressWarnings("unused") // It is serialized
		LocalDateTime generationTime;
		
		LocalDate menuForDay;
	}
	
	private final static ILunchMenuProvider[] PROVIDER = new ILunchMenuProvider[]{
			new MRILunchMenuProvider(),
			new KITLunchMenuProvider(),
			new PizzaHausLunchMenuProvider(),
	};
	
	private BooleanPreference[] providerPreferences;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getPathInfo().equals("/preferences")) {
			doGetPreferences(request, response);
		}
		else {
			doGetLunchMenu(request, response);
		}
	}
	
	private List<Preference<?>> getPreferences() {
		List<Preference<?>> prefs = new LinkedList<>();
		
		providerPreferences = new BooleanPreference[PROVIDER.length];
		
		for(int i = 0; i < PROVIDER.length; ++i ) {
			ILunchMenuProvider provider = PROVIDER[i];
			providerPreferences[i] = new BooleanPreference("Show " + provider.getName(), "root.show." + provider.getUUID(), true);
			prefs.add(providerPreferences[i]);
		}
		
		return prefs;
	}
	
	private void doGetPreferences(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(gson.toJson(preferences));
	}
	
	private IUserPreferences parsePreferences(String header) {
		if(header == null) {
			return UserPreferences.EMPTY;
		}
		HashMap<String, String> userPreferences = 
				gson.fromJson(new String(Base64.getDecoder().decode(header.getBytes())), TYPE_HASHMAP_STRING_STRING);
		
		return new UserPreferences(userPreferences);
	}
	
	private void doGetLunchMenu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Result result = new Result();
		result.menus = new LinkedList<>();
		
		IUserPreferences userPreferences = parsePreferences(request.getHeader("X-User-Preferences"));
		
		result.menuForDay = LocalDate.now();
		result.generationTime = LocalDateTime.now();
		
		if(LocalDateTime.now().getHour() >= 15 ) {
			result.menuForDay = result.menuForDay.plusDays(1);
		}
		if(result.menuForDay.getDayOfWeek().getValue() > DayOfWeek.FRIDAY.getValue()) {
			result.menuForDay = result.menuForDay.plusDays( 1 + DayOfWeek.SUNDAY.getValue() - result.menuForDay.getDayOfWeek().getValue());
		}
		
		for(int i = 0; i < PROVIDER.length; ++i ) {
			if(!userPreferences.getValueOrDefault(providerPreferences[i]))
				continue;
			try {
				result.menus.addAll(PROVIDER[i].getMenu(result.menuForDay, userPreferences));
			} catch (LunchProviderException | IOException e) {
				e.printStackTrace();
			}
		}
		
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(gson.toJson(result));
	}

}
