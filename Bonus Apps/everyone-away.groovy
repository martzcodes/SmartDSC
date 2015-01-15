/**
*  Everyone Away
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
definition(
    name: "Everyone Away",
    namespace: "oehokie",
    author: "Matt Martz",
    description: "Everyone Away Notification",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("When all of these people leave home") {
        input "people", "capability.presenceSensor", multiple: true
    }

    section("Change to this mode to...") {
        input "newAwayMode",    "mode", title: "Everyone is away"
    }

    section("Away threshold (defaults to 10 min)") {
        input "awayThreshold", "decimal", title: "Number of minutes", required: false
    }

    section("Alarm") {
        input "armAlarm", "enum", title: "Arm the Alarm?", metadata:[values:["Yes","No"]], required:false
        input "dscthing", "capability.polling", title: "Alarm Thing", multiple: false, required: false
    }

    section("Notifications") {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone", "number", title: "Send a text?", required:false
    }
}

def installed() {
    init()
}

def updated() {
    unsubscribe()
    init()
}

def init() {
    subscribe(people, "presence", presence)
}

def presence(evt) {
    if(evt.value == "not present") {
        log.debug("Checking if everyone is away")

        if(everyoneIsAway()) {
            log.info("Starting ${newAwayMode} sequence")
            def delay = (awayThreshold != null && awayThreshold != "") ? awayThreshold * 60 : 10 * 60
            runIn(delay, "setAway")
        }
    }
}

def setAway() {
    if(everyoneIsAway()) {
        if(location.mode != newAwayMode) {
            def message = "${app.label} changed your mode to '${newAwayMode}' because everyone left home"
            log.info(message)
            send(message)
            setLocationMode(newAwayMode)
            if(armAlarm != "No") {
                def latestValueState = dscthing.latestValue("alarmstate")
                def latestValueStatus = dscthing.latestValue("alarmStatus")
                if (latestValueState == "disarmed" && latestValueStatus == "ready") {
                    dscthing.arm()
                }
            }
        }

        else {
            log.debug("Mode is the same, not evaluating")
        }
    }

    else {
        log.info("Somebody returned home before we set to '${newAwayMode}'")
    }
}

private everyoneIsAway() {
    def result = true

    if(people.findAll { it?.currentPresence == "present" }) {
        result = false
    }

    log.debug("everyoneIsAway: ${result}")

    return result
}

private send(msg) {
    if(sendPushMessage != "No") {
        log.debug("Sending push message")
        sendPush(msg)
    }
    if(phone) {
        sendSmsMessage(phone, msg)
    }

    log.debug(msg)
}