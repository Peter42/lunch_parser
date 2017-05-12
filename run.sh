#!/bin/bash
mvn clean install
cd webservice
mvn exec:java -Dexec.mainClass="de.philipp1994.lunchmenu.webservice.LunchMenuWebserviceLauncher"