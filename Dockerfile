FROM    centos:centos6

# Enable EPEL for Node.js
RUN     rpm -Uvh http://download.fedoraproject.org/pub/epel/6/i386/epel-release-6-8.noarch.rpm
# Install Node.js and npm
RUN     yum install -y npm

# Install HomeBridge
#RUN     npm install -g node-gyp-install homebridge homebridge-legacy-plugins 

# Bundle app source
COPY /NAP-demo /src
#ADD /NAP-demo/homebridge.json ~/.homebridge/config.json

# Install app dependencies
RUN cd /src; npm install

EXPOSE 4025 8086

ENTRYPOINT ["node", "/src/app.js"]