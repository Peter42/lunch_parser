package de.philipp1994.lunchmenu.webservice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import de.philipp1994.lunch.common.ILunchMenuProvider;
import de.philipp1994.lunch.common.LunchMenu;
import de.philipp1994.lunch.common.LunchProviderException;
import de.philipp1994.lunch.mri.MRILunchMenuProvider;
import de.philipp1994.lunch.pizzahaus.PizzaHausLunchMenuProvider;

public class LunchMenuServlet extends HttpServlet {
	
	private final static ILunchMenuProvider[] PROVIDER = new ILunchMenuProvider[]{ new MRILunchMenuProvider(), new PizzaHausLunchMenuProvider() };
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LunchMenu[] menus = new LunchMenu[PROVIDER.length];
		
		LocalDate date = LocalDate.now();
		if(LocalDateTime.now().getHour() >= 15 ) {
			date = date.plusDays(1);
		}
		
		for(int i = 0; i < PROVIDER.length; ++i ) {
			try {
				menus[i] = PROVIDER[i].getMenu(date);
			} catch (LunchProviderException | IOException e) {
				e.printStackTrace();
			}
		}

		response.setContentType("application/json; charset=utf-8");
		response.getWriter().write(new Gson().toJson(menus));
	}

}
