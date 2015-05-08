package com.cypress.cysmart.parsers;

import android.bluetooth.BluetoothGattCharacteristic;

import com.cypress.cysmart.utils.Logger;
/**
 * 
 * Class used for parsing Sensor hub related information
 */
public class SensorHubParser {
	public static final int FIRST_BITMASK = 0x01;
	public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
	public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
	public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
	public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
	public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
	public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
	public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

	public static int getAcceleroMeterXYZReading(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		int acc_xyz = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT16, 0);
		return acc_xyz;
	}

	public static float getThermometerReading(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		float temp = characteristic.getFloatValue(
				BluetoothGattCharacteristic.FORMAT_FLOAT, 0);
		return temp;
	}

	public static int getBarometerReading(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
			Logger.w("stringBuilder " + stringBuilder);
		}

		int pressure = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT16, 0);
		Logger.w("pressure " + pressure);
		return pressure;
	}

	public static int getSensorScanIntervalReading(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));

		}

		int scaninterval = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return scaninterval;
	}

	public static int getSensorTypeReading(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		int sensorType = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return sensorType;
	}

	public static int getFilterConfiguration(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		int filterConfiguration = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return filterConfiguration;
	}

	public static int getThresholdValue(
			BluetoothGattCharacteristic characteristic) {

		byte[] data = characteristic.getValue();
		StringBuilder stringBuilder = null;
		// writes the data formatted in HEX.
		if (data != null && data.length > 0) {
			stringBuilder = new StringBuilder(data.length);
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		int threshold = characteristic.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT16, 0);
		return threshold;
	}
}
