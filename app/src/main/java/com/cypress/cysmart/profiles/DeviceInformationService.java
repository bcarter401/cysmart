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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Fragment to display the Device information service
 * 
 */
public class DeviceInformationService extends Fragment {

	// GATT Service and Characteristics
	public static BluetoothGattService mservice;
	private static BluetoothGattCharacteristic mReadCharacteristic;

	// Data view variables
	TextView mManufacturerName;
	TextView mModelName;
	TextView mSerialName;
	TextView mHardwareRevisionName;
	TextView mSoftwareRevisionName;
	TextView mpnpid;
	TextView mSysid;
	AlertDialog alert;

	// Flag for data set
	public static boolean mManufacturerSet = false;
	public static boolean mmModelNumberSet = false;
	public static boolean mSerialNumberSet = false;
	public static boolean mHardwareNumberSet = false;
	public static boolean mSoftwareNumberSet = false;
	public static boolean mPnpidSet = false;
	public static boolean mSystemidSet = false;

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
			else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// Check manufacture name
				if (extras.containsKey(Constants.EXTRA_MNS_VALUE)) {
					String received_mns_data = intent
							.getStringExtra(Constants.EXTRA_MNS_VALUE);
					if (!received_mns_data.equalsIgnoreCase(" ")) {
						if (!mManufacturerSet) {
							mManufacturerSet = true;
							displayManufactureName(received_mns_data);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.MODEL_NUMBER_STRING)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}

				}
				// Check model number
				if (extras.containsKey(Constants.EXTRA_MONS_VALUE)) {
					String received_mons_data = intent
							.getStringExtra(Constants.EXTRA_MONS_VALUE);
					if (!received_mons_data.equalsIgnoreCase(" ")) {
						if (!mmModelNumberSet) {
							mmModelNumberSet = true;
							displayModelNumber(received_mons_data);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.SERIAL_NUMBER_STRING)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}
				}
				// Check serial number
				if (extras.containsKey(Constants.EXTRA_SNS_VALUE)) {
					String received_sns_data = intent
							.getStringExtra(Constants.EXTRA_SNS_VALUE);
					if (!received_sns_data.equalsIgnoreCase(" ")) {
						if (!mSerialNumberSet) {
							mSerialNumberSet = true;
							displaySerialNumber(received_sns_data);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.HARDWARE_REVISION_STRING)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}
				}
				// check hardware revision
				if (extras.containsKey(Constants.EXTRA_HRS_VALUE)) {
					String received_hrs_data = intent
							.getStringExtra(Constants.EXTRA_HRS_VALUE);
					if (!received_hrs_data.equalsIgnoreCase(" ")) {
						if (!mHardwareNumberSet) {
							mHardwareNumberSet = true;
							displayhardwareNumber(received_hrs_data);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.SOFTWARE_REVISION_STRING)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}
				}
				// Check software revision
				if (extras.containsKey(Constants.EXTRA_SRS_VALUE)) {
					String received_srs_data = intent
							.getStringExtra(Constants.EXTRA_SRS_VALUE);
					if (!received_srs_data.equalsIgnoreCase(" ")) {
						if (!mSoftwareNumberSet) {
							mSoftwareNumberSet = true;
							displaySoftwareNumber(received_srs_data);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.PNP_ID)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}
				}
				// Check PNP
				if (extras.containsKey(Constants.EXTRA_PNP_VALUE)) {
					String received_pnpid = intent
							.getStringExtra(Constants.EXTRA_PNP_VALUE);
					if (!received_pnpid.equalsIgnoreCase(" ")) {
						if (!mPnpidSet) {
							mPnpidSet = true;
							displayPnpId(received_pnpid);
							List<BluetoothGattCharacteristic> mgatt = mservice
									.getCharacteristics();
							for (BluetoothGattCharacteristic gattCharacteristic : mgatt) {
								String uuidchara = gattCharacteristic.getUuid()
										.toString();
								if (uuidchara
										.equalsIgnoreCase(GattAttributes.SYSTEM_ID)) {
									Logger.i("Characteristic " + uuidchara);
									mReadCharacteristic = gattCharacteristic;
									prepareBroadcastDataRead(gattCharacteristic);

								}

							}
						}

					}

				}
				// Check software id
				if (extras.containsKey(Constants.EXTRA_SID_VALUE)) {
					String received_sid_value = intent
							.getStringExtra(Constants.EXTRA_SID_VALUE);
					if (!received_sid_value.equalsIgnoreCase(" ")) {
						if (!mSystemidSet) {
							displaySystemid(received_sid_value);
							mReadCharacteristic = null;
							mSystemidSet = true;

						}

					}
				}

			}

		}
	};

	public DeviceInformationService create(BluetoothGattService service) {
		DeviceInformationService fragment = new DeviceInformationService();
		mservice = service;
		return fragment;
	}

	/**
	 * Display SystemID
	 * 
	 * @param received_sid_value
	 */

	protected void displaySystemid(String received_sid_value) {
		mSysid.setText(received_sid_value);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_system_id),
				getActivity().getApplicationContext());
	}

	/**
	 * Display PNPID
	 * 
	 * @param received_pnpid
	 */
	protected void displayPnpId(String received_pnpid) {
		mpnpid.setText(received_pnpid);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_pnp_id),
				getActivity().getApplicationContext());
	}

	/**
	 * Display Software revision number
	 * 
	 * @param received_srs_data
	 */
	protected void displaySoftwareNumber(String received_srs_data) {
		mSoftwareRevisionName.setText(received_srs_data);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_software_number),
				getActivity().getApplicationContext());
	}

	/**
	 * Display hardware revision number
	 * 
	 * @param received_hrs_data
	 */
	protected void displayhardwareNumber(String received_hrs_data) {
		mHardwareRevisionName.setText(received_hrs_data);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_hardware_number),
				getActivity().getApplicationContext());
	}

	/**
	 * Display serial number
	 * 
	 * @param received_sns_data
	 */
	protected void displaySerialNumber(String received_sns_data) {
		mSerialName.setText(received_sns_data);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_serial_number),
				getActivity().getApplicationContext());
	}

	/**
	 * Display model number
	 * 
	 * @param received_mons_data
	 */
	protected void displayModelNumber(String received_mons_data) {
		mModelName.setText(received_mons_data);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_model_number),
				getActivity().getApplicationContext());

	}

	/**
	 * Display manufacture name
	 * 
	 * @param received_mns_data
	 */

	protected void displayManufactureName(String received_mns_data) {
		mManufacturerName.setText(received_mns_data);
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_manufacture_name),
				getActivity().getApplicationContext());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.device_information_measurement, container, false);
		mManufacturerName = (TextView) rootView
				.findViewById(R.id.div_manufacturer);
		mModelName = (TextView) rootView.findViewById(R.id.div_model);
		mSerialName = (TextView) rootView.findViewById(R.id.div_serial);
		mHardwareRevisionName = (TextView) rootView
				.findViewById(R.id.div_hardware);
		mSoftwareRevisionName = (TextView) rootView
				.findViewById(R.id.div_software);
		mpnpid = (TextView) rootView.findViewById(R.id.div_pnp);
		mSysid = (TextView) rootView.findViewById(R.id.div_system);
		LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		getGattData();
		getActivity().getActionBar().setTitle(R.string.device_info);
		setHasOptionsMenu(true);
		return rootView;
	}

	/**
	 * Prepare Broadcast receiver to broadcast read characteristics
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
		}
	}

	@Override
	public void onResume() {
		makeDefaultBooleans();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		clearUI();
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.device_info));
		getGattData();
		super.onResume();
	}

	/**
	 * clear all data fields
	 */
	private void clearUI() {
		mManufacturerName.setText("");
		mModelName.setText("");
		mSerialName.setText("");
		mHardwareRevisionName.setText("");
		mSoftwareRevisionName.setText("");
		mpnpid.setText("");
		mSysid.setText("");
	}

	/**
	 * Flag up default
	 */
	private void makeDefaultBooleans() {
		mManufacturerSet = false;
		mmModelNumberSet = false;
		mSerialNumberSet = false;
		mHardwareNumberSet = false;
		mSoftwareNumberSet = false;
		mPnpidSet = false;
		mSystemidSet = false;
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onPause();
	}

	/**
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();
			if (uuidchara
					.equalsIgnoreCase(GattAttributes.MANUFACTURER_NAME_STRING)) {
				Logger.i("Characteristic div" + uuidchara);
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
