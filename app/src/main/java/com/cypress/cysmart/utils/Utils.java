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

package com.cypress.cysmart.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.View;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;

/**
 * Class for commonly used methods in the project
 * 
 */
public class Utils {

	// Shared preference constant
	static String SHARED_PREF_NAME = "CySmart Shared Preference";

	/**
	 * Returns the manufacture name from the given characteristic
	 * 
	 * @param characteristic
	 * @return manfacture_name_string
	 */
	public static String getManufacturerNameString(
			BluetoothGattCharacteristic characteristic) {
		String manfacture_name_string = characteristic.getStringValue(0);
		return manfacture_name_string;
	}

	/**
	 * Returns the model number from the given characteristic
	 * 
	 * @param characteristic
	 * @return model_name_string
	 */

	public static String getModelNumberString(
			BluetoothGattCharacteristic characteristic) {
		String model_name_string = characteristic.getStringValue(0);

		return model_name_string;
	}

	/**
	 * Returns the serial number from the given characteristic
	 * 
	 * @param characteristic
	 * @return serial_number_string
	 */
	public static String getSerialNumberString(
			BluetoothGattCharacteristic characteristic) {
		String serial_number_string = characteristic.getStringValue(0);

		return serial_number_string;
	}

	/**
	 * Returns the serial number from the given characteristic
	 * 
	 * @param characteristic
	 * @return hardware_revision_name_string
	 */
	public static String getHardwareRevisionString(
			BluetoothGattCharacteristic characteristic) {
		String hardware_revision_name_string = characteristic.getStringValue(0);

		return hardware_revision_name_string;
	}

	/**
	 * Returns the software revision number from the given characteristic
	 * 
	 * @param characteristic
	 * @return hardware_revision_name_string
	 */
	public static String getSoftwareRevisionString(
			BluetoothGattCharacteristic characteristic) {
		String hardware_revision_name_string = characteristic.getStringValue(0);

		return hardware_revision_name_string;
	}

	/**
	 * Returns the PNP ID from the given characteristic
	 * 
	 * @param characteristic
	 * @return {@link String}
	 */
	public static String getPNPID(BluetoothGattCharacteristic characteristic) {
		final byte[] data = characteristic.getValue();
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		if (data != null && data.length > 0) {
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		return String.valueOf(stringBuilder);
	}

	/**
	 * Returns the SystemID from the given characteristic
	 * 
	 * @param characteristic
	 * @return {@link String}
	 */
	public static String getSYSID(BluetoothGattCharacteristic characteristic) {
		final byte[] data = characteristic.getValue();
		final StringBuilder stringBuilder = new StringBuilder(data.length);
		if (data != null && data.length > 0) {
			for (byte byteChar : data)
				stringBuilder.append(String.format("%02X ", byteChar));
		}

		return String.valueOf(stringBuilder);
	}

	/**
	 * Adding the necessary INtent filters for Broadcast receivers
	 * 
	 * @return {@link IntentFilter}
	 */
	public static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		return intentFilter;
	}

	/**
	 * Converting the Byte to binary
	 * 
	 * @param bytes
	 * @return {@link String}
	 */
	public static String BytetoBinary(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}

	/**
	 * Returns the battery level information from the characteristics
	 * 
	 * @param characteristics
	 * @return {@link String}
	 */
	public static String getBatteryLevel(
			BluetoothGattCharacteristic characteristics) {
		int battery_level = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return String.valueOf(battery_level);
	}

	/**
	 * Returns the Alert level information from the characteristics
	 * 
	 * @param characteristics
	 * @return {@link String}
	 */
	public static String getAlertLevel(
			BluetoothGattCharacteristic characteristics) {
		int alert_level = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		return String.valueOf(alert_level);
	}

	/**
	 * Returns the Transmission power information from the characteristic
	 * 
	 * @param characteristics
	 * @return {@link integer}
	 */
	public static int getTransmissionPower(
			BluetoothGattCharacteristic characteristics) {
		int power_level = characteristics.getIntValue(
				BluetoothGattCharacteristic.FORMAT_SINT8, 0);
		return power_level;
	}

	/**
	 * Get the data from milliseconds
	 * 
	 * @return {@link String}
	 */
	public static String GetDateFromMilliseconds() {
		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
		Calendar calendar = Calendar.getInstance();
		return formatter.format(calendar.getTime());

	}

	/**
	 * Get the date
	 * 
	 * @return {@link String}
	 */
	public static String GetDate() {
		DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
		Calendar calendar = Calendar.getInstance();
		return formatter.format(calendar.getTime());

	}

	/**
	 * Get the seven days before date
	 * 
	 * @return {@link String}
	 */

	public static String GetDateSevenDaysBack() {
		DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -7);
		return formatter.format(calendar.getTime());

	}

	/**
	 * Get the time from milliseconds
	 * 
	 * @return {@link String}
	 */
	public static String GetTimeFromMilliseconds() {
		DateFormat formatter = new SimpleDateFormat("HH:mm ss SSS");
		Calendar calendar = Calendar.getInstance();
		return formatter.format(calendar.getTime());

	}

	/**
	 * Get time and date
	 * 
	 * @return {@link String}
	 */

	public static String GetTimeandDate() {
		DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm ss");
		Calendar calendar = Calendar.getInstance();
		return formatter.format(calendar.getTime());

	}

	/**
	 * Setting the shared preference with values provided as parameters
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static final void setStringSharedPreference(Context context,
			String key, String value) {
		SharedPreferences goaPref = context.getSharedPreferences(
				SHARED_PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = goaPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Returning the stored values in the shared preference with values provided
	 * as parameters
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static final String getStringSharedPreference(Context context,
			String key) {
		if (context != null) {

			SharedPreferences Pref = context.getSharedPreferences(
					SHARED_PREF_NAME, Context.MODE_PRIVATE);
			String value = Pref.getString(key, "");
			return value;

		} else {
			return "";
		}
	}

	/**
	 * Take the screen shot of the device
	 * 
	 * @param view
	 */
	public static void screenShotMethod(View view) {
		Bitmap bitmap;
		if (view != null) {
			View v1 = view;
			v1.setDrawingCacheEnabled(true);
			v1.buildDrawingCache(true);
			bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File f = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "temporary_file.jpg");
			try {
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Alert dialog to display when the GATT Server is disconnected from the
	 * client
	 * 
	 * @param context
	 * @param alert
	 */

	public static void connectionLostalertbox(final Activity context,
			AlertDialog alert) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(
				context.getResources().getString(
						R.string.alert_message_reconnect))
				.setCancelable(false)
				.setTitle(context.getResources().getString(R.string.app_name))
				.setPositiveButton(
						context.getResources().getString(
								R.string.alert_message_exit_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intentActivity = context.getIntent();
								context.finish();
								context.overridePendingTransition(
										R.anim.slide_left, R.anim.push_left);
								context.startActivity(intentActivity);
								context.overridePendingTransition(
										R.anim.slide_right, R.anim.push_right);
							}
						});
		alert = builder.create();
		alert.setCanceledOnTouchOutside(false);
		alert.show();
	}

	/**
	 * Setting up the action bar with values provided as parameters
	 * 
	 * @param context
	 * @param title
	 */
	public static void setUpActionBar(Activity context, String title) {
		ActionBar actionBar = context.getActionBar();
		actionBar.setIcon(new ColorDrawable(context.getResources().getColor(
				android.R.color.transparent)));
		actionBar.setTitle(title);
	}

	/**
	 * Check whether Internet connection is enabled on the device
	 * @param context
	 * @return
	 */
	public static final boolean checkNetwork(Context context) {
		if (context != null) {
			boolean result = true;
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
				result = false;
			}
			return result;
		} else {
			return false;
		}
	}
}
