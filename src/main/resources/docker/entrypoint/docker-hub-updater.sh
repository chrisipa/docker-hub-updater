#!/bin/bash

# include parent entrypoint script
source /jdk-base.sh

# execute command
exec java -jar /opt/docker-hub-updater/docker-hub-updater-*.jar $@
