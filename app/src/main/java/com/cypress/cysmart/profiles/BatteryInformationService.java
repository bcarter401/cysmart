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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Fragment to show the battery information service
 * 
 */
public class BatteryInformationService extends Fragment {

	// Data fields
	private TextView mBatteryLevelText;
	private ImageView mBatteryImageBack;
	private ImageView mBatteryImageFront;
	private Button mUpdateLevel;

	// Service and characteristics
	public static BluetoothGattService mservice;
	public static BluetoothGattCharacteristic mReadCharacteristic;

	// Battery level string
	public static String mBatteryLevel;

	// Alert dialog
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
								R.string.data_logger_device_disconnected),
						getActivity().getApplicationContext());
			}
			// GATT Data available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// Check for battery information
				if (extras.containsKey(Constants.EXTRA_BTL_VALUE)) {
					String received_btl_data = intent
							.getStringExtra(Constants.EXTRA_BTL_VALUE);

					if (!received_btl_data.equalsIgnoreCase(" ")) {
						mBatteryLevel = received_btl_data;
						displayBatteryLevel(received_btl_data);

					}

				}

			}

		}

	};

	/**
	 * Method to display the battery level
	 * 
	 * @param received_btl_data
	 */
	private void displayBatteryLevel(String received_btl_data) {
		mBatteryLevelText.setText(received_btl_data + "%");
		int battery = Integer.parseInt(received_btl_data);
		int batteryFinished = 100 - battery;
		mBatteryImageBack.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, batteryFinished));
		mBatteryImageFront.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, battery));
		// Adding data to data logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_btl_value),
				getActivity().getApplicationContext());
	}

	public BatteryInformationService create(BluetoothGattService service) {
		BatteryInformationService fragment = new BatteryInformationService();
		mservice = service;
		return fragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.battery_info_fragment,
				container, false);

		mBatteryLevelText = (TextView) rootView
				.findViewById(R.id.battery_level);
		mBatteryImageBack = (ImageView) rootView
				.findViewById(R.id.battery_icon);
		mBatteryImageFront = (ImageView) rootView
				.findViewById(R.id.battery_indication);
		mUpdateLevel = (Button) rootView
				.findViewById(R.id.battery_level_update);
		mUpdateLevel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getGattData();
			}
		});
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		return rootView;
	}

	/**
	 * Preparing Broadcast receiver to broadcast read characteristics
	 * 
	 * @param gattCharacteristic
	 */
	protected void prepareBroadcastDataRead(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			mReadCharacteristic = characteristic;
			BluetoothLeService.readCharacteristic(characteristic);
			// Adding data to data logger
			Logger.datalog(
					getResources().getString(
							R.string.data_logger_characteristic_read_start),
					getActivity().getApplicationContext());
		}
	}

	@Override
	public void onResume() {
		getGattData();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.battery_info_fragment));
		super.onResume();
	}

	@Override
	public void onPause() {
		// getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	};

	/**
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();
			if (uuidchara.equalsIgnoreCase(GattAttributes.BATTERY_LEVEL)) {
				mReadCharacteristic = gattCharacteristic;
				prepareBroadcastDataRead(gattCharacteristic);
				break;
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
