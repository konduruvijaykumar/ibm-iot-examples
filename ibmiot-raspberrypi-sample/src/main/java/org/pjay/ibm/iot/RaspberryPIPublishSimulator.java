/**
 * 
 */
package org.pjay.ibm.iot;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.DeviceClient;

/**
 * @author Vijay Konduru
 *
 */
public class RaspberryPIPublishSimulator {
	
	private final static String PROPERTIES_FILE_NAME = "/device.properties";
	private final static List<Float> temperatureDummyData = Arrays.asList(33F, 42F, 28F, 38F, 58F, 35F, 48F, 53F, 24F, 18F, 21F, 12F, 27F, 60F, 39F, 59F, 66F, 46F);
	private final static List<Float> humidityDummyData = Arrays.asList(32F, 61F, 29F, 47F, 59F, 36F, 56F, 67F, 25F, 19F, 22F, 43F, 26F, 13F, 37F, 49F, 51F, 34F);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		Random random = new Random();
		try {
			props.load(RaspberryPIPublishSimulator.class.getResourceAsStream(PROPERTIES_FILE_NAME));
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
		int numberOfIterations = 10;//15
		while(numberOfIterations > 0) {
			try {
				//Generate a JSON object of the event to be published
				JsonObject event = new JsonObject();
				event.addProperty("temperature", temperatureDummyData.get(random.nextInt(temperatureDummyData.size())));
				event.addProperty("humidity", humidityDummyData.get(random.nextInt(humidityDummyData.size())));
				
				status = myClient.publishEvent("raspberry-pi-sensors", event, 0);
				System.out.println(event);
				Thread.sleep(4000);
				numberOfIterations--;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!status) {
				System.out.println("Failed to publish the event......");
				System.exit(-1);
			}
		}
		// Added to terminate application, after number of iterations completed
		myClient.disconnect();
	}

}
