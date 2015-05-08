package com.cypress.cysmart.parsers;

import java.util.ArrayList;

import android.R.integer;
import android.bluetooth.BluetoothGattCharacteristic;

import com.cypress.cysmart.utils.Logger;

/**
 * 
 * Class used for parsing CapSense related information
 */
public class CapSenseParser {
	/**
	 * Parse the CapSense proximity value from the characteristic
	 * @param characteristic
	 * @return {@link integer}
	 */
	public static int getCapSenseProximity(
			BluetoothGattCharacteristic characteristic) {
		int proximityValue = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return proximityValue;
	}
	/**
	 * Parse the CapSense slider value from the characteristic
	 * @param characteristic
	 * @return {@link integer}
	 */
	public static int getCapSenseSlider(
			BluetoothGattCharacteristic characteristic) {
		int sliderValue = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return sliderValue;

	}
	/**
	 * Parse the CapSense buttons value from the characteristic
	 * @param characteristic
	 * @return {@link ArrayList}
	 */

	public static ArrayList<Integer> getCapSenseButtons(
			BluetoothGattCharacteristic characteristic) {
		ArrayList<Integer> buttonParams = new ArrayList<Integer>();
		int buttonCount = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		int buttonStatus1 = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 1);
		int buttonStatus2 = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 2);

		Logger.i("Button count" + buttonCount + "Button status "
				+ buttonStatus1 + "Button status " + buttonStatus2);
		buttonParams.add(buttonCount);
		buttonParams.add(buttonStatus1);
		buttonParams.add(buttonStatus2);
		return buttonParams;
	}
}
