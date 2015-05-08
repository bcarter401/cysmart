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

package com.cypress.cysmart.profiles;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Fragment to show the glucose service
 */
public class GlucoseService extends Fragment {

	// GATT Service and characteristics
	public static BluetoothGattService mservice;
	public static BluetoothGattCharacteristic mNotifyCharacteristic;

	// Data view variables
	private TextView mGlucoceConcentration;
	private TextView mGlucoseRecordTime;
	private TextView mGlucoseType;
	private TextView mGlucoseSampleLocation;
	private TextView mGlucoseConcentrationUnit;
	AlertDialog alert;
	/**
	 * BroadcastReceiver for receiving the GATT server status
	 */
	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Bundle extras = intent.getExtras();
			// GATT Disconnected
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Utils.connectionLostalertbox(getActivity(), alert);
				// Adding data to Data Logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_device_connected),
						getActivity().getApplicationContext());
			}
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (extras.containsKey(Constants.EXTRA_GLUCOSE_VALUE)) {
					ArrayList<String> received_glucose_data = intent
							.getStringArrayListExtra(Constants.EXTRA_GLUCOSE_VALUE);
					displayLiveData(received_glucose_data);
				}

			}

		}
	};

	public GlucoseService create(BluetoothGattService service) {
		GlucoseService fragment = new GlucoseService();
		mservice = service;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.glucose_measurement,
				container, false);
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.parent);
		mGlucoceConcentration = (TextView) rootView
				.findViewById(R.id.glucose_measure);
		mGlucoseRecordTime = (TextView) rootView
				.findViewById(R.id.recording_time_data);
		mGlucoseType = (TextView) rootView.findViewById(R.id.glucose_type);
		mGlucoseSampleLocation = (TextView) rootView
				.findViewById(R.id.glucose_sample_location);
		mGlucoseConcentrationUnit = (TextView) rootView
				.findViewById(R.id.glucose_unit);
		mGlucoceConcentration.setSelected(true);
		mGlucoseRecordTime.setSelected(true);
		mGlucoseType.setSelected(true);
		mGlucoseSampleLocation.setSelected(true);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		setHasOptionsMenu(true);
		// getGattData();
		return rootView;
	}

	private void displayLiveData(ArrayList<String> glucose_data) {
		if (glucose_data != null) {
			Logger.datalog(
					getResources()
							.getString(
									R.string.data_logger_characteristic_glucose_concentration)
							+ glucose_data.get(0).toString(), getActivity());
			Logger.datalog(
					getResources().getString(
							R.string.data_logger_characteristic_glucose_type)
							+ glucose_data.get(1).toString(), getActivity());
			Logger.datalog(
					getResources()
							.getString(
									R.string.data_logger_characteristic_glucose_recording_time)
							+ glucose_data.get(3).toString(), getActivity());
			mGlucoceConcentration.setText(glucose_data.get(0).toString());
			mGlucoseType.setText(glucose_data.get(1).toString());
			mGlucoseSampleLocation.setText(glucose_data.get(2).toString());
			mGlucoseRecordTime.setText(glucose_data.get(3).toString());
			mGlucoseConcentrationUnit.setText(glucose_data.get(4).toString());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getGattData();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		/**
		 * Initializes ActionBar as required
		 */
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.glucose_fragment));

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mNotifyCharacteristic != null) {
			stopBroadcastDataNotify(mNotifyCharacteristic);
		}
		getActivity().unregisterReceiver(mGattUpdateReceiver);
	};

	/**
	 * Stopping Broadcast receiver to broadcast notify characteristics
	 * 
	 * @param gattCharacteristic
	 */
	public void stopBroadcastDataNotify(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			if (mNotifyCharacteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
			}

		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_notify_stop),
				getActivity().getApplicationContext());
	}

	/**
	 * Preparing Broadcast receiver to broadcast notify characteristics
	 * 
	 * @param gattCharacteristic
	 */
	protected void prepareBroadcastDataNotify(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			if (mNotifyCharacteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
			}
			mNotifyCharacteristic = characteristic;
			BluetoothLeService.setCharacteristicNotification(characteristic,
					true);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_notify_start),
				getActivity().getApplicationContext());
	}

	/**
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();

			if (uuidchara.equalsIgnoreCase(GattAttributes.GLUCOSE_COCNTRN)) {
				mNotifyCharacteristic = gattCharacteristic;
				prepareBroadcastDataNotify(mNotifyCharacteristic);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.global, menu);
		MenuItem graph = menu.findItem(R.id.graph);
		MenuItem log = menu.findItem(R.id.log);
		graph.setVisible(false);
		log.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
