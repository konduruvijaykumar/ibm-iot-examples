/**
 *****************************************************************************
 * Copyright (c) 2016 IBM Corporation and other Contributors.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Amit M Mangalvedkar - Initial Contribution
 *****************************************************************************
 */

/**
 * This sample shows how we can write a device client which publishes events, in a Quickstart mode <br>
 * It uses the Java Client Library for IBM Watson IoT Platform
 * This sample code should be executed in a JRE running on the device
 * 
 */

package org.pjay.ibm.iot;

import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.DeviceClient;

public class QuickstartDeviceEventPublish {

	public static void main(String[] args) throws MqttException {
		
		//Provide the device specific data using Properties class
		Properties options = new Properties();
		options.setProperty("org", "quickstart");
		options.setProperty("type", "iotsample-arduino");
		options.setProperty("id", "00aabbccde03");
		
		DeviceClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			myClient = new DeviceClient(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Connect to the IBM Watson IoT Platform
		myClient.connect();
		
		//Generate a JSON object of the event to be published
		JsonObject event = new JsonObject();
		event.addProperty("name", "foo");
		event.addProperty("cpu",  75);
		event.addProperty("mem",  63);
		
		//Quickstart flow allows only QoS = 0
		myClient.publishEvent("status", event, 0);
		System.out.println("SUCCESSFULLY POSTED......");

		//Disconnect cleanly
		myClient.disconnect();
	}
}
