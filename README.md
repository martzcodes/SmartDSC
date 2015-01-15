SmartDSC
=====================
----
### Update Section

2015-01-14:  Decided to share my "Everyone Away" App which is handy.  If all presence sensors/mobile devices are away, it'll arm your alarm and set your home to away.  Pretty straightforward... very handy.

### End Update Section


* Original Author: Kent Holloway \<drizit at gmail dot com\> at https://github.com/kholloway/smartthings-dsc-alarm

* Current Author:  Matt Martz \<matt dot martz at gmail dot com\>

This Repo is going to focus on my specific implementation including:

* Node.JS implementation instead of Python (includes a method for arming and disarming the alarm through the smartthings app)
* Smartthings code for DSC (or generic) alarm panels via REST API

Smartthings support is beta status right now follow the rough steps below to get it setup.
Requirements:
  * https://github.com/oehokie/NodeAlarmProxy (see NAP-Demo)

### Install AlarmServer on your server/computer

#### Node.JS Method (NEW!):

This method uses the https://github.com/oehokie/NodeAlarmProxy alarm server.  To install create a directory on your server and:

    npm init

Answer the questions to the best of your ability.  This will initialize the project directory.  Then run:

    npm install nodealarmproxy --save
    npm install express --save
    npm install body-parser --save

Then see `NAP-Demo` (may need some tweaking).  Make new app.js and config files based on that (should be in the root of that folder you made).

With NAP-Demo setup it enables enabling/disabling the alarm via a separate device panel, DSC Alarm Thing. (including zero-entry delay "night" mode)

### Setup a Smartthings developer account at:

 [https://graph.api.smartthings.com](https://graph.api.smartthings.com)

### Get Authentication up and running

1. Create a new Smartthings App in the IDE, call it 'DSC Integration' or whatever you like. Use the code from dscAlarmIntegrationSmarththingsApp.groovy file for the new smartapp.

2. Click "Enable OAuth in Smart App" and copy down the generated "OAuth Client ID" and the "OAuth Client Secret", you will need them later to generate an access code.
   Click "Create" and when the code section comes up select all the text and replace it with the code from the file 'dscAlarmIntegrationSmarththingsApp.groovy'.
   Click "Save" then "Publish" -> "For Me".

2. Now the hard part, we need to authorize this Smarttthings app to be used via the REST API.
   It's going to take a few steps but all you need is a web browser and your OAuth ID's from the app setup page.
   Follow the RESTAPISetup.md document in this same repo to finish the setup.

3. Edit 'config.js' in the NAP-demo folder (or tweak to your own configuration) and add in the OAuth/Access Code information, adjust your zones/partitions and callback event codes as needed.
   Leaving them at the defaults is likely what you already want.

4. Fire up the AlarmServer, you should see your events from the server show up within 1-2 seconds on your Smartphone.

### Setup device types

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

### Changes from the Original Repo:

* I added a lot of code to the smart app.  It now detects when you leave your doors unlocked and arm the alarm and sends you a notification... can turn off lights... can turn off sonos (doesn't currently work)... etc

### Eventual improvements:

* Note, the current setup isn't 100% secure using my method.  ~~I had to "hack" SmartThings HubAction and pass the alarm commands and verification "password" by placing them in the header.  Couldn't figure out how to get anything in the body to show up.~~  *Pretty sure it's not encrypted too.(?)*  Odds of someone knowing to sniff for your password and do all this are very low.  If they're that determined to break into your house, it's going to happen anyways.