/**
 * 
 */
package org.pjay.ibm.iot;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.DeviceClient;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author Vijay Konduru
 * 
 * https://stackoverflow.com/questions/5245840/how-to-convert-string-to-jsonobject-in-java
 * https://stackoverflow.com/questions/19400867/string-to-jsonobject-in-java
 *
 */
public class Pi4JLightOnOffSubscriber {
	
	private final static String PROPERTIES_FILE_NAME = "/device.properties";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		  * Load device properties
		  */
		Properties props = new Properties();
		
		DeviceClient myClient = null;
		try {
			//Instantiate the class by passing the properties file
			props.load(Pi4JLightOnOffSubscriber.class.getResourceAsStream(PROPERTIES_FILE_NAME));
			myClient = new DeviceClient(props);
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
		try {
			myClient.connect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}

class CommandCallbackSubscriber implements CommandCallback, Runnable {

	private BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();
	private Command command = null;
	
	// create gpio controller
	final static GpioController gpio = GpioFactory.getInstance();
	// provision gpio pin #01 as an output pin and turn on
	final static GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED", PinState.LOW);

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
		pin.setShutdownOptions(true, PinState.LOW);
		while(true){
			try {
				// Command related
				command = commandQueue.take();
				System.out.println("Command :: " + command.getCommand() + "\t Payload :: " + command.getData());
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = jsonParser.parse(command.getData().toString()).getAsJsonObject();
				//JsonObject jsonObject = new JsonObject();
				if(jsonObject.get("lightOn").getAsBoolean()){
					System.out.println("Light ON");
					pin.high();
				}else{
					System.out.println("Light OFF");
					pin.low();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
