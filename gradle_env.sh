#!/bin/bash

# Loads the .env file into the environment
set -a
source .env
set +a

./gradlew $@
