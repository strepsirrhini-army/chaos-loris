#!/usr/bin/env sh

set -e

cd chaos-loris
./mvnw -q -Dmaven.test.skip=true deploy
