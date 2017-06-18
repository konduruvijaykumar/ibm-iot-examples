/**
 * 
 */
package org.pjay.ibm.iot;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;

/**
 * @author Vijay Konduru
 *
 */
public class CommandCallbackSubscriber implements CommandCallback, Runnable {

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
		while (true) {
			try {
				// Command related
				command = commandQueue.take();
				System.out.println("Command :: " + command.getCommand() + "\t Payload :: " + command.getData());
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = jsonParser.parse(command.getData().toString()).getAsJsonObject();
				// JsonObject jsonObject = new JsonObject();
				if (jsonObject.get("lightOn").getAsBoolean()) {
					System.out.println("Light ON");
				} else {
					System.out.println("Light OFF");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
