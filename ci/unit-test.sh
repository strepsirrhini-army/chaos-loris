#!/usr/bin/env sh

set -e -u

mysqld_safe --datadir='/var/lib/mysql' &
sleep 2
mysqladmin create chaos-loris

cd chaos-loris
BUNDLE_PATH=../gems ./mvnw -q -Dmaven.repo.local=../m2/repository -Dmaven.user.home=../m2 package

mysqladmin shutdown
