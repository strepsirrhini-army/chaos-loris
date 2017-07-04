#!/usr/bin/env sh

set -e -u -x

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

ls -alF
ln -fs m2 ~/.m2

cd chaos-loris
./mvnw -q package

mysqladmin shutdown
