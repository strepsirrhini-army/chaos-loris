FROM java:8-jdk-alpine

RUN apk add --update \
    bash \
    build-base \
    git \
    libffi-dev \
    libxml2-dev \
    libxslt-dev \
    mariadb \
    mariadb-client \
    nodejs \
    pcre-dev \
    ruby \
    ruby-bundler \
    ruby-dev \
    ruby-io-console \
 && rm -rf /var/cache/apk/*

RUN gem install nokogiri --version 1.6.7.2 --no-rdoc --no-ri

RUN mysql_install_db --user=mysql --rpm
