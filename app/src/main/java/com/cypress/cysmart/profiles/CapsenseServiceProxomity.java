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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Fragment to display the CapSense Proximity
 * 
 */
public class CapsenseServiceProxomity extends Fragment {

	// GATT Service
	public static BluetoothGattService mservice;

	// Data variables
	ImageView mproximityViewForeground;
	ImageView mproximityViewBackground;
	AlertDialog alert;
/**
 *  BroadcastReceiver for receiving the GATT server status
 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// GATT Disconnected
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Utils.connectionLostalertbox(getActivity(), alert);
				// Adding data to Data Logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_device_disconnected),
						getActivity().getApplicationContext());
			}
			// GATT Data Available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				Bundle extras = intent.getExtras();
				if (extras.containsKey(Constants.EXTRA_CAPPROX_VALUE)) {
					int received_proximity_rate = extras
							.getInt(Constants.EXTRA_CAPPROX_VALUE);
					displayLiveData(received_proximity_rate);

				}

			}
		}
	};

	public CapsenseServiceProxomity create(BluetoothGattService service) {
		CapsenseServiceProxomity fragment = new CapsenseServiceProxomity();
		mservice = service;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.capsense_proximity,
				container, false);
		mproximityViewForeground = (ImageView) rootView
				.findViewById(R.id.proximity_view_1);
		mproximityViewBackground = (ImageView) rootView
				.findViewById(R.id.proximity_view_2);
		setHasOptionsMenu(true);
		return rootView;
	}

	/**
	 * Display the proximity value
	 * 
	 * @param proximity_value
	 */
	private void displayLiveData(int proximity_value) {
		int priximity2value = 255 - proximity_value;
		mproximityViewBackground.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, proximity_value));
		mproximityViewForeground.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, priximity2value));
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_device_disconnected),
				getActivity().getApplicationContext());
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.global, menu);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		MenuItem graph = menu.findItem(R.id.graph);
		MenuItem log = menu.findItem(R.id.log);
		graph.setVisible(false);
		log.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
