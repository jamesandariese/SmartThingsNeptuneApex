/**
 *  Neptune Apex Outlet
 *
 *  Copyright 2017 James Andariese
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

metadata {
	definition (name: "Neptune Apex Outlet", namespace: "jamesandariese", author: "James Andariese") {
		capability "Switch"
        capability "Outlet"
        capability "Polling"
        capability "Refresh"
        
        command "auto"
        
        attribute "apexOutputName", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
        	state "off", label:'off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
		    state "on", label:'on', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
    		state "turningOn", label:'Turning on', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState: "turningOff"
    		state "turningOff", label:'Turning off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState: "turningOn"
           	state "autoOn", label:'on (auto)', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
           	state "autoOff", label:'off (auto)', action:"switch.off", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOff"
		}
        standardTile("auto", "device.switch", width:1, height:1, canChangeIcon: true) {
            state "auto", label: 'auto', action: "auto", nextState:"turningAuto"
        }
        standardTile("off", "device.switch", width:1, height:1, canChangeIcon: true) {
            state "off", icon:"st.switches.switch.off", label: 'off', action: "off"
        }
        standardTile("on", "device.switch", width:1, height:1, canChangeIcon: true) {
            state "on", icon:"st.switches.switch.on", label: 'on', action: "on"
        }
        standardTile("flatIcon", "device.switch", width: 2, height: 2, decoration: "flat") {
			state "icon", action:"refresh.refresh", icon:"st.secondary.refresh", defaultState: true
		}

	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

// handle commands
def on() {
	log.debug "Executing 'on'"
	parent.outletOn(device.deviceNetworkId)
}

def off() {
	log.debug "Executing 'off'"
	parent.outletOff(device.deviceNetworkId)
}

def auto() {
    log.debug "Executing 'auto'"
    parent.outletAuto(device.deviceNetworkId)
    sendEvent("switch", "turningAuto")
}

def updateFromApex(status) {
	// looks like {"status": ["AON",""],"name": "Pump","gid": "","type": "outlet","ID": 0,"did": "base_Out1"}

    log.debug "${device.deviceNetworkId} - updateFromApex($status)"
    
    if (status.did != device.deviceNetworkId) {
        log.error "received update for the wrong did.  I'm ${device.deviceNetworkId}."
        return
    }
    
    def statusName = status["status"][0]
    
    if (statusName == "AON") {
        sendEvent(name: "switch", value: "autoOn")
    } else if (statusName == "AOF") {
        sendEvent(name: "switch", value: "autoOff")
    } else if (statusName == "ON") {
        sendEvent(name: "switch", value: "on")
    } else if (statusName == "OFF") {
        sendEvent(name: "switch", value: "off")
    } else {
    	log.error "unknown switch status: ${statusName}.  expecting one of AON, AOF, ON, or OFF."
    }
    sendEvent(name: "apexOutputName", value: status["name"])
}

def refresh() {
	log.debug "Executing 'refresh'"
    parent.poll()
}

def poll() {
	log.debug "Executing 'poll'"
    refresh()
}
