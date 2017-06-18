/**
 * 
 */
package org.pjay.ibm.iot;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.DeviceClient;

/**
 * @author Vijay Konduru
 * 
 * Note: 
 * With the latest java library for Watson IoT Platform, a DeviceClient can only subscribe to CommandCallbacks and not EventCallbacks. 
 * However, an ApplicationClient can publish Events and Commands both.  publishEvent method will not trigger the CommandCallback
 * 
 */
public class DeviceCommandCallbackSubscriber {

	private final static String PROPERTIES_FILE_NAME = "/device.properties";
	//private final static String PROPERTIES_FILE_NAME = "C:\\JavaIoT\\ibm-iot-properties\\app.properties";
	
	/**
	 * @param args
	 * @throws MqttException 
	 */
	public static void main(String[] args) throws MqttException {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		//props = ApplicationClient.parsePropertiesFile(new File("C:\\JavaIoT\\ibm-iot-properties\\app1.properties"));		
		
		
		DeviceClient myClient = null;
		//ApplicationClient appClient = null;
		try {
			//Instantiate the class by passing the properties file
			// Does not have method other setting command callback, hence trying Application client
			//myClient = new DeviceClient(props);
			props.load(DeviceCommandCallbackSubscriber.class.getResourceAsStream(PROPERTIES_FILE_NAME));
			myClient = new DeviceClient(props);
			//appClient = new ApplicationClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Pass the implemented CommandCallback as an argument to this device client
		CommandCallbackSubscriber commandCallbackSubscriber = new CommandCallbackSubscriber();
		Thread t1 = new Thread(commandCallbackSubscriber);
		t1.start();
		
		//myClient.setCommandCallback(eventCallbackSubscriber);
		myClient.setCommandCallback(commandCallbackSubscriber);
		
		// Connect to the IBM Watson IoT Platform
		myClient.connect();
	}

}

class CommandCallbackSubscriber implements CommandCallback, Runnable {

	private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();
	private Command command = null;

	@Override
	public void processCommand(Command cmd) {
		try {
			commandQueue.put(cmd);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true){
			try {
				// Command related
				command = commandQueue.take();
				System.out.println("Command :: " + command.getCommand() + "\t Payload :: " + command.getData());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}