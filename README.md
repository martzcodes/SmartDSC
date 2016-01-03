SmartDSC
=====================
----
### Update Section

2016-01-03:  Major update.  I finally am updating the README with better / up-to-date install instructions and I'm adding in Homebridge for siri support.

2015-01-14:  Decided to share my "Everyone Away" App which is handy.  If all presence sensors/mobile devices are away, it'll arm your alarm and set your home to away.  Pretty straightforward... very handy.

### End Update Section

### Background

* Original Author: Kent Holloway \<drizit at gmail dot com\> at https://github.com/kholloway/smartthings-dsc-alarm

* Current Author:  Matt Martz \<matt dot martz at gmail dot com\>

This Repo is going to focus on my specific implementation including:

* Node.JS implementation instead of Python (includes a method for arming and disarming the alarm through the smartthings app)
* Smartthings code for DSC (or generic) alarm panels via REST API
* Adding HomeBridge (Apple HomeKit) support for siri integration with SmartThings

### What this project will not go over

This project will not go over:

* Setting up a server (you could use a Raspberry Pi with Ubuntu if you want something cheap)

### Install AlarmServer on your server

#### Requirements

* Server with NodeJS installed
* Needs to be on the same network as your DSC Envisalink 3

#### Environment / Config Variable Explanations

You can use either Environment Variables or the config file to configure your server.  The demo app's preference is to use ENV variables before the config file ones.

* `NODE_ALARM_APP_PORT`: (Optional) The port you want to open up on your server for SmartThings to talk to (defaults to 8086)
* `NODE_ALARM_ACCESS_TOKEN`: This is the Access Token from SmartThings
* `NODE_ALARM_APP_ID`:  This is the App Id from SmartThings
* `NODE_ALARM_PIN`: This is the 4-digit Pin you want to use to Arm/Disarm the alarm on the DSC side (meaning if your alarm was going off this pin would work to turn it off)
* `NODE_ALARM_PROXY_HOST`: This is the LAN IP Address of your DSC Envisalink 3 Card (e.g. 10.0.0.31)
* `NODE_ALARM_PROXY_PORT`: This is the LAN IP Port of your DSC Envisalink 3 Card (probably 4025)
* `NODE_ALARM_ZONE_COUNT`:  This is the number of sensors you have (door/window/motion/smoke/etc)
* `NODE_ALARM_PARTITION_COUNT`:  This is the number of partitions you have
* `NODE_ALARM_PASSWORD`: This is your DSC Envisalink 3 password (proxy uses this to connect to DSC)
* `NODE_ALARM_SERVER_PASSWORD`: This is the password you want the proxy to use (recommend same as Envisalink 3 password) (mimics what the envisalink 3 does, so other things use this to connect to the proxy)
* `NODE_ALARM_STPASS`: This is the password you want your server to use for ST commands.  SmartThings will use this password to send your server instructions like Arm/Disarm

#### Initial Steps

Before you get started, you need a SmartThings Developer Account: [https://graph.api.smartthings.com](https://graph.api.smartthings.com)

Create and Install the SmartDSC Alarm Thing Device

1. Create a new SmartThing Device Type

2. Copy and Paste the code from `devicetypes/DSCAlarmThing.groovy` and publish it for yourself

Install the device using the SmartThings app or Developer page

3. On the SmartThings developer website, go to `My Devices`

4. Click `New Device`

5. Name it, provide required info and in the `Type` section at the bottom you should see your `SmartDSC Alarm Thing`

Create the SmartDSC App

6. Create a new SmartApp and copy/paste the code from `smartapp/dscAlarmIntegrationSmartApp.groovy` into it

7. Set your Location

8. Click "App Settings"

9. Go down to "OAuth" and "Enable OAuth in SmartApp", then click "Update" at the bottom of the page

10. Once updated, click `Code` in the upper right to go back to the code view.

11. Click `Publish` (for me)

12. In your browser go to the `Live Logging` page

Keep the `Live Logging` page open, and go to your mobile device.

13. In the SmartThings App, go to the `Marketplace` (lower right button on iOS)

14. Go to the `SmartApps` tab

15. Scroll down to `My Apps` and add the `SmartDSC App`

16. The only required item is the `Alarm Thing`.  Select the `SmartDSC Alarm Thing Device` that you created in step 5.

17. You can setup the rest later.  Keep tapping Next / Done in the upper right until finished.

Check that your `Live Logging` page is still working (refresh the page, sometimes it times-out) then:

18.  In the SmartThings App, go to your SmartApps section.  You should see the SmartDSC App.  You may need to quit / reopen the app.  Once you see it, tap the arrow on the right of the name (looks like a generic `Play` button)

19.  In the `Live Logging` output, you should see output that looks like this:  `Home Data URL: https://graph.api.smartthings.com/api/smartapps/installations/b445edc4-eb0b-4d97-9854-ecacb80f84d7/data?access_token=9d8e6a07-43ad-465a-9ab3-1815b4bec158`

Save that.  The part after installations is your SmartApp Id (e.g. `b445edc4-eb0b-4d97-9854-ecacb80f84d7`) and the Access Token is at the end (e.g. `9d8e6a07-43ad-465a-9ab3-1815b4bec158`).  You'll need these next.  Now use Method 1 or 2 depending on your comfort with Docker (or not).


#### Method 1: Docker

If you don't have Docker already, skip to Method 2.  If you already have a Docker instance on your home network, you can use the included Dockerfile to build the project and then run it with this command:

```
docker build -t SmartDSC
docker run -d --name SmartDSC --publish 8086:8086 --publish 4025:4025 --restart always -e "NODE_ALARM_ACCESS_TOKEN=YOUR-ACCESS-TOKEN" -e "NODE_ALARM_APP_ID=YOUR-APP-ID" -e "NODE_ALARM_PASSWORD=YOUR-APP-PASSWORD" -e "NODE_ALARM_PIN=XXXX" -e "NODE_ALARM_PROXY_HOST=xxx.xxx.xxx.xxx" -e "NODE_ALARM_PROXY_PORT=4025" -e "NODE_ALARM_SERVER_PASSWORD=YOUR-ENVISALINK-PASSWORD" -e "NODE_ALARM_STPASS=password" -e "NODE_ALARM_ZONE_COUNT=numberofzones" -e "NODE_ALARM_PARTITION_COUNT=numberofpartitions" SmartDSC
```

#### Method 2: Copying the files

1. Clone this Repo on your server (`git clone https://github.com/oehokie/SmartDSC.git`)

2. `cd SmartDSC/NAP-demo`

3. Edit the config.js file with your configuration values.  Use the App Id and Access Token you found in the `Initial Steps` above.

4. On the command line, run `node app`

### Setting up device types

Using the Smartthings IDE create 3 new device types using the code from the devicetypes directory.

There are 4 types of devices you can create:

* DSC Panel       - (Shows partition status info)  (personally I don't use this one, DSC Alarm Thing has replaced it)
* DSC ZoneContact - (contact device open/close)
* DSC ZoneMotion  - (motion device active/inactive)
* DSC ZoneSmoke - (smoke detectors?)
* DSC Alarm Thing - (w/Node.JS method allows arming/disarming the alarm + night mode, manual refresh of data)

In the Web IDE for Smartthings create a new device type for each of the above devices and paste in the code for each device from the corresponding groovy files in the repo.

You can name them whatever you like but I recommend using the names above 'DSC Panel', 'DSC ZoneContact', 'DSC ZoneMotion', 'DSC Thing' (I'm not creative - oehokie), since those names directly identify what they do.

For all the device types make sure you save them and then publish them for yourself.

### Create panel device

Create a new device and choose the type of "DSC Panel" that you published earlier. The network id needs to be **partition1**.

### Create individual zones
Create a new "Zone Device" for each Zone you want Smartthings to show you status for. 

The network id needs to be the word 'zone' followed by the matching zone number that your DSC system sees it as.

For example: **zone1** or **zone5**

### Enjoy!