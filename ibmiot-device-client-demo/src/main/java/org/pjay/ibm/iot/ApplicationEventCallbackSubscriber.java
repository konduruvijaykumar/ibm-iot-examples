/**
 * 
 */
package org.pjay.ibm.iot;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;

/**
 * @author Vijay Konduru
 * 
 * Note: 
 * With the latest java library for Watson IoT Platform, a DeviceClient can only subscribe to CommandCallbacks and not EventCallbacks. 
 * However, an ApplicationClient can publish Events and Commands both.  publishEvent method will not trigger the CommandCallback
 * This code is not receiving even call back and hence not able to get data
 * 
 */
public class ApplicationEventCallbackSubscriber {

	//private final static String PROPERTIES_FILE_NAME = "/device.properties";
	//private final static String PROPERTIES_FILE_NAME = "C:\\JavaIoT\\ibm-iot-properties\\app.properties";
	private final static String PROPERTIES_FILE_NAME = "/app1.properties";
	
	/**
	 * @param args
	 * @throws MqttException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws MqttException, IOException {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		//props = ApplicationClient.parsePropertiesFile(new File("C:\\JavaIoT\\ibm-iot-properties\\app1.properties"));
		props.load(ApplicationEventCallbackSubscriber.class.getResourceAsStream(PROPERTIES_FILE_NAME));
		
		
		//DeviceClient myClient = null;
		ApplicationClient appClient = null;
		try {
			//Instantiate the class by passing the properties file
			// Does not have method other setting command callback, hence trying Application client
			//myClient = new DeviceClient(props);
			//myClient = new DeviceClient(props);
			appClient = new ApplicationClient(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Pass the implemented EventCallback as an argument to this device client
		EventCallbackSubscriber eventCallbackSubscriber = new EventCallbackSubscriber();
		Thread t1 = new Thread(eventCallbackSubscriber);
		t1.start();
		
		//myClient.setCommandCallback(eventCallbackSubscriber);
		appClient.setEventCallback(eventCallbackSubscriber);
		
		// Connect to the IBM Watson IoT Platform
		appClient.connect();
	}

}

class EventCallbackSubscriber implements EventCallback, Runnable {

	private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();
	private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>();
	private Command command = null;
	private Event event = null;
	
	@Override
	public void processEvent(Event evt) {
		try {
			eventQueue.put(evt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processCommand(Command cmd) {
		try {
			commandQueue.put(cmd);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true){
			try {
				// Command related
				command = commandQueue.take();
				System.out.println(" :: Command :: " + command.getCommand() + " :: Payload :: " + command.getData());
				
				// Event related
				event = eventQueue.take();
				System.out.println(" :: Event :: " + event.getEvent() + " :: Payload :: " + event.getData());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}