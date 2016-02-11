/**
 *  Lock Control
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
    name: "Lock Control",
    namespace: "oehokie",
    author: "Matt Martz",
    description: "Kwikset Lock and Alarm Integration",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("What Locks") {
                input "locks","capability.lock", title: "Lock", multiple: true, required: false
    }
    section("User 1") {
        input "name1", "text", title: "User Name", required: false
        input "code1", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete1", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("User 2") {
        input "name2", "text", title: "User Name", required: false
        input "code2", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete2", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("User 3") {
        input "name3", "text", title: "User Name", required: false
        input "code3", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete3", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("User 4") {
        input "name4", "text", title: "User Name", required: false
        input "code4", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete4", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("User 5") {
        input "name5", "text", title: "User Name", required: false
        input "code5", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete5", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("User 6") {
        input "name6", "text", title: "User Name", required: false
        input "code6", "decimal", title: "Code (4 to 8 digits)", required: false
        input "delete6", "enum", title: "Delete User", required: false, metadata: [values: ["Yes","No"]]
    }
    section("Alarm Disarm") {
        input "alarmdisarm", "enum", title: "Disarm the Alarm", required: false, metadata: [values: ["Yes","No"]]
        input "alarmThing", "capability.polling", title: "Alarm Thing", required: false
    }
    section("Notifications") {
        input "pushnotify", "enum", title: "Push Notify?", required: false, metadata: [values: ["Yes","No"]]
        input "phone1", "number", title: "Phone Number", required: false
    }
}

def installed()
{
        subscribe(app, appTouch)
        subscribe(locks, "userunlock", userunlock)
}

def updated()
{
        unsubscribe()
        subscribe(app, appTouch)
        subscribe(locks, "userunlock", userunlock)
}

def appTouch(evt) {
    log.debug "app touched"
    def codes = [code1,code2,code3,code4,code5,code6]
    def deletes = [delete1,delete2,delete3,delete4,delete5,delete6]
    
    for (lock in locks) {
    	for (int i = 0; i < 6; i++) {
        	def lockcode = codes[i]
            def lockdelete = deletes[i]
            def idstatus = 1
            if (lockcode) {
            	if (delete == "Yes") {
                    idstatus = 0
                } else {
                    idstatus = 1
                }
                lock.usercodechange(i+1, lockcode, idstatus)
            } else {
                lock.usercodechange(i+1, lockcode, 0)
            }
        }
    }
}

def userunlock(evt){
	def usernames = [name1,name2,name3,name4,name5,name6]

    if (alarmdisarm == "Yes") {
    	if (alarmthing) {
        	alarmthing.disarm()
        }
    }
    def msg = "User ${evt.value}: ${usernames[evt.value.toInteger()-1]} Unlocked ${evt.linkText}"
    log.debug msg
    if (pushnotify == "Yes") {
    	sendPush(msg)
    }
    if (phone1) {
    	sendSms(phone1,msg)
    }
    log.debug "Event User?: ${evt.user}"
}
