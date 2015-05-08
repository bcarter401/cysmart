/*
 * Copyright Cypress Semiconductor Corporation, 2014 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.cypress.cysmart.backgroundservices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.cypress.cysmart.parsers.BloodPressureParser;
import com.cypress.cysmart.parsers.CSCParser;
import com.cypress.cysmart.parsers.CapSenseParser;
import com.cypress.cysmart.parsers.GlucoseParser;
import com.cypress.cysmart.parsers.HRMParser;
import com.cypress.cysmart.parsers.HTMParser;
import com.cypress.cysmart.parsers.RGBParser;
import com.cypress.cysmart.parsers.RSCParser;
import com.cypress.cysmart.parsers.SensorHubParser;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.UUIDDatabase;
import com.cypress.cysmart.utils.Utils;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given BlueTooth LE device.
 */
public class BluetoothLeService extends Service {

	/**
	 * Flag to check the mBound status
	 */
	public boolean mBound;

	/**
	 * BlueTooth manager for handling connections
	 */
	private BluetoothManager mBluetoothManager;
	/**
	 * BluetoothAdapter for handling connections
	 */
	private static BluetoothAdapter mBluetoothAdapter;
	/**
	 * Device address
	 */
	private static String mBluetoothDeviceAddress;
	private static BluetoothGatt mBluetoothGatt;

	/**
	 * Connection status Constants
	 */
	public static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	public static int mConnectionState = STATE_DISCONNECTED;

	/**
	 * GATT Status constants
	 */
	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

	private static Context mContext;
	/**
	 * Implements callback methods for GATT events that the app cares about. For
	 * example,connection change and services discovered.
	 * 
	 */

	private final static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			// GATT Server connected
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastConnectionUpdate(intentAction);
				Logger.datalog(" Connected to GATT server " + gatt.getDevice(),
						mContext);
			}
			// GATT Server disconnected
			else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Logger.datalog(" Disconnected from GATT server.", mContext);
				broadcastConnectionUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// GATT Services discovered
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastConnectionUpdate(ACTION_GATT_SERVICES_DISCOVERED);
				Logger.datalog(" Service discovery status " + status, mContext);
			} else {
				Logger.datalog(" Service discovery status " + status, mContext);

			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// GATT Characteristic read
			if (status == BluetoothGatt.GATT_SUCCESS) {
				UUID charUuid = characteristic.getUuid();
				final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
				Bundle mBundle = new Bundle();
				// Putting the byte value read for GATT Db
				mBundle.putByteArray(Constants.EXTRA_BYTE_VALUE,
						characteristic.getValue());
				// Body sensor location read value
				if (charUuid.equals(UUIDDatabase.UUID_BODY_SENSOR_LOCATION)) {
					mBundle.putString(Constants.EXTRA_BSL_VALUE,
							HRMParser.getBodySensorLocation(characteristic));
				}
				// Manufacture name read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_MANUFATURE_NAME_STRING)) {
					mBundle.putString(Constants.EXTRA_MNS_VALUE,
							Utils.getManufacturerNameString(characteristic));
				}
				// Model number read value
				else if (charUuid.equals(UUIDDatabase.UUID_MODEL_NUMBER_STRING)) {
					mBundle.putString(Constants.EXTRA_MONS_VALUE,
							Utils.getModelNumberString(characteristic));
				}
				// Serial number read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_SERIAL_NUMBER_STRING)) {
					mBundle.putString(Constants.EXTRA_SNS_VALUE,
							Utils.getSerialNumberString(characteristic));
				}
				// Hardware revision read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_HARDWARE_REVISION_STRING)) {
					mBundle.putString(Constants.EXTRA_HRS_VALUE,
							Utils.getHardwareRevisionString(characteristic));
				}
				// Software revision read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_SOFTWARE_REVISION_STRING)) {
					mBundle.putString(Constants.EXTRA_SRS_VALUE,
							Utils.getSoftwareRevisionString(characteristic));
				}
				// Battery level read value
				else if (charUuid.equals(UUIDDatabase.UUID_BATTERY_LEVEL)) {
					mBundle.putString(Constants.EXTRA_BTL_VALUE,
							Utils.getBatteryLevel(characteristic));
				}
				// PNP ID read value
				else if (charUuid.equals(UUIDDatabase.UUID_PNP_ID)) {
					mBundle.putString(Constants.EXTRA_PNP_VALUE,
							Utils.getPNPID(characteristic));
				}
				// System ID read value
				else if (charUuid.equals(UUIDDatabase.UUID_SYSTEM_ID)) {
					mBundle.putString(Constants.EXTRA_SID_VALUE,
							Utils.getSYSID(characteristic));
				}
				// Health thermometer sensor location read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_HEALTH_THERMOMETER_SENSOR_LOCATION)) {
					mBundle.putString(Constants.EXTRA_HSL_VALUE, HTMParser
							.getHealthThermoSensorLocation(characteristic));
				}
				// CapSense proximity read value
				else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_PROXIMITY)) {
					mBundle.putInt(Constants.EXTRA_CAPPROX_VALUE,
							CapSenseParser.getCapSenseProximity(characteristic));
				}
				// CapSense slider read value
				else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_SLIDER)) {
					mBundle.putInt(Constants.EXTRA_CAPSLIDER_VALUE,
							CapSenseParser.getCapSenseSlider(characteristic));
				}
				// CapSense buttons read value
				else if (charUuid.equals(UUIDDatabase.UUID_CAPSENSE_BUTTONS)) {
					mBundle.putIntegerArrayList(
							Constants.EXTRA_CAPBUTTONS_VALUE,
							CapSenseParser.getCapSenseButtons(characteristic));
				}
				// Alert level read value
				else if (charUuid.equals(UUIDDatabase.UUID_ALERT_LEVEL)) {
					mBundle.putString(Constants.EXTRA_ALERT_VALUE,
							Utils.getAlertLevel(characteristic));
				}
				// Transmission power level read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_TRANSMISSION_POWER_LEVEL)) {
					mBundle.putInt(Constants.EXTRA_POWER_VALUE,
							Utils.getTransmissionPower(characteristic));
				}
				// RGB Led read value
				else if (charUuid.equals(UUIDDatabase.UUID_RGB_LED)) {
					mBundle.putString(Constants.EXTRA_RGB_VALUE,
							RGBParser.getRGBValue(characteristic));
				}
				// Glucose read value
				else if (charUuid.equals(UUIDDatabase.UUID_GLUCOSE)) {
					mBundle.putStringArrayList(Constants.EXTRA_GLUCOSE_VALUE,
							GlucoseParser.getGlucoseHealth(characteristic));
				}
				// Running speed read value
				else if (charUuid.equals(UUIDDatabase.UUID_RSC_MEASURE)) {
					mBundle.putStringArrayList(Constants.EXTRA_RSC_VALUE,
							RSCParser.getRunningSpeednCadence(characteristic));
				}
				// Accelerometer X read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_READING_X)) {
					mBundle.putInt(Constants.EXTRA_ACCX_VALUE, SensorHubParser
							.getAcceleroMeterXYZReading(characteristic));
				}
				// Accelerometer Y read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_READING_Y)) {
					mBundle.putInt(Constants.EXTRA_ACCY_VALUE, SensorHubParser
							.getAcceleroMeterXYZReading(characteristic));
				}
				// Accelerometer Z read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_READING_Z)) {
					mBundle.putInt(Constants.EXTRA_ACCZ_VALUE, SensorHubParser
							.getAcceleroMeterXYZReading(characteristic));
				}
				// Temperature read value
				else if (charUuid.equals(UUIDDatabase.UUID_TEMPERATURE_READING)) {
					mBundle.putFloat(Constants.EXTRA_STEMP_VALUE,
							SensorHubParser
									.getThermometerReading(characteristic));
				}
				// Barometer read value
				else if (charUuid.equals(UUIDDatabase.UUID_BAROMETER_READING)) {
					mBundle.putInt(Constants.EXTRA_SPRESSURE_VALUE,
							SensorHubParser.getBarometerReading(characteristic));
				}
				// Accelerometer scan interval read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_SENSOR_SCAN_INTERVAL)) {
					mBundle.putInt(
							Constants.EXTRA_ACC_SENSOR_SCAN_VALUE,
							SensorHubParser
									.getSensorScanIntervalReading(characteristic));
				}
				// Accelerometer analog sensor read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_ANALOG_SENSOR)) {
					mBundle.putInt(Constants.EXTRA_ACC_SENSOR_TYPE_VALUE,
							SensorHubParser
									.getSensorTypeReading(characteristic));
				}
				// Accelerometer data accumulation read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_ACCELEROMETER_DATA_ACCUMULATION)) {
					mBundle.putInt(Constants.EXTRA_ACC_FILTER_VALUE,
							SensorHubParser
									.getFilterConfiguration(characteristic));
				}
				// Temperature sensor scan read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_TEMPERATURE_SENSOR_SCAN_INTERVAL)) {
					mBundle.putInt(
							Constants.EXTRA_STEMP_SENSOR_SCAN_VALUE,
							SensorHubParser
									.getSensorScanIntervalReading(characteristic));
				}
				// Temperature analog sensor read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_TEMPERATURE_ANALOG_SENSOR)) {
					mBundle.putInt(Constants.EXTRA_STEMP_SENSOR_TYPE_VALUE,
							SensorHubParser
									.getSensorTypeReading(characteristic));
				}
				// Barometer sensor scan interval read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_BAROMETER_SENSOR_SCAN_INTERVAL)) {
					mBundle.putInt(
							Constants.EXTRA_SPRESSURE_SENSOR_SCAN_VALUE,
							SensorHubParser
									.getSensorScanIntervalReading(characteristic));
				}
				// Barometer digital sensor
				else if (charUuid
						.equals(UUIDDatabase.UUID_BAROMETER_DIGITAL_SENSOR)) {
					mBundle.putInt(Constants.EXTRA_SPRESSURE_SENSOR_TYPE_VALUE,
							SensorHubParser
									.getSensorTypeReading(characteristic));
				}
				// Barometer threshold for indication read value
				else if (charUuid
						.equals(UUIDDatabase.UUID_BAROMETER_THRESHOLD_FOR_INDICATION)) {
					mBundle.putInt(Constants.EXTRA_SPRESSURE_THRESHOLD_VALUE,
							SensorHubParser.getThresholdValue(characteristic));
				}
				intent.putExtras(mBundle);

				/**
				 * Sending the broad cast so that it can be received on
				 * registered receivers
				 */

				mContext.sendBroadcast(intent);

			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastNotifyUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	private static void broadcastConnectionUpdate(final String action) {
		final Intent intent = new Intent(action);
		mContext.sendBroadcast(intent);
	}

	private static void broadcastNotifyUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		Bundle mBundle = new Bundle();
		mBundle.putByteArray(Constants.EXTRA_BYTE_VALUE,
				characteristic.getValue());
		// Heart rate Measurement notify value
		if (UUIDDatabase.UUID_HEART_RATE_MEASUREMENT.equals(characteristic
				.getUuid())) {
			String heart_rate = HRMParser.getHeartRate(characteristic);
			String energy_expended = HRMParser
					.getEnergyExpended(characteristic);
			ArrayList<Integer> rrintervel = HRMParser
					.getRRInterval(characteristic);
			mBundle.putString(Constants.EXTRA_HRM_VALUE, heart_rate);
			mBundle.putString(Constants.EXTRA_HRM_EEVALUE, energy_expended);
			mBundle.putIntegerArrayList(Constants.EXTRA_HRM_RRVALUE, rrintervel);
		}
		// Health thermometer notify value
		if (UUIDDatabase.UUID_HEALTH_THERMOMETER.equals(characteristic
				.getUuid())) {
			String health_temp = HTMParser.getHealthThermo(characteristic);
			mBundle.putString(Constants.EXTRA_HTM_VALUE, health_temp);
		}
		if
		// CapSense Proximity notify value
		(UUIDDatabase.UUID_CAPSENSE_PROXIMITY.equals(characteristic.getUuid())) {
			int capsense_proximity = CapSenseParser.getCapSenseProximity(characteristic);
			mBundle.putInt(Constants.EXTRA_CAPPROX_VALUE, capsense_proximity);
		}
		// CapSense slider notify value
		if (UUIDDatabase.UUID_CAPSENSE_SLIDER.equals(characteristic.getUuid())) {
			Logger.i("CapSense Slider");
			int capsense_slider = CapSenseParser.getCapSenseSlider(characteristic);
			mBundle.putInt(Constants.EXTRA_CAPSLIDER_VALUE, capsense_slider);
			intent.putExtras(mBundle);
		}
		// CapSense buttons notify value
		if (UUIDDatabase.UUID_CAPSENSE_BUTTONS.equals(characteristic.getUuid())) {
			ArrayList<Integer> capsense_buttons = CapSenseParser
					.getCapSenseButtons(characteristic);
			mBundle.putIntegerArrayList(Constants.EXTRA_CAPBUTTONS_VALUE,
					capsense_buttons);
			intent.putExtras(mBundle);
		}
		// Glucose notify value
		if (UUIDDatabase.UUID_GLUCOSE.equals(characteristic.getUuid())) {
			Bundle glucose_attributes = new Bundle();
			ArrayList<String> glucose_values = GlucoseParser
					.getGlucoseHealth(characteristic);
			mBundle.putStringArrayList(Constants.EXTRA_GLUCOSE_VALUE,
					glucose_values);
			intent.putExtras(glucose_attributes);
		}
		// Blood pressure measurement notify value
		if (UUIDDatabase.UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic
				.getUuid())) {
			Bundle blood_pressure_attributes = new Bundle();
			String blood_pressure_systolic = BloodPressureParser
					.getSystolicBloodPressure(characteristic, mContext);
			String blood_pressure_diastolic = BloodPressureParser
					.getDiaStolicBloodPressure(characteristic, mContext);
			String blood_pressure_systolic_unit = BloodPressureParser
					.getSystolicBloodPressureUnit(characteristic, mContext);
			String blood_pressure_diastolic_unit = BloodPressureParser
					.getDiaStolicBloodPressureUnit(characteristic, mContext);
			blood_pressure_attributes.putString(
					Constants.EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE,
					blood_pressure_systolic_unit);
			blood_pressure_attributes.putString(
					Constants.EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE,
					blood_pressure_diastolic_unit);
			blood_pressure_attributes.putString(
					Constants.EXTRA_PRESURE_SYSTOLIC_VALUE,
					blood_pressure_systolic);
			blood_pressure_attributes.putString(
					Constants.EXTRA_PRESURE_DIASTOLIC_VALUE,
					blood_pressure_diastolic);
			intent.putExtras(blood_pressure_attributes);
		}
		// Running speed measurement notify value
		if (UUIDDatabase.UUID_RSC_MEASURE.equals(characteristic.getUuid())) {
			Bundle rsc_attributes = new Bundle();
			ArrayList<String> rsc_values = RSCParser
					.getRunningSpeednCadence(characteristic);
			mBundle.putStringArrayList(Constants.EXTRA_RSC_VALUE, rsc_values);
			intent.putExtras(rsc_attributes);
		}
		// Cycling speed Measurement notify value
		if (UUIDDatabase.UUID_CSC_MEASURE.equals(characteristic.getUuid())) {
			Bundle csc_attributes = new Bundle();
			ArrayList<String> csc_values = CSCParser
					.getCyclingSpeednCadence(characteristic);
			mBundle.putStringArrayList(Constants.EXTRA_CSC_VALUE, csc_values);
			intent.putExtras(csc_attributes);
		}
		// Accelerometer x notify value
		if (UUIDDatabase.UUID_ACCELEROMETER_READING_X.equals(characteristic
				.getUuid())) {
			Bundle accx_attributes = new Bundle();
			mBundle.putInt(Constants.EXTRA_ACCX_VALUE,
					SensorHubParser.getAcceleroMeterXYZReading(characteristic));
			intent.putExtras(accx_attributes);
		}
		// Accelerometer Y notify value
		if (UUIDDatabase.UUID_ACCELEROMETER_READING_Y.equals(characteristic
				.getUuid())) {
			Bundle accx_attributes = new Bundle();
			mBundle.putInt(Constants.EXTRA_ACCY_VALUE,
					SensorHubParser.getAcceleroMeterXYZReading(characteristic));
			intent.putExtras(accx_attributes);
		}
		// Accelerometer Z notify value
		if (UUIDDatabase.UUID_ACCELEROMETER_READING_Z.equals(characteristic
				.getUuid())) {
			Bundle accx_attributes = new Bundle();
			mBundle.putInt(Constants.EXTRA_ACCZ_VALUE,
					SensorHubParser.getAcceleroMeterXYZReading(characteristic));
			intent.putExtras(accx_attributes);
		}
		// Temperature notify value
		if (UUIDDatabase.UUID_TEMPERATURE_READING.equals(characteristic
				.getUuid())) {
			Bundle stemp_attributes = new Bundle();
			mBundle.putFloat(Constants.EXTRA_STEMP_VALUE,
					SensorHubParser.getThermometerReading(characteristic));
			intent.putExtras(stemp_attributes);
		}
		// Barometer notify value
		if (UUIDDatabase.UUID_BAROMETER_READING
				.equals(characteristic.getUuid())) {
			Logger.i("SPressure BLESERVICE");
			Bundle spressure_attributes = new Bundle();
			mBundle.putInt(Constants.EXTRA_SPRESSURE_VALUE,
					SensorHubParser.getBarometerReading(characteristic));
			intent.putExtras(spressure_attributes);
		}
		intent.putExtras(mBundle);
		/**
		 * Sending the broad cast so that it can be received on registered
		 * receivers
		 */

		mContext.sendBroadcast(intent);
	}

	/**
	 * 
	 * Local binder class
	 */
	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		mBound = true;
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mBound = false;
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local BlueTooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {

				Logger.datalog(" Unable to initialize BluetoothManager.",
						getApplicationContext());

				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {

			Logger.datalog(" Unable to obtain a BluetoothAdapter.",
					getApplicationContext());
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the BlueTooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public static boolean connect(final String address, Context context) {
		mContext = context;
		if (mBluetoothAdapter == null || address == null) {

			Logger.datalog(
					" BluetoothAdapter not initialized or unspecified address.",
					context);
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {

			Logger.datalog(
					" Trying to use an existing mBluetoothGatt for connection.",
					context);
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public static void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	public static void discoverServices() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		} else {
			mBluetoothGatt.discoverServices();
		}

	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public static void readCharacteristic(
			BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Request a write with no response on a given
	 * {@code BluetoothGattCharacteristic}.
	 * 
	 * @param characteristic
	 * @param value
	 *            to write
	 */
	public static void writeCharacteristicNoresponse(
			BluetoothGattCharacteristic characteristic, int value) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		} else {
			byte[] valueByte = new byte[1];
			valueByte[0] = (byte) value;
			characteristic.setValue(valueByte);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}

	}

	/**
	 * Request a write on a given {@code BluetoothGattCharacteristic}.
	 * 
	 * @param characteristic
	 * @param byteArray
	 */

	public void writeCharacteristicGattDb(
			BluetoothGattCharacteristic characteristic, byte[] byteArray) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		} else {
			byte[] valueByte = byteArray;
			characteristic.setValue(valueByte);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}

	}

	/**
	 * Request a write on a given {@code BluetoothGattCharacteristic} for RGB.
	 * 
	 * @param characteristic
	 * @param red
	 * @param green
	 * @param blue
	 * @param intensity
	 */
	public static void writeCharacteristicRGB(
			BluetoothGattCharacteristic characteristic, int red, int green,
			int blue, int intensity) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		} else {
			byte[] valueByte = new byte[4];
			valueByte[0] = (byte) red;
			valueByte[1] = (byte) green;
			valueByte[2] = (byte) blue;
			valueByte[3] = (byte) intensity;
			characteristic.setValue(valueByte);
			mBluetoothGatt.writeCharacteristic(characteristic);
		}

	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public static void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		if (characteristic.getDescriptor(UUID
				.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}

	}

	/**
	 * Enables or disables indications on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable indications. False otherwise.
	 */
	public static void setCharacteristicIndication(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		// Logger.w(TAG + characteristic.getUuid());
		if (UUIDDatabase.UUID_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic
				.getUuid())) {
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
			if (mBluetoothGatt.writeDescriptor(descriptor)) {
				Logger.d("Written descriptor successfully");
			}

		}
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public static List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	public static int getConnectionState() {

		return mConnectionState;
	}

	@Override
	public void onCreate() {
		Logger.d("Service created");
		// Initializing the service
		if (!initialize()) {
			Logger.d("Service not initialized");
		}
	}
}