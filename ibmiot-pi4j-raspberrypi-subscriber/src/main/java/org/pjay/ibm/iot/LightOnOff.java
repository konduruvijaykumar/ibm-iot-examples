/**
 * 
 */
package org.pjay.ibm.iot;

/**
 * @author Vijay Konduru
 * 
 * Not used here, dealing with google gson JsonObject
 *
 */
public class LightOnOff {

	private boolean lightOn;

	public LightOnOff() {
	}

	public LightOnOff(boolean lightOn) {
		this.lightOn = lightOn;
	}

	public boolean isLightOn() {
		return lightOn;
	}

	public void setLightOn(boolean lightOn) {
		this.lightOn = lightOn;
	}

	@Override
	public String toString() {
		return "LightOnOff [lightOn=" + lightOn + "]";
	}

}
