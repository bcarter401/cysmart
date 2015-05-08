package com.cypress.cysmart.parsers;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * 
 * Class used for parsing Health temperature related information
 */
public class HTMParser {
	/**
	 * Get the thermometer reading
	 * 
	 * @param characteristic
	 * @return
	 */
	public static String getHealthThermo(
			BluetoothGattCharacteristic characteristic) {
		int flag = characteristic.getProperties();
		int format = -1;
		if ((flag & 0x01) != 0) {
			format = BluetoothGattCharacteristic.FORMAT_FLOAT;

		} else {
			format = BluetoothGattCharacteristic.FORMAT_FLOAT;

		}
		final Float heartRate = characteristic.getFloatValue(format, 2);
		return String.format("%f", heartRate);
	}

	/**
	 * Get the thermometer sensor location
	 * 
	 * @param characteristic
	 * @return
	 */
	public static String getHealthThermoSensorLocation(
			BluetoothGattCharacteristic characteristic) {
		String health_thermo_sensor_location = "";
		final byte[] data = characteristic.getValue();
		if (data != null && data.length > 0) {
			final StringBuilder stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			int health_body_sensor = Integer.valueOf(stringBuilder.toString()
					.trim());

			switch (health_body_sensor) {
			case 01:
				health_thermo_sensor_location = "Armpit";
				break;
			case 02:
				health_thermo_sensor_location = "Body (general)";
				break;
			case 03:
				health_thermo_sensor_location = "Ear (usually ear lobe)";
				break;
			case 04:
				health_thermo_sensor_location = "Finger";
				break;
			case 05:
				health_thermo_sensor_location = "Gastro-intestinal Tract";
				break;
			case 06:
				health_thermo_sensor_location = "Mouth";
				break;
			case 07:
				health_thermo_sensor_location = "Rectum";
				break;
			case 8:
				health_thermo_sensor_location = "Tympanum (ear drum)";
				break;
			case 9:
				health_thermo_sensor_location = "Toe";
				break;
			case 10:
				health_thermo_sensor_location = "Toe";
				break;
			default:
				health_thermo_sensor_location = "Reserved for future use";
				break;
			}

		}
		return health_thermo_sensor_location;
	}
}
