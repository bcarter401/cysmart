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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
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
import android.widget.GridView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.adapters.CapSenseButtonsGridAdapter;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.datamodels.CapSenseButtonsGridModel;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;
/**
 *  Fragment to display the CapSense Buttons
 *
 */
public class CapsenseServiceButtons extends Fragment {

	// GATT Services and characteristics
	public static BluetoothGattService mservice;
	public static BluetoothGattCharacteristic mNotifyCharacteristic;
	public static BluetoothGattCharacteristic mReadCharacteristic;

	// Data variables
	int mCount = 1;
	GridView mGridView;
	CapSenseButtonsGridAdapter mCapsenseButtonsAdapter;
	ArrayList<CapSenseButtonsGridModel> mData = new ArrayList<CapSenseButtonsGridModel>();
	ArrayList<Integer> mReceivedButtons = new ArrayList<Integer>();
	AlertDialog alert;

	/**
	 * BroadcastReceiver for receiving the GATT server status
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Bundle extras = intent.getExtras();

			// Disconnected
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Utils.connectionLostalertbox(getActivity(), alert);
				// Adding data to Data Logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_device_disconnected),
						getActivity().getApplicationContext());
			}
			// Data Available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

				// Check for CapSense buttons
				if (extras.containsKey(Constants.EXTRA_CAPBUTTONS_VALUE)) {
					mReceivedButtons = extras
							.getIntegerArrayList(Constants.EXTRA_CAPBUTTONS_VALUE);
					displayLiveData(mReceivedButtons);

				}
			}
		}
	};

	public CapsenseServiceButtons create(BluetoothGattService service) {
		CapsenseServiceButtons fragment = new CapsenseServiceButtons();
		mservice = service;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.capsense_buttons, container,
				false);
		mGridView = (GridView) rootView
				.findViewById(R.id.capsense_buttons_grid);
		setHasOptionsMenu(true);
		return rootView;
	}

	/**
	 * Display buttons data
	 * 
	 * @param button_data
	 */
	private void displayLiveData(ArrayList<Integer> button_data) {
		int buttonCount = button_data.get(0);
		fillButtons(buttonCount);
		setDataAdapter();
		mCapsenseButtonsAdapter.notifyDataSetChanged();
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_capsense_buttons),
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
		super.onDestroy();
		getActivity().unregisterReceiver(mGattUpdateReceiver);
	}

	/**
	 * Insert The Data
	 * 
	 * @param buttons
	 */
	private void fillButtons(int buttons) {
		mData.clear();
		for (int i = 0; i < buttons; i++) {
			mData.add(new CapSenseButtonsGridModel("" + (mCount + i),
					R.drawable.capsense_btn_bg));
		}
	}

	/**
	 * Set the Data Adapter
	 */
	private void setDataAdapter() {
		mCapsenseButtonsAdapter = new CapSenseButtonsGridAdapter(getActivity(),
				R.layout.carousel_fragment_item, mData, mReceivedButtons);
		mGridView.setAdapter(mCapsenseButtonsAdapter);
	}

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
