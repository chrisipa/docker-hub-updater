Docker Hub Updater
=======

[![Build Status](https://papke.it/jenkins/buildStatus/icon?job=ad-password-handler)](https://papke.it/jenkins/job/docker-hub-updater/)
[![Code Analysis](https://img.shields.io/badge/code%20analysis-available-blue.svg)](https://papke.it/sonar/overview?id=212)

Overview
--------
Spring boot application for updating docker image description texts on docker hub.

Features
---------
* Read docker hub credentials from docker config file
* Read description texts from custom files
* Set short description of a docker image
* Set full description of a docker image

Prerequisites
-------------
* [Docker](https://docs.docker.com/engine/installation/) must be installed

Usage
-----
* Run docker container for updating description texts:
  ```
  docker run --rm -v $HOME/.docker:/root/.docker -v $HOME/workspace/debian:/image chrisipa/docker-hub-updater --image.name=debian --full.description.file=/image/README.md --short.description.file=/image/README_SHORT.md
  ```