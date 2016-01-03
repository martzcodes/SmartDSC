FROM debian:jessie
MAINTAINER "Matt Martz <matt.martz@gmail.com>"

RUN apt-get update

##################################################
# Set environment variables                      #
##################################################

# Ensure UTF-8
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8

ENV DEBIAN_FRONTEND noninteractive
ENV TERM xterm


##################################################
# Add app user                                   #
##################################################

#RUN useradd --create-home --home-dir /home/app --shell /bin/bash app


##################################################
# Install tools                                  #
##################################################

RUN apt-get install -y curl wget git apt-transport-https python build-essential make g++ libavahi-compat-libdnssd-dev libkrb5-dev vim net-tools
RUN alias ll='ls -alG'

#####SPECIFIC#####


##################################################
# Install homebridge                             #
##################################################

#ADD homebridge-src /home/app/homebridge
#ADD config.json /home/app/homebridge/config.json
#RUN chown -R app:app /home/app/homebridge

#WORKDIR /home/app/homebridge
#USER app
#RUN npm install

RUN npm install -g node-gyp-install
RUN npm install -g homebridge
RUN npm install -g homebridge-openhab
RUN npm install -g homebridge-legacy-plugins

##################################################
# Start                                          #
##################################################

USER root
#WORKDIR /home/app/homebridge

RUN mkdir -p /var/run/dbus
#VOLUME /var/run/dbus

EXPOSE 4025 5353 8086 51826

#CMD ["npm", "run", "start"]
ADD NAP-demo/run.sh /root/run.sh
COPY /NAP-demo /root

CMD cd /root; npm install

RUN mkdir /root/.homebridge
ADD NAP-demo/homebridge.json /root/.homebridge/config.json

ENTRYPOINT ["/root/run.sh"]