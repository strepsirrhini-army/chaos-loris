#!/usr/bin/env sh

set -e -u

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

ln -fs $PWD/maven ~/.m2

cd chaos-loris
./mvnw -q deploy

mysqladmin shutdown
