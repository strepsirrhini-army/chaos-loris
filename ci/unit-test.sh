#!/usr/bin/env sh

set -e

mysql_install_db --user=mysql --rpm
mysqld_safe --datadir='/var/lib/mysql' &

cd chaos-loris
./mvnw -q package
