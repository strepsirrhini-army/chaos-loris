#!/usr/bin/env sh

set -e -u

cd chaos-loris
./mvnw -q -Dmaven.repo.local=$M2/repository -Dmaven.user.home=$M2 -Dmaven.test.skip=true deploy
