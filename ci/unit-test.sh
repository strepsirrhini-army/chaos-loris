#!/usr/bin/env sh

set -e

mysql_install_db --user=mysql --rpm
mysqld_safe --datadir='/var/lib/mysql' &

# /usr/bin/mysqladmin -u root password 'password'
# - SPRING_DATASOURCE_USERNAME=travis SPRING_DATASOURCE_PASSWORD=""

cd chaos-loris
./mvnw -q package
