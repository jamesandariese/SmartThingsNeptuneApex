/**
 *  Neptune Apex Feed Mode
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
	definition (name: "Neptune Apex Feed Mode", namespace: "jamesandariese", author: "James Andariese") {
		capability "Switch"
        capability "Momentary"
        capability "Refresh"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
        	state "off", label:'off', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
		    state "on", label:'on', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
    		state "turningOn", label:'Turning on', icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState: "turningOff"
    		state "turningOff", label:'Turning off', icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState: "turningOn"
		}
        standardTile("momentary", "device.momentary", width:1, height:1, canChangeIcon: true) {
            state "push", label: 'activate', action: "momentary.push"
        }
        standardTile("flatIcon", "device.switch", width: 2, height: 2, decoration: "flat") {
			state "icon", action:"refresh.refresh", icon:"st.secondary.refresh", defaultState: true
		}

	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute

}

// handle commands
def on() {
	log.debug "Executing 'on'"
	parent.feedMode(device.deviceNetworkId.split('_')[1])
}

def off() {
	log.debug "Executing 'off'"
	parent.feedMode(null)
}

def push() {
	on()
}

def updateFromApex(feedStatus) {
	// looks like {name: 6, active: 43673}
    log.debug "${device.deviceNetworkId} - updateFromApex($feedStatus)"
    if ((int)feedStatus.name == 0) {
    	// 0 means off
    	sendEvent(name: "switch", value: "off")
        return
    }
    def dniNumber = ((int)feedStatus.name) - 1
    def dniComp = "feed_$dniNumber"
    log.debug "is ${device.deviceNetworkId} == $dniComp?"
    log.debug (device.deviceNetworkId == dniComp)
    if (device.deviceNetworkId == dniComp) {
    	sendEvent(name: "switch", value: "on")
    } else {
    	sendEvent(name: "switch", value: "off")
    }
}

def refresh() {
    parent.poll()
}