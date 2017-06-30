package de.philipp1994.lunchmenu.webservice;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class LunchMenuWebserviceLauncher {

	private static int port = 8080;

	private static boolean parseOptions(String[] args) {

		Options options = new Options();
		options.addOption(Option.builder("p").hasArg(true).optionalArg(false).desc("Port for httpd").longOpt("port").argName("port").type(Number.class).build());

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("p")) {
				port = ((Number) cmd.getParsedOptionValue("p")).intValue();
				if (port < 0 || port > 65535) {
					throw new ParseException("Port out of range");
				}
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			// FIXME:
			formatter.printHelp(Integer.MAX_VALUE, "java " + System.getProperty("sun.java.command", LunchMenuWebserviceLauncher.class.getName()), "", options, "", true);
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws Exception {
		if (!parseOptions(args)) {
			return;
		}

		// http://stackoverflow.com/a/28735121
		// https://www.eclipse.org/jetty/documentation/9.3.x/embedded-examples.html
		
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.setConnectors(new Connector[] { connector });

		ServletContextHandler ctx = new ServletContextHandler();
		ctx.setContextPath("/");

		DefaultServlet defaultServlet = new DefaultServlet();
		ServletHolder holderPwd = new ServletHolder("default", defaultServlet);
		holderPwd.setInitParameter("resourceBase", "./target/classes/static/");

		ctx.addServlet(holderPwd, "/");
		ctx.addServlet(LunchMenuServlet.class, "/api/v1/*");

		server.setHandler(ctx);

		server.start();
		server.join();

	}

}
