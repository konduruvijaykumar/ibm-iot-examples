/**
 * 
 */
package org.pjay.ibm.iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.DeviceClient;

/**
 * @author Vijay Konduru
 *
 */
public class RaspberryPIPublish {
	
	private final static String PROPERTIES_FILE_NAME = "/device.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		try {
			props.load(RaspberryPIPublish.class.getResourceAsStream(PROPERTIES_FILE_NAME));
		} catch (IOException e1) {
			System.err.println("Not able to read the properties file, exiting..");
			System.exit(-1);
		}		
		
		DeviceClient myClient = null;
		try {
			//Instantiate and connect to IBM Watson IoT Platform
			myClient = new DeviceClient(props);
			myClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		boolean status = true;
		int numberOfIterations = 30;//15
		// Main logic for PI with python code run using runtime process
		try {
			// Process pythonProcess = Runtime.getRuntime().exec("python3 /home/pi/PROJECT-DSI/sensingDHT22.py");
			String line = "";
			BufferedReader bufferedReader = null;
			String[] splitArray = null;
			for (;numberOfIterations > 0;) {
				// Process pythonProcess = Runtime.getRuntime().exec("python3 /home/pi/PROJECT-DSI/sensingDHT22.py");
				Process pythonProcess = Runtime.getRuntime().exec("python3 /home/pi/PROJECT-DSI/DHT22SensorDemo.py");
				bufferedReader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
				while (null != (line = bufferedReader.readLine())) {
					System.out.println("Data humidity,temperature is :: " + line);
					splitArray = line.split(",");
					// Generate a JSON object of the event to be published
					JsonObject event = new JsonObject();
					event.addProperty("temperature", Float.parseFloat(splitArray[1]));
					event.addProperty("humidity", Float.parseFloat(splitArray[0]));
					status = myClient.publishEvent("raspberry-pi-sensors", event, 0);
					System.out.println(event);
					Thread.sleep(2000);
				}
				numberOfIterations--;
				pythonProcess.destroy();
				// bufferedReader.close();
			}
			bufferedReader.close();
			// Thread.sleep(6000);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		if (!status) {
			System.out.println("Failed to publish the event......");
			System.exit(-1);
		}

		// Added to terminate application, after number of iterations completed
		myClient.disconnect();
	}

}
