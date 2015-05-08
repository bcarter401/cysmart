package com.cypress.cysmart.parsers;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Class to parse the RGB service related information
 */
public class RGBParser {
	/**
	 * Parsing the RGB value from the characteristic
	 * @param characteristics
	 * @return {@link String}
	 */
	public static String getRGBValue(BluetoothGattCharacteristic characteristics) {
		int red = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		int green = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 1);
		int blue = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 2);
		int intensity = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 3);
		return String.valueOf(red + "," + green + "," + blue + "," + intensity);
	}
}
