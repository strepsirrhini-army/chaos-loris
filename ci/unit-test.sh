#!/usr/bin/env sh

set -e -u

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

GEM_HOME=$PWD/gems
M2=$PWD/m2

cd chaos-loris
./mvnw -q -Dmaven.repo.local=$M2/repository -Dmaven.user.home=$M2 package

mysqladmin shutdown
