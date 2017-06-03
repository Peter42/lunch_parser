package de.philipp1994.lunchmenu.webservice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.mri.MRILunchMenuProvider;
import de.philipp1994.lunch.pizzahaus.PizzaHausLunchMenuProvider;
import de.philipp1994.lunchmenu.webservice.adapter.LocalDateAdapter;
import de.philipp1994.lunchmenu.webservice.adapter.LocalDateTimeAdapter;

public class LunchMenuServlet extends HttpServlet {

	private static final long serialVersionUID = 2531284175314743260L;
	
	private final Gson gson;
	public LunchMenuServlet() {
		gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
	}
	
	private static class Result {
		LunchMenu[] menus;
		
		@SuppressWarnings("unused") // It is serialized
		LocalDateTime generationTime;
		
		LocalDate menuForDay;
	}
	
	private final static ILunchMenuProvider[] PROVIDER = new ILunchMenuProvider[]{ new MRILunchMenuProvider(), new PizzaHausLunchMenuProvider() };
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Result result = new Result();
		result.menus = new LunchMenu[PROVIDER.length];
		
		result.menuForDay = LocalDate.now();
		result.generationTime = LocalDateTime.now();
		
		if(LocalDateTime.now().getHour() >= 15 ) {
			// TODO: skip the weekend
			result.menuForDay = result.menuForDay.plusDays(1);
		}
		
		for(int i = 0; i < PROVIDER.length; ++i ) {
			try {
				result.menus[i] = PROVIDER[i].getMenu(result.menuForDay);
			} catch (LunchProviderException | IOException e) {
				e.printStackTrace();
			}
		}
		
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(gson.toJson(result));
	}

}
