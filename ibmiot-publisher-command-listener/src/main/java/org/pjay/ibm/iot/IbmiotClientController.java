/**
 * 
 */
package org.pjay.ibm.iot;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.device.DeviceClient;

/**
 * @author Vijay Konduru
 *
 */
@RestController
@RequestMapping("/")
public class IbmiotClientController {
	
	@Autowired
	DeviceClient myClient;
	
	@Autowired
	ApplicationClient applicationClient;
	
	@Autowired
	CommandCallbackSubscriber commandCallbackSubscriber;
	
	@Value("${Device-Type}")
	String deviceType;
	@Value("${Device-ID}")
	String deviceID;
	
	// Default GET, so no need to mention
	//@RequestMapping(method={RequestMethod.GET})
	@RequestMapping()
	public String helloPage() {
		return "Welcome to IBM IoT Client App";
	}
	
	@RequestMapping("/ibmiot/appclient/connect")
	public String connectAppclient() {
		try {
			applicationClient.connect();
		} catch (MqttException e) {
			return "Failed to connected with IBM IoT application Client";
		}
		return "Connected to IBM IoT application Client";
	}
	
	// This is for testing purpose, in reality the device will be a IoT in the field. Which will receive command signals from Application Client 
	@RequestMapping("/ibmiot/deviceclient/connect")
	public String connectDeviceclient() {
		Thread t1 = new Thread(commandCallbackSubscriber);
		t1.start();
		myClient.setCommandCallback(commandCallbackSubscriber);
		
		// Connect to the IBM Watson IoT Platform
		try {
			myClient.connect();
		} catch (MqttException e) {
			return "Failed to connected with IBM IoT deviced Client";
		}
		return "Connected to IBM IoT deviced Client";
	}
	
	@RequestMapping("/ibmiot/lighton")
	public String lightOn() {
		LightOnOff lightOnOff = new LightOnOff(true);
		boolean status = false;
		System.out.println("lightOnOff Object :: " + lightOnOff);
		status = applicationClient.publishCommand(deviceType.trim(), deviceID.trim(), "light_on_off", lightOnOff);
		if(status){
			return "success";
		}else{
			return "failed";
		}
	}
	
	@RequestMapping("/ibmiot/lightoff")
	public String lightOff() {
		LightOnOff lightOnOff = new LightOnOff(false);
		boolean status = false;
		System.out.println("lightOnOff Object :: " + lightOnOff);
		status = applicationClient.publishCommand(deviceType.trim(), deviceID.trim(), "light_on_off", lightOnOff);
		if(status){
			return "success";
		}else{
			return "failed";
		}
	}

}
