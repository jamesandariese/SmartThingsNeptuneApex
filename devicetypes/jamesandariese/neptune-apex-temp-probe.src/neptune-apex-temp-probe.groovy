/**
 *  Neptune Apex Temp Probe
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
	definition (name: "Neptune Apex Temp Probe", namespace: "jamesandariese", author: "James Andariese") {
		capability "Temperature Measurement"
        capability "Refresh"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state "val", label:'${currentValue}', defaultState: true
		}
		standardTile("flatIcon", "device.switch", width: 2, height: 2, decoration: "flat") {
			state "icon", action:"refresh.refresh", icon:"st.secondary.refresh", defaultState: true
		}
	}
}

// parse events into attributes
def parse(String description) {
	log.debug("parse($description)")
}

def refresh() {
	log.debug "Polling parent"
	parent.poll()
}

def updateFromApex(status) {
	log.debug "Update from Apex: $status"
	sendEvent(name: "temperature", value: status["value"])
}