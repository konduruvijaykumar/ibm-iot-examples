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
 * This sample shows how we can write a device client which publishes events, in a Registered mode, using a properties file <br>
 * It uses the Java Client Library for IBM Watson IoT Platform.
 * This sample code should be executed in a JRE running on the device.
 * 
 */

package org.pjay.ibm.iot;

import java.io.IOException;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.app.ApplicationClient;

public class RegisteredDeviceEventPublishPropertiesFile {

	public static void main(String[] args) throws MqttException, MalformedObjectNameException, InstanceNotFoundException, ReflectionException, IOException {
		//Provide the device specific data, as well as Auth-key and token using Properties class
		//A Sample properties file is provided in the src folder
		//Properties options = DeviceClient.parsePropertiesFile(new File("C:\\temp\\device.properties"));
		//Properties options = DeviceClient.parsePropertiesFile(new File("C:\\JavaIoT\\ibm-iot-properties\\app.properties"));
		Properties options = new Properties();
		options.load(RegisteredDeviceEventPublishPropertiesFile.class.getResourceAsStream("/app.properties"));

		//DeviceClient myClient = null;
		ApplicationClient myClient = null;
		try {
			//Instantiate the class by passing the properties file			
			//myClient = new DeviceClient(options);
			myClient = new ApplicationClient(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Connect to the IBM Watson IoT Platform	
		myClient.connect();
		
		SystemObject obj = new SystemObject();
		boolean status = true;
		
		//Generate a JSON object of the event to be published
		JsonObject command = new JsonObject();
//		event.addProperty("name", "foo");
//		event.addProperty("cpu",  90);
//		event.addProperty("mem",  70);
		command.addProperty("name", SystemObject.getName());
		command.addProperty("cpu",  obj.getProcessCpuLoad());
		command.addProperty("mem",  obj.getMemoryUsed());
		
		//Registered flow allows 0, 1 and 2 QoS
		//myClient.publishEvent("status", event, 1);
		status = myClient.publishCommand("<Your Devce Type>", "<Your Devce ID>", "cpu_load", command);
		status = myClient.publishEvent("<Your Devce Type>", "<Your Devce ID>", "cpu_load", command);
		System.out.println("SUCCESSFULLY POSTED......");

		//Disconnect cleanly
		myClient.disconnect();
	}
}
