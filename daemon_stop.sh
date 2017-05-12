#!/bin/bash
cd webservice
PID=$(cat ./pid)
kill $PID
