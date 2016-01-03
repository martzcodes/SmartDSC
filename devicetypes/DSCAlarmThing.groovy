/**
*  DSC Alarm Thing
*
*  Copyright 2014 Matt Martz
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*/
import groovy.json.JsonBuilder

preferences {
    input("hostpassword", "password", title: "Server Password:", description: "Note: this is sent in the clear (for now).  Don't use something stupid")
    input("hostaddress", "text", title: "IP Address for Server:", description: "Ex: 10.0.0.12 or 192.168.0.4 (no http://)")
    input("hostport", "number", title: "Port of Server", description: "port")
}

metadata {
    // Automatically generated. Make future change here.
    definition (name: "SmartDSC Alarm Thing", author: "Matt Martz") {
        capability "Polling"
        capability "Button"
        capability "Refresh"
        capability "Alarm"
        command "dscCommand"
        command "sendDisarm"
        command "arm"
        command "disarm"
        command "nightarm"
        command "updatestatus"
        attribute "partition1", "string"
        attribute "alarmStatus", "string"
        attribute "alarmstate", "string"
    }

    // simulator metadata
    simulator {

    }

    // UI tile definitions
    tiles {
        standardTile("button","device.mainState", width: 2, height: 2, canChangeIcon: true) {
            state "default", label: 'Default', action: "arm", icon: "st.Home.home2", backgroundColor: "#79b821", nextState: "arming"
            state "disarm", label: 'Disarmed', action: "arm", icon: "st.Home.home2", backgroundColor: "#79b821", nextState: "arming"
            state "arm", label: 'Armed', action: "disarm", icon: "st.Home.home3", backgroundColor: "#b82078", nextState: "disarming"
            state "alarming", label: 'ALARMING', action: "disarm", icon: "st.Home.home3", backgroundColor: "#b82078", nextState: "disarming"
            state "disarming", label: 'Disarming', action: "arm", icon: "st.Home.home2", backgroundColor: "#b8ab20", nextState: "disarm"
            state "arming", label: 'Arming', action: "disarm", icon: "st.Home.home3", backgroundColor: "#b8ab20", nextState: "arm"
            state "night", label: 'Armed Night', action: "disarm", icon: "st.Home.home3", backgroundColor: "#2078b8", nextState: "disarming"

        }
        standardTile("nightbutton","device.button", width: 1, height: 1, canChangeIcon: true) {
            state "default", label: 'Night Arm', action: "nightarm", icon: "st.Weather.weather4", backgroundColor: "#2078b8", nextState: "default"            
        }
        standardTile("alarmStatus","device.alarmStatus", width: 1, height: 1, canChangeIcon: true) {
            state "ready", label: 'Ready', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "readyf", label: 'Ready - F', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "notready", label: 'Not Ready', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "alarming", label: 'ALARMING', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "entry", label: 'Entry Delay', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "exit", label: 'Exit Delay', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "lockout", label: 'Keypad Lockout', action: "f", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "fail", label: 'Failed to Arm', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "away", label: 'Armed - Away', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "stay", label: 'Armed - Stay', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "zeroaway", label: 'Zero Entry Away', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "zerostay", label: 'Zero Entry Stay', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""
            state "disarmed", label: 'Disarmed', action: "", icon: "", backgroundColor: "#ffffff", nextState: ""            
        }
        standardTile("update", "default", inactiveLabel: false) {
            state "default", action:"updatestatus", icon:"st.secondary.refresh"
        }
        standardTile("disarmbutton", "device.button", inactiveLabel:false) {
            state "default", label:"Disarm", icon:"st.Home.home2", action:"disarm"
        }
        main (["button","nightbutton","alarmStatus","update","disarmbutton"])
        details(["button","nightbutton","alarmStatus","update","disarmbutton"])
    }
}

// Parse incoming device messages to generate events
def parse(String description) {

}

def arm() {
    log.debug "Arming..."
    contactEnvisalinkJson("arm")
}

def disarm() {
    log.debug "Disarming..."
    contactEnvisalinkJson("disarm")
}

def nightarm() {
    log.debug "Night..."
    contactEnvisalinkJson("nightarm")
}

def dscCommand(String state,String statemode) {
    log.debug "DSC Command State Requested: ${state} and ${statemode}"

    if ("${state}" == "ready") {
        sendEvent(name: 'alarmStatus', value: 'ready')
        sendEvent(name: 'alarm', value: 'Ready')
        sendEvent(name: 'mainState', value: 'disarm')
        sendEvent(name: 'alarmstate', value: 'disarmed')
    }
    if ("${state}" == "disarmed") {
        sendEvent(name: 'alarmStatus', value: 'ready')
        sendEvent(name: 'alarm', value: 'Ready')
        sendEvent(name: 'mainState', value: 'disarm')
        sendEvent(name: 'alarmstate', value: 'disarmed')
    }
    if ("${state}" == "notready") {
        sendEvent(name: 'alarmStatus', value: 'notready')
        sendEvent(name: 'alarm', value: 'Not Ready')
        sendEvent(name: 'mainState', value: 'disarm')
        sendEvent(name: 'alarmstate', value: 'disarmed')
    }
    if ("${state}" == "armed") {
        if ("${statemode}" == "0") {
            sendEvent(name: 'alarmStatus', value: 'away')
            sendEvent(name: 'alarm', value: 'Away')
            sendEvent(name: 'mainState', value: 'arm')
        }
        if ("${statemode}" == "1") {
            sendEvent(name: 'alarmStatus', value: 'stay')
            sendEvent(name: 'alarm', value: 'Stay')
            sendEvent(name: 'mainState', value: 'arm')
        }
        if ("${statemode}" == "2") {
            sendEvent(name: 'alarmStatus', value: 'zeroaway')
            sendEvent(name: 'alarm', value: 'Zero Entry Delay (Away)')
            sendEvent(name: 'mainState', value: 'night')
        }
        if ("${statemode}" == "3") {
            sendEvent(name: 'alarmStatus', value: 'zerostay')
            sendEvent(name: 'alarm', value: 'Zero Entry Delay (Stay)')
            sendEvent(name: 'mainState', value: 'night')
        }
        sendEvent(name: 'alarmstate', value: 'armed')
        sendEvent(name: 'alarm', value: 'Armed')
    }
    if ("${state}" == "alarm") {
        sendEvent(name: 'alarmStatus', value: 'alarming')
        sendEvent(name: 'alarm', value: 'ALARMING')
        sendEvent(name: 'mainState', value: 'alarming')
        sendEvent(name: 'alarmstate', value: 'disarmed')
    }
    if ("${state}" == "exitdelay") {
        sendEvent(name: 'alarmStatus', value: 'exit')
        sendEvent(name: 'alarm', value: 'Exit Delay')
        sendEvent(name: 'mainState', value: 'arming')
        sendEvent(name: 'alarmstate', value: 'disarmed')
    }
    if ("${state}" == "entrydelay") {
        sendEvent(name: 'alarmStatus', value: 'entry')
        sendEvent(name: 'alarm', value: 'Entry Delay')
        sendEvent(name: 'mainState', value: 'arm')
        sendEvent(name: 'alarmstate', value: 'armed')
    }
    if ("${state}" == "lockout") {
        sendEvent(name: 'alarmStatus', value: 'lockout')
        sendEvent(name: 'alarm', value: 'Keypad Lockout')
    }
    if ("${state}" == "failed") {
        sendEvent(name: 'alarmStatus', value: 'fail')
        sendEvent(name: 'alarm', value: 'Failed')
    }
}


def sendDisarm() {
    log.debug "TRYING TO DISARM"
    disarm()
}

def poll() {
    log.debug "Executing 'poll'"
    contactEnvisalinkJson("status")
}

def updatestatus() {
    log.debug "Executing 'updatestatus'"
    contactEnvisalinkJson("status")
}


def refresh() {
    log.debug "Executing 'refresh' which is actually poll()"
    poll()
}

def contactEnvisalinkJson(String command) {
    def host = settings.hostaddress
    def port = settings.hostport
    def hosthex = convertIPtoHex(host)
    def porthex = convertPortToHex(port)
    device.deviceNetworkId = "$hosthex:$porthex" 

    log.debug "The device id configured is: $device.deviceNetworkId"

    def path = "/jsoncommand"

    def json = new JsonBuilder()
    json.call("command":"${command}","password":"${settings.hostpassword}")
    def message = json.toString()

    def headers = [:] 
    headers.put("HOST", "$host:$port")
    headers.put("Content-Type", "application/json")
    headers.put("Message", message)

    log.debug "The Header is $headers"

    def method = "POST"

    try {
        def hubAction = new physicalgraph.device.HubAction(
            method: method,
            path: path,
            body: json,
            headers: headers,
        )

        log.debug hubAction
        hubAction
    }
    catch (Exception e) {
        log.debug "Hit Exception $e on $hubAction"
    }
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex

}

private String convertPortToHex(port) {
    String hexport = port.toString().format( '%04x', port.toInteger() )
    log.debug hexport
    return hexport
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}


private String convertHexToIP(hex) {
    log.debug("Convert hex to ip: $hex") 
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
    def ip = convertHexToIP(parts[0])
    def port = convertHexToInt(parts[1])
    return ip + ":" + port
}