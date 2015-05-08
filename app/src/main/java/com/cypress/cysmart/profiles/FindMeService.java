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
import java.util.HashMap;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

public class FindMeService extends Fragment {

	// Data view variables
	public static Spinner mSpinnerLinkLoss;
	public static Spinner mSpinnerImmediateAlert;
	ImageView mTransmissionPower;
	TextView mTransmissionPowerValue;
	View rootView;
	AlertDialog alert;

	// GATT service and characteristic
	public static BluetoothGattService mCurrentservice;
	public static ArrayList<HashMap<String, BluetoothGattService>> mExtraservice;
	public static BluetoothGattCharacteristic mReadCharacteristic_ll;
	public static BluetoothGattCharacteristic mReadCharacteristic_tp;

	// Immediate alert constants
	public static final int IMM_NO_ALERT = 0x00;
	public static final int IMM_MID_ALERT = 0x01;
	public static final int IMM_HIGH_ALERT = 0x02;

	// Immediate alert text
	public static final String IMM_NO_ALERT_TEXT = "No Alert";
	public static final String IMM_MID_ALERT_TEXT = "Mid Alert";
	public static final String IMM_HIGH_ALERT_TEXT = "High Alert";

	public static boolean mTransmissionSet = false;

	/**
	 * BroadcastReceiver for receiving the GATT server status
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
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
			// GATT Data available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// Check alert level
				if (extras.containsKey(Constants.EXTRA_ALERT_VALUE)) {
					String received_alt_data = intent
							.getStringExtra(Constants.EXTRA_ALERT_VALUE);

					if (!received_alt_data.equalsIgnoreCase(" ")) {
						int val = Integer.parseInt(received_alt_data);
						updateSpinnerItem(val);
						if (mReadCharacteristic_tp != null) {
							prepareBroadcastDataReadtp(mReadCharacteristic_tp);
						}
					}

				}
				// Check power value
				if (extras.containsKey(Constants.EXTRA_POWER_VALUE)) {
					int received_pwr_data = intent.getIntExtra(
							Constants.EXTRA_POWER_VALUE, 246);
					mTransmissionSet = true;
					prepareBroadcastDataReadtp(mReadCharacteristic_tp);
					if (received_pwr_data != 246) {
						float value = received_pwr_data;
						float flval = (float) 1 / 120;
						float scaleVal = (value + 100) * flval;
						Logger.i("scaleVal " + scaleVal);
						mTransmissionPower.animate().setDuration(500)
								.scaleX(scaleVal);
						mTransmissionPower.animate().setDuration(500)
								.scaleY(scaleVal);
						mTransmissionPowerValue.setText(String
								.valueOf(received_pwr_data));

					}

				}

			}

		}
	};

	public FindMeService create(
			BluetoothGattService currentservice,
			ArrayList<HashMap<String, BluetoothGattService>> gattExtraServiceData) {
		FindMeService fragment = new FindMeService();
		mCurrentservice = currentservice;
		mExtraservice = gattExtraServiceData;
		return fragment;
	}

	/**
	 * Update the spinner item
	 * 
	 * @param val
	 */
	protected void updateSpinnerItem(int val) {
		mSpinnerLinkLoss.setSelection(val);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.profile_findme, container, false);
		getActivity().getActionBar().setTitle(R.string.find_immediate_alert);
		setHasOptionsMenu(true);
		return rootView;
	}

	/**
	 * Prepare Broadcast receiver to broadcast read characteristics LinkLoss
	 * 
	 * @param gattCharacteristic
	 */

	protected void prepareBroadcastDataReadll(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			mReadCharacteristic_ll = characteristic;
			BluetoothLeService.readCharacteristic(characteristic);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_read_start),
				getActivity().getApplicationContext());
	}

	/**
	 * Prepare Broadcast receiver to broadcast read characteristics Transmission
	 * power
	 * 
	 * @param gattCharacteristic
	 */
	protected void prepareBroadcastDataReadtp(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();
		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			mReadCharacteristic_tp = characteristic;
			BluetoothLeService.readCharacteristic(characteristic);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_read_start),
				getActivity().getApplicationContext());
	}

	@Override
	public void onResume() {
		getGattData();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.findme_fragment));
		super.onResume();
	}

	/**
	 * Method to get required characteristics from service
	 */
	private void getGattData() {
		LinearLayout ll_layout = (LinearLayout) rootView
				.findViewById(R.id.linkloss_layout);
		LinearLayout im_layout = (LinearLayout) rootView
				.findViewById(R.id.immalert_layout);
		LinearLayout tp_layout = (LinearLayout) rootView
				.findViewById(R.id.transmission_layout);
		RelativeLayout tpr_layout = (RelativeLayout) rootView
				.findViewById(R.id.transmission_rel_layout);
		LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		for (int position = 0; position < mExtraservice.size(); position++) {
			HashMap<String, BluetoothGattService> item = mExtraservice
					.get(position);
			BluetoothGattService bgs = item.get("UUID");
			List<BluetoothGattCharacteristic> gattCharacteristicsCurrent = bgs
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicsCurrent) {
				String uuidchara = gattCharacteristic.getUuid().toString();
				if (uuidchara.equalsIgnoreCase(GattAttributes.ALERT_LEVEL)) {
					if (bgs.getUuid().toString()
							.equalsIgnoreCase(GattAttributes.LINK_LOSS_SERVICE)) {
						ll_layout.setVisibility(View.VISIBLE);
						mReadCharacteristic_ll = gattCharacteristic;
						prepareBroadcastDataReadll(gattCharacteristic);
						mSpinnerLinkLoss = (Spinner) rootView
								.findViewById(R.id.linkloss_spinner);
						// Create an ArrayAdapter using the string array and a
						// default
						// spinner layout
						ArrayAdapter<CharSequence> adapter_linkloss = ArrayAdapter
								.createFromResource(getActivity(),
										R.array.findme_immediate_alert_array,
										android.R.layout.simple_spinner_item);
						// Specify the layout to use when the list of choices
						// appears
						adapter_linkloss
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// Apply the adapter to the spinner
						mSpinnerLinkLoss.setAdapter(adapter_linkloss);
						mSpinnerLinkLoss
								.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {

										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("No Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_NO_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_NO_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}
										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("Mid Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_MID_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_MID_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}
										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("High Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_HIGH_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_HIGH_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}

									}

									@Override
									public void onNothingSelected(
											AdapterView<?> parent) {
										// TODO Auto-generated method stub

									}
								});
					}
					if (bgs.getUuid()
							.toString()
							.equalsIgnoreCase(
									GattAttributes.IMMEDIATE_ALERT_SERVICE)) {
						im_layout.setVisibility(View.VISIBLE);
						mSpinnerImmediateAlert = (Spinner) rootView
								.findViewById(R.id.immediate_spinner);
						// Create an ArrayAdapter using the string array and a
						// default
						// spinner layout
						ArrayAdapter<CharSequence> adapter_immediate_alert = ArrayAdapter
								.createFromResource(getActivity(),
										R.array.findme_immediate_alert_array,
										android.R.layout.simple_spinner_item);
						// Specify the layout to use when the list of choices
						// appears
						adapter_immediate_alert
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// Apply the adapter to the spinner
						mSpinnerImmediateAlert
								.setAdapter(adapter_immediate_alert);
						mSpinnerImmediateAlert
								.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long id) {

										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("No Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_NO_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_NO_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}
										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("Mid Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_MID_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_MID_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}
										if (parent.getItemAtPosition(position)
												.toString()
												.equalsIgnoreCase("High Alert")) {
											BluetoothLeService
													.writeCharacteristicNoresponse(
															gattCharacteristic,
															IMM_HIGH_ALERT);
											Toast.makeText(
													getActivity(),
													"Written "
															+ IMM_HIGH_ALERT_TEXT
															+ " successfully",
													Toast.LENGTH_SHORT).show();
										}

									}

									@Override
									public void onNothingSelected(
											AdapterView<?> parent) {
										// TODO Auto-generated method stub

									}
								});
					}

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.TRANSMISSION_POWER_LEVEL)) {
					tp_layout.setVisibility(View.VISIBLE);
					tpr_layout.setVisibility(View.VISIBLE);
					mReadCharacteristic_tp = gattCharacteristic;
					mTransmissionPower = (ImageView) rootView
							.findViewById(R.id.findme_tx_power_img);
					mTransmissionPowerValue = (TextView) rootView
							.findViewById(R.id.findme_tx_power_txt);
					if (mReadCharacteristic_ll == null) {
						prepareBroadcastDataReadtp(mReadCharacteristic_tp);
					}

				}

			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mReadCharacteristic_ll = null;
		mReadCharacteristic_tp = null;
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	};

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
