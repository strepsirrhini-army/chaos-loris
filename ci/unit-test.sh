#!/usr/bin/env sh

set -e

mysql_install_db --user=mysql --rpm
mysqld_safe --datadir='/var/lib/mysql' &
mysql -e "create database IF NOT EXISTS chaos-loris;" -uroot

cd chaos-loris
./mvnw -q package
