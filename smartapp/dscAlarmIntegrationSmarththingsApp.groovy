/*
*  DSC Alarm Panel integration via REST API callbacks
*
*  Author: Kent Holloway <drizit@gmail.com>
*  Modified by: Matt Martz <matt.martz@gmail.com>
*/


// Automatically generated. Make future change here.
definition(
    name: "DSC Alarm Panel App",
    namespace: "",
    author: "Matt Martz",
    description: "DSC Alarm Panel App",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

import groovy.json.JsonBuilder

preferences {
    page(name: "dscPrefs", title: "Devices", nextPage: "helloPrefs") {
        section() {
            paragraph "Device Preferences:"
        }
        section("Alarm Thing:") {
            input "dscthing", "capability.polling", title: "Alarm Thing", multiple: false, required: true
        }
        section("Zone Devices:") {
            input "zonedevices", "capability.polling", title: "DSC Zone Devices", multiple: true, required: false
        }
        section("Alarm Panel: (not required)") {
            input "panel", "capability.polling", title: "Alarm Panel", multiple: false, required: false
        }
        section("Thermostats (used later)") {
            input "thermostats", "capability.thermostat", title: "Thermostats", multiple: true, required: false    
        }
        section("Locks (used later)") {
            input "locks", "capability.lock", title: "Locks", required: false, multiple: true
        }
    }
    page(name: "helloPrefs", title: "When Disarmed...", nextPage: "disarmedPrefs") {
        section() {
            paragraph "Hello, Home Mode Preferences"
        }
        section("Disarm Alarm when Mode changes to:") {
            input "helloDisarm", "mode", title: "Disarm", required: false
        }
        section("Arm Alarm (Away) when Mode changes to:") {
            input "helloArm", "mode", title: "Arm", required: false
        }
        section("Arm Alarm (Night) when Mode changes to:") {
            input "helloNight", "mode", title: "Arm", required: false
        }
    }
    page(name: "disarmedPrefs", title: "When Disarmed...", nextPage: "armedPrefs") {
        section() {
            paragraph "Disarmed Preferences:  When Disarmed..."
        }
        section("Change Hello, Home Mode to: ") {
            input "disarmMode", "mode", title: "Disarmed Mode", required: false
        }
        section("Set Thermostat(s) to home?:") {
            input "thermostatdisarm", "enum", title: "Set Thermostat to Home", required: false,
                metadata: [
                    values: ["Yes","No"]
                ]
        }
    }
    page(name: "armedPrefs", title: "When Armed...", nextPage: "nightPrefs") {
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
    page(name: "nightPrefs", title: "When Armed in Night Mode...", nextPage: "alarmingPrefs") {
        section() {
            paragraph "Night Preferences:  When Armed in Night Mode..."
        }
        section("Change Hello, Home Mode to: ") {
            input "nightMode", "mode", title: "Night Mode", required: false
        }
    }
    page(name: "alarmingPrefs", title: "When ALARMING...", nextPage: "notificationPrefs") {
        section() {
            paragraph "ALARMING Preferences:  When ALARMING..."
        }
        section("Turn things on when ALARMING:") {
            input "lightson", "capability.switch", title: "Which lights/switches?", multiple: true, required: false
            input "alarms", "capability.alarm", title: "Which Alarm(s)?", multiple: true, required: false
        }
    }
    page(name: "notificationPrefs", title: "Notifications", install: true, uninstall: true) {
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

mappings {
    path("/panel/fullupdate") {
        action: [
            POST: "fullupdate"
        ]
    }
    path("/panel/zoneupdate") {
        action: [
            POST: "zonejsonupdate"
        ]
    }
    path("/panel/partitionupdate") {
        action: [
            POST: "partitionjsonupdate"
        ]
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
        subscribe(location, "mode", modeChangeHandler)   
    }
    if (locks) {
        subscribe(locks, "lock", lockHandler)   
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
            }
            if ("${event}" == 'alarm') {
                if (lightson) {
                    lightson?.on()
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
                            if (nightMode) {
                                setLocationMode(nightMode)
                            }
                        }
                    }
                }
            }
            dscthing.dscCommand("${event}","${eventMode}")
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
            dscthing.disarm()
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
        dscthing.disarm()
    }
    if (evt.value == helloArm && evt.isStateChange) {
        dscthing.arm()
    }
    if (evt.value == helloNight && evt.isStateChange) {
        dscthing.night()
    }
}