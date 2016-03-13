/*
*  DSC Alarm Panel integration via REST API callbacks
*
*  Author: Kent Holloway <drizit@gmail.com>
*  Modified by: Matt Martz <matt.martz@gmail.com>
*/


// Automatically generated. Make future change here.
definition(
    name: "SmartDSC App",
    namespace: "oehokie",
    author: "Matt Martz",
    description: "DSC Alarm Panel App",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

import groovy.json.JsonBuilder

preferences {
    page(name: "copyConfig", install: true, uninstall: true)
    page(name: "setupDevices")
    page(name: "dscPrefs")
    page(name: "helloPrefs")
    page(name: "smartMonitorIntegration")
    page(name: "disarmedPrefs")
    page(name: "armedPrefs")
    page(name: "nightPrefs")
    page(name: "alarmingPrefs")
    page(name: "notificationPrefs")
}

def copyConfig() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "copyConfig", title: "Config", install:true) {
        section() {
            href page:"setupDevices", required:false, title:"Devices", description:"Select Devices for OAuth Control"
            href page:"dscPrefs", required:false, title:"DSC Preferences", description:"Setup your SmartDSC Integration"
            href page:"helloPrefs", required:false, title:"Hello, Home", description:"Preferences for Hello, Home"
            href page:"smartMonitorIntegration", required:true, title:"Smart Monitor Integration", description:"Integrate with Smart Home Monitor"
            href page:"notificationPrefs", required:false, title:"Notifications", description:"How/When to get Notified"
        }

        section() {
            paragraph "View this SmartApp's configuration to use it in other places."
            href url:"https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/config?access_token=${state.accessToken}", style:"embedded", required:false, title:"Config", description:"Tap, select, copy, then click \"Done\""
            href url:"https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/devices?access_token=${state.accessToken}", style:"embedded", required:false, title:"Debug", description:"View accessories JSON"
        }
    }
}

def setupDevices() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "setupDevices", title: "Setup Devices") {
        section("Select devices to include in the /devices API call") {
            input "switches", "capability.switch", title: "Switches", multiple: true, required: false
            input "hues", "capability.colorControl", title: "Hues", multiple: true, required: false
            input "thermostats", "capability.thermostat", title: "Thermostats", multiple: true, required: false
            input "locks", "capability.lock", title: "Locks", required: false, multiple: true
        }
    }
}

def smartMonitorIntegration() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "smartMonitorIntegration", title: "Smart Home Monitor") {
        section("Smart Home Monitor") {
            input "smartmonitor", "enum", title: "Integrate?", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
    }
}

def dscPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "dscPrefs", title: "DSC Preferences") {
        section() {
            input "dscthing", "capability.polling", title: "SmartDSC Alarm Thing", multiple: false, required: false
            input "zonedevices", "capability.polling", title: "DSC Zone Devices", multiple: true, required: false
            input "thermostats", "capability.thermostat", title: "Thermostats", multiple: true, required: false
            input "locks", "capability.lock", title: "Locks", required: false, multiple: true
        }
        section("Preferences") {
            href page:"helloPrefs", required:false, title:"Hello, Home", description:"Preferences for Hello, Home"
            href page:"disarmedPrefs", required:false, title:"When Disarming...", description:"Actions to take when the alarm disarms"
            href page:"armedPrefs", required:false, title:"When Arming...", description:"Actions to take when the alarm arms"
            href page:"nightPrefs", required:false, title:"When Arming (Night)...", description:"Actions to take when the alarm arms in night mode"
            href page:"alarmingPrefs", required:false, title:"When ALARMING...", description:"Actions to take when the alarm is ALARMING"
        }
    }
}

def helloPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "helloPrefs", title: "Hello, Home Mode Preferences") {
        section("Disarming") {
            input "helloDisarm", "mode", title: "Disarm Alarm when Mode changes to", required: false
            href page:"disarmedPrefs", required:false, title:"When Disarming...", description:"Actions to take when the alarm disarms"
        }
        section("Arming") {
            input "helloArm", "mode", title: "Arm Alarm (Away) when Mode changes to", required: false
            href page:"armedPrefs", required:false, title:"When Arming...", description:"Actions to take when the alarm arms"
        }
        section("Night Mode") {
            input "helloNight", "mode", title: "Arm Alarm (Night) when Mode changes to", required: false
            href page:"nightPrefs", required:false, title:"When Arming (Night)...", description:"Actions to take when the alarm arms in night mode"
        }
        section("ALARMING") {
            href page:"alarmingPrefs", required:false, title:"When ALARMING...", description:"Actions to take when the alarm is ALARMING"
        }
    }
}

def disarmedPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "disarmedPrefs", title: "Disarmed Preferences") {
        section("When Disarmed...") {
            input "disarmMode", "mode", title: "Change Hello, Home Mode to", required: false
            input "disarmoff", "capability.switch", title: "Which lights/switches to turn off?", multiple: true, required: false
            input "thermostatdisarm", "enum", title: "Set Thermostat to Home", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
    }
}

    
    
def armedPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "armedPrefs", title: "When Armed...") {
        section() {
            paragraph "Armed Preferences:  When Armed..."
        }
        section("Change Hello, Home Mode to: ") {
            input "awayMode", "mode", title: "Armed Mode", required: false
        }
        section("Set Thermostat(s) to away?:") {
            input "thermostataway", "enum", title: "Set Thermostat to Away", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
        section("Disarm when user unlocks with code?") {
            input "lockdisarm", "enum", title: "Disarm?", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
    }
}

def nightPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "nightPrefs", title: "When Armed in Night Mode...") {
        section() {
            paragraph "Night Preferences:  When Armed in Night Mode..."
        }
        section("Change Hello, Home Mode to: ") {
            input "nightMode", "mode", title: "Night Mode", required: false
        }
    }
}

def alarmingPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "alarmingPrefs", title: "When ALARMING...") {
        section() {
            paragraph "ALARMING Preferences:  When ALARMING..."
        }
        section("Turn things on when ALARMING:") {
            input "lightson", "capability.switch", title: "Which lights/switches?", multiple: true, required: false
            input "alarmson", "capability.alarm", title: "Which Alarm(s)?", multiple: true, required: false
        }
    }
}

def notificationPrefs() {
    if (!state.accessToken) {
        createAccessToken()
    }
    dynamicPage(name: "notificationPrefs", title: "Notifications") {
        section() {
            paragraph "Notification Preferences:"
        }
        section("Notifications (optional):") {
            input "sendNotification", "enum", title: "Push Notifiation", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
            input "phone1", "phone", title: "Phone Number", required: false
            input "notifyalarm", "enum", title: "Notify When Alarming?", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
            input "notifyarmed", "enum", title: "Notify When Armed?", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
        section("XBMC Notifications") {
            input "xbmcserver", "text", title: "XBMC IP", description: "IP Address", required: false
            input "xbmcport", "number", title: "XBMC Port", description: "Port", required: false
        }
    }
}

def renderConfig() {
    def configJson = new groovy.json.JsonOutput().toJson([
        description: "JSON API",
        platforms: [
            [
                platform: "SmartThings",
                name: "SmartThings",
                app_id:        app.id,
                access_token:  state.accessToken
            ]
        ],
    ])

    def configString = new groovy.json.JsonOutput().prettyPrint(configJson)
    render contentType: "text/plain", data: configString
}

def deviceCommandMap(device, type) {
  device.supportedCommands.collectEntries { command->
      def commandUrl = "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/${type}/${device.id}/command/${command.name}?access_token=${state.accessToken}"
      [
        (command.name): commandUrl
      ]
  }
}

def authorizedDevices() {
    [
        switches: switches,
        hues: hues,
        thermostats: thermostats,
        locks: locks
    ]
}

def renderDevices() {
    def deviceData = authorizedDevices().collectEntries { devices->
        [
            (devices.key): devices.value.collect { device->
                [
                    name: device.displayName,
                    commands: deviceCommandMap(device, devices.key)
                ]
            }
        ]
    }
    def deviceJson    = new groovy.json.JsonOutput().toJson(deviceData)
    def deviceString  = new groovy.json.JsonOutput().prettyPrint(deviceJson)
    render contentType: "application/json", data: deviceString
}

def deviceCommand() {
  def device  = authorizedDevices()[params.type].find { it.id == params.id }
  def command = params.command
  if (!device) {
      httpError(404, "Device not found")
  } else {
      if (params.value) {
        device."$command"(params.value)
      } else {
        device."$command"()
      }
  }
}

def authError() {
    [error: "Permission denied"]
}

mappings {
    if (!params.access_token || (params.access_token && params.access_token != state.accessToken)) {
        path("/devices")                      { action: [GET: "authError"] }
        path("/config")                       { action: [GET: "authError"] }
        path("/:type/:id/command/:command")   { action: [PUT: "authError"] }
        path("/panel/fullupdate")             { action: [POST: "authError"] }
        path("/panel/zoneupdate")             { action: [POST: "authError"] }
        path("/panel/partitionupdate")        { action: [POST: "authError"] }
    } else {
        path("/devices")                      { action: [GET: "renderDevices"]  }
        path("/config")                       { action: [GET: "renderConfig"]  }
        path("/:type/:id/command/:command")   { action: [PUT: "deviceCommand"] }
        path("/panel/fullupdate")             { action: [POST: "fullupdate"] }
        path("/panel/zoneupdate")             { action: [POST: "zonejsonupdate"] }
        path("/panel/partitionupdate")        { action: [POST: "partitionjsonupdate"] }
    }
}

def installed() {
    log.debug "Installed!"
    if (panel) {
        subscribe(panel)   
    }
    if (dscthing) {
        subscribe(dscthing, "updateDSC", updateDSC)   
    }
    if (location) {
    	subscribe(location, "routineExecuted", modeChangeHandler)
        // subscribe(location, "mode", modeChangeHandler)   
    }
    if (locks) {
        subscribe(locks, "lock", lockHandler)   
    }
    if (smartmonitor == "Yes") {
        subscribe(location, "alarmSystemStatus", alarmStatusUpdate)
    }
}

def updated() {
    log.debug "Updated!"
    unsubscribe()
    installed()
    getURL(null)
}

void updateZoneOrPartition() {
    update(panel)
}

def getURL(e) {
    if (resetOauth) {
        log.debug "Reseting Access Token"
        state.accessToken = null
    }

    if (!state.accessToken) {
        createAccessToken()
        log.debug "Creating new Access Token: $state.accessToken"
    }

    def url1 = "https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/data"
    def url2 = "?access_token=${state.accessToken}"
    log.debug "${title ?: location.name} Data URL: $url1$url2"
}

def updateDSC(evt) {
    log.debug "$evt.value"
    def evtList = "$evt.value".tokenize();
    if ("${evtList[1]}" == '601') {
        updateZoneDevices(zonedevices,"${evtList[0]}","alarm")
    }
    if ("${evtList[1]}" == '602') {
        updateZoneDevices(zonedevices,"${evtList[0]}","closed")
    }
    if ("${evtList[1]}" == '609') {
        updateZoneDevices(zonedevices,"${evtList[0]}","open")
    }
    if ("${evtList[1]}" == '610') {
        updateZoneDevices(zonedevices,"${evtList[0]}","closed")
    }
}

void fullupdate() {
    def partitions = request.JSON?.partition

    for (partition in partitions) {
        def partitiondata = partition.value
        def partitioncode = partitiondata.code[0..2]
        log.debug "partitioncode: ${partitioncode}"
        if (partitioncode == "652") {
            def partitionmode = partitiondata.code[3..4]
            updatePartition("${partitioncode}","${partitionmode}")
        } else {
            updatePartition("${partitioncode}",null)
        }
    }

    def zones = request.JSON?.zone
    for (zone in zones) {
        log.debug "zone"+zone.key+" -- ${zone.value.code[0..2]}"
        updateZone("${zone.value.code[0..2]}","zone"+zone.key)
    }
}

def zonejsonupdate() {
    def zone = request.JSON
    log.debug "zone json update"
    log.debug "zone"+zone.zone+" -- ${zone.code[0..2]}"
    updateZone("${zone.code[0..2]}","zone"+zone.zone)
}

def partitionjsonupdate() {
    def partition = request.JSON
    log.debug "Json: ${request.JSON}"
    if (partition.code == "652") {
        updatePartition("${partition.code}","${partition.mode}")
    } else {
        updatePartition("${partition.code}",null)
    }
}

private updateZone(String eventCode, String zone) {

    if (eventCode && zone) {
        def eventMap = [
            '601':"alarm",
            '602':"closed",
            '609':"open",
            '610':"closed"
        ]

        def event = eventMap."${eventCode}"

        if (event) {
            def zonedevice = zonedevices.find { it.deviceNetworkId == "${zone}" }
            if (!zonedevice) {

            } else {
                log.debug "Was True... Zone Device: $zonedevice.displayName at $zonedevice.deviceNetworkId is ${event}"
                if ("${zonedevice.latestValue("contact")}" != "${event}") {
                    zonedevice.zone("${event}")
                    def lanaddress = "${settings.xbmcserver}:${settings.xbmcport}"
                    def deviceNetworkId = "1234"
                    def json = new JsonBuilder()
                    def messagetitle = "$zonedevice.displayName".replaceAll(' ','%20')
                    log.debug "$messagetitle"
                    json.call("jsonrpc":"2.0","method":"GUI.ShowNotification","params":[title: "$messagetitle",message: "${event}"],"id":1)
                    def xbmcmessage = "/jsonrpc?request="+json.toString()
                    def result = new physicalgraph.device.HubAction("""GET $xbmcmessage HTTP/1.1\r\nHOST: $lanaddress\r\n\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}")
                    sendHubCommand(result)
                }

            }
        }
    }

}

private updatePartition(String eventCode, String eventMode) {
    log.debug "updatePartition: ${eventCode}, eventMode: ${eventMode}"

    if (eventCode) {
        def eventMap = [
            '650':"ready",
            '651':"notready",
            '652':"armed",
            '654':"alarm",
            '656':"exitdelay",
            '657':"entrydelay",
            '658':"lockout",
            '659':"failed",
            '655':"disarmed"
        ]

        def event = eventMap."${eventCode}"

        if (event) {
            log.debug "It was a partition...  ${event}... ${eventMode}"
            if ("${event}" == 'disarmed') {
            	setSmartHomeMonitor("off")
                if (disarmMode) {
                    setLocationMode(disarmMode)
                }
                if (thermostatdisarm == "Yes") {
                    if (thermostats) {
                        for (thermostat in thermostats) {
                            thermostat.present()
                        }
                    }
                }
                if (disarmoff) {
                    disarmoff?.off()
                }
            }
            if ("${event}" == 'alarm') {
                if (lightson) {
                    lightson?.on()
                }
                if (alarmson) {
                    alarmson?.on()
                }
                if (notifyalarm == "Yes") {
                    log.debug "Notify when alarm is Yes and the Alarm is going off"
                    sendMessage("ALARMING")
                }
            }
            if ("${event}" == 'armed') {
                if ("${dscthing.latestValue('alarmstate')}" != "armed") {
                    if (locks) {
                        for (lock in locks) {
                            if ("${lock.latestValue('lock')}" == "unlocked") {
                                sendMessage("$lock is Unlocked :(")
                            }
                        }
                    }
                    if (notifyarmed == "Yes") {
                        log.debug "Notify when alarm is Yes and the Alarm is Armed"
                        sendMessage("Alarm is Armed")
                    }
                    if (eventMode) {
                        if ("${eventMode}" == '0') { //away mode (i.e. not at home)
                        	setSmartHomeMonitor("away")
                            if (thermostataway == "Yes") {
                                if (thermostats) {
                                    for (thermostat in thermostats) {
                                        thermostat.away()
                                    }
                                }
                            }
                            if (awayMode) {
                                setLocationMode(awayMode)
                            }
                        }
                        if ("${eventMode}" == '3' || "${eventMode}" == '2') { //armed w/zero entry delay
                        	setSmartHomeMonitor("stay")
                            if (nightMode) {
                                setLocationMode(nightMode)
                            }
                        }
                    }
                }
            }
            if (dscthing) {
                dscthing.dscCommand("${event}","${eventMode}")
            }
        }
    }

}

private sendMessage(msg) {
    def newMsg = "Alarm Notification: $msg"
    if (phone1) {
        sendSms(phone1, newMsg)
    }
    if (sendNotification == "Yes") {
        sendPush(newMsg)
    }
}

def lockHandler(evt) {
    log.debug "This event name is ${evt.name}"

    // get the value of this event, e.g., "on" or "off"
    log.debug "The value of this event is ${evt.value}"

    // get the Date this event happened at
    log.debug "This event happened at ${evt.date}"

    // did the value of this event change from its previous state?
    log.debug "The value of this event is different from its previous value: ${evt.isStateChange()}"
    if (lockdisarm == "Yes") {
        if (evt.descriptionText.contains("Un-Secured by User")) {
            log.debug "Disarming due to door code"
            if (dscthing) {
                dscthing.disarm()
            }
            if (smartmonitor == "Yes") {
                setSmartHomeMonitor("off")
            }
        }
    }
}

def modeChangeHandler(evt) {
    log.debug "This event name is ${evt.name}"

    // get the value of this event, e.g., "on" or "off"
    log.debug "The value of this event is ${evt.value}"

    // get the Date this event happened at
    log.debug "This event happened at ${evt.date}"

    // did the value of this event change from its previous state?
    log.debug "The value of this event is different from its previous value: ${evt.isStateChange()}"
    if (evt.value == helloDisarm && evt.isStateChange) {
        if (dscthing) {
            dscthing.disarm()
        }
        if (smartmonitor == "Yes") {
            setSmartHomeMonitor("off")
        }
    }
    if (evt.value == helloArm && evt.isStateChange) {
        if (dscthing) {
            dscthing.arm()
        }
        if (smartmonitor == "Yes") {
            setSmartHomeMonitor("away")
        }
    }
    if (evt.value == helloNight && evt.isStateChange) {
        if (dscthing) {
            dscthing.nightarm()
        }
        if (smartmonitor == "Yes") {
            setSmartHomeMonitor("stay")
        }
    }
}

private setSmartHomeMonitor(status)
{
	//Let's make sure the user turned on Smart Home Monitor Integration and the value I'm trying to set it to isn't already set
	if(smartmonitor == "Yes" && location.currentState("alarmSystemStatus").value != status)
    {
    	log.debug "Set Smart Home Monitor to $status"
    	sendLocationEvent(name: "alarmSystemStatus", value: status)
    }
}