#!/usr/bin/env sh

set -e -u

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

cd chaos-loris
./mvnw -q deploy

mysqladmin shutdown
