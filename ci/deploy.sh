#!/usr/bin/env sh

set -e

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

cd chaos-loris
./mvnw -q deploy

mysqladmin shutdown
