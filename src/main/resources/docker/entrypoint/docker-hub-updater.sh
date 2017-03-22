#!/bin/bash

# include parent entrypoint script
source /jdk.sh

# execute command
exec java -jar /opt/docker-hub-updater/docker-hub-updater-*.jar $@
