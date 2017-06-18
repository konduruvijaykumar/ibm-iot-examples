/**
 * 
 */
package org.pjay.ibm.iot;

import java.util.Properties;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.ibm.iotf.client.app.ApplicationClient;

/**
 * @author Vijay Konduru
 *
 */
public class AppClientDeviceCommandPublisher {

	private final static String PROPERTIES_FILE_NAME = "/application.properties";
	
	/**
	 * @param args
	 * @throws MqttException 
	 */
	public static void main(String[] args) throws MqttException {
		
		//Properties options = DeviceClient.parsePropertiesFile(new File("C:\\JavaIoT\\ibm-iot-properties\\app.properties"));
		Properties props = new Properties();
		
		ApplicationClient appClient = null;
		try {
			//Instantiate the class by passing the properties file
			props.load(AppClientDeviceCommandPublisher.class.getResourceAsStream(PROPERTIES_FILE_NAME));
			appClient = new ApplicationClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Connect to the IBM Watson IoT Platform	
		appClient.connect();
		
		LightOnOff lightOnOff = null;//new LightOnOff(false);
		boolean status = false;
		int numberOfIterations = 10;
		Random random = new Random();
		
		while(numberOfIterations > 0){
			lightOnOff = new LightOnOff(random.nextBoolean());
			System.out.println("lightOnOff Object :: " + lightOnOff);
			//status = appClient.publishCommand("Your Device Type", "Your Device ID", "cpu_load", lightOnOff);
			status = appClient.publishCommand(props.getProperty("Device-Type").trim(), props.getProperty("Device-ID").trim(), "light_on_off", lightOnOff);
			if(status)
				System.out.println("SUCCESSFULLY POSTED......");
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			numberOfIterations--;
		}
		
		//Disconnect cleanly
		appClient.disconnect();
	}

}
