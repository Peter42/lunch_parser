#!/bin/bash

maven=$(which mvn)

mvn clean install
cd webservice
daemonize -e ./stderr.log -o stdout.log -c . -p ./pid -l ./lock $maven exec:java -Dexec.mainClass="de.philipp1994.lunchmenu.webservice.LunchMenuWebserviceLauncher"
