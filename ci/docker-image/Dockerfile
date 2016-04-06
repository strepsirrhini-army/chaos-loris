FROM java:8-jdk-alpine

RUN apk add --update \
    --repository http://dl-cdn.alpinelinux.org/alpine/edge/community \
    bash \
    build-base \
    git \
    libffi-dev \
    mariadb \
    mariadb-client \
    nodejs \
    ruby \
    ruby-bundler \
    ruby-dev \
    ruby-io-console \
    ruby-nokogiri \
 && rm -rf /var/cache/apk/*

RUN mysql_install_db --user=mysql --rpm
