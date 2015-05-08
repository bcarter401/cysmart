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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

//Fragment to display the heart rate service
public class HeartRateService extends Fragment {

	// Data view variables
	private TextView mDataField_HRM;
	private TextView mDataField_HREE;
	private TextView mDataField_HRRR;
	private TextView mDataField_BSL;
	private ImageView mHeartView;
	AlertDialog alert;

	// GATT service and characteristics
	public static BluetoothGattService mservice;
	public static BluetoothGattCharacteristic mNotifyCharacteristic;
	public static BluetoothGattCharacteristic mReadCharacteristic;

	// Flags
	public static boolean mHeartHRMvalue = false;
	public static boolean mHeartEEvalue = false;
	public static boolean mHeartRRvalue = false;
	public static boolean mHeartSensorSet = false;
	private boolean HANDLER_FLAG = false;

	// Graph view variables
	LinearLayout graph_layout;
	private GraphView graphView;
	private GraphViewSeries graphSeries;
	private double graph2LastXValue = 1;

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
				if (alert != null && !alert.isShowing()) {
					Utils.connectionLostalertbox(getActivity(), alert);
					// Adding data to Data Logger
					Logger.datalog(
							getResources().getString(
									R.string.data_logger_device_connected),
							getActivity().getApplicationContext());
				}

			}
			// GATT Data available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// Check body sensor location
				if (extras.containsKey(Constants.EXTRA_BSL_VALUE)) {
					String received_bsl_data = intent
							.getStringExtra(Constants.EXTRA_BSL_VALUE);
					mHeartSensorSet = true;
					displayBSLData(received_bsl_data);
					prepareBroadcastDataNotify(mNotifyCharacteristic);
				}
				// Check heart rate
				if (extras.containsKey(Constants.EXTRA_HRM_VALUE)) {
					String received_heart_rate = extras
							.getString(Constants.EXTRA_HRM_VALUE);
					mHeartHRMvalue = true;
					displayHRMData(received_heart_rate);

				}
				// Check energy expended
				if (extras.containsKey(Constants.EXTRA_HRM_EEVALUE)) {
					String received_heart_rate_ee = extras
							.getString(Constants.EXTRA_HRM_EEVALUE);
					mHeartEEvalue = true;
					displayHRMEEData(received_heart_rate_ee);

				}
				// Check rr interval
				if (extras.containsKey(Constants.EXTRA_HRM_RRVALUE)) {
					ArrayList<Integer> received_rr_interval = extras
							.getIntegerArrayList(Constants.EXTRA_HRM_RRVALUE);
					mHeartRRvalue = true;
					displayHRMRRData(received_rr_interval);

				}

			}

		}
	};

	public HeartRateService create(BluetoothGattService service) {
		mservice = service;
		HeartRateService fragment = new HeartRateService();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.hrm_measurement, container,
				false);
		mDataField_HRM = (TextView) rootView.findViewById(R.id.hrm_heartrate);
		mDataField_HREE = (TextView) rootView.findViewById(R.id.heart_rate_ee);
		mDataField_HRRR = (TextView) rootView.findViewById(R.id.heart_rate_rr);
		mDataField_BSL = (TextView) rootView.findViewById(R.id.hrm_sensor_data);
		mHeartView = (ImageView) rootView.findViewById(R.id.heart_icon);
		LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		Animation pulse = AnimationUtils.loadAnimation(getActivity(),
				R.anim.pulse);
		mHeartView.startAnimation(pulse);
		getActivity().getActionBar().setTitle(R.string.heart_rate);
		setHasOptionsMenu(true);
		graph_layout = (LinearLayout) rootView.findViewById(R.id.graph_hrm);
		graphView = new LineGraphView(getActivity(), "Heart Rate");
		graphSeries = new GraphViewSeries(
				new GraphViewData[] { new GraphViewData(0, 0) });
		// add data
		graphView.addSeries(graphSeries);
		// set view port, start=2, size=10
		graphView.setViewPort(0, 300);
		// set styles

		graphView.getGraphViewStyle().setNumHorizontalLabels(5);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);

		graphView.setScrollable(true);
		graph_layout.addView(graphView);
		return rootView;
	}

	private void displayBSLData(String hrm_body_sensor_data) {
		if (hrm_body_sensor_data != null) {
			Logger.datalog("Body Sensor Location " + hrm_body_sensor_data,
					getActivity());
			mDataField_BSL.setText(hrm_body_sensor_data);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources()
						.getString(
								R.string.data_logger_characteristic_heart_rate_sensor_value),
				getActivity().getApplicationContext());
	}

	private void displayHRMData(final String hrm__data) {
		if (hrm__data != null) {
			Logger.datalog("Heart Rate Mesurement " + hrm__data, getActivity());
			mDataField_HRM.setText(hrm__data);
			final Handler lHandler = new Handler();
			Runnable lRunnable = new Runnable() {

				@Override
				public void run() {
					if (HANDLER_FLAG) {
						graph2LastXValue++;
						int val = Integer.valueOf(hrm__data);
						graphSeries.appendData(new GraphViewData(
								graph2LastXValue, val), true, 500);

					}

				}
			};
			lHandler.postDelayed(lRunnable, 1000);

		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_heart_rate_value),
				getActivity().getApplicationContext());
	}

	private void displayHRMEEData(String hrm_ee_data) {
		if (hrm_ee_data != null) {
			Logger.datalog("HRM Energy Expended Data " + hrm_ee_data,
					getActivity());
			mDataField_HREE.setText(hrm_ee_data);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources()
						.getString(
								R.string.data_logger_characteristic_heart_rate_energy_expended),
				getActivity().getApplicationContext());
	}

	/**
	 * Method to detect whether the device is phone or tablet
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private void displayHRMRRData(ArrayList<Integer> hrm_rr_data) {
		if (hrm_rr_data != null) {
			for (int i = 0; i < hrm_rr_data.size(); i++) {
				String data = String.valueOf(hrm_rr_data.get(i));
				mDataField_HRRR.setText(data);
			}

		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources()
						.getString(
								R.string.data_logger_characteristic_heart_rate_rr_value),
				getActivity().getApplicationContext());
	}

	@Override
	public void onResume() {
		super.onResume();
		HANDLER_FLAG = true;
		getGattData();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.heart_rate));
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		HANDLER_FLAG = false;
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		stopBroadcastDataNotify(mNotifyCharacteristic);
		super.onDestroy();
	};

	/**
	 * Stopping Broadcast receiver to broadcast notify characteristics
	 * 
	 * @param gattCharacteristic
	 */
	public static void stopBroadcastDataNotify(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			if (mNotifyCharacteristic != null) {
				Logger.d("Stopped notification");
				BluetoothLeService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
			}

		}

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
		}
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

	}

	/**
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();
			if (uuidchara.equalsIgnoreCase(GattAttributes.BODY_SENSOR_LOCATION)) {
				mReadCharacteristic = gattCharacteristic;
				final Handler lHandler = new Handler();
				prepareBroadcastDataRead(mReadCharacteristic);
				Runnable lRunnable = new Runnable() {

					@Override
					public void run() {
						if (HANDLER_FLAG) {
							prepareBroadcastDataRead(mReadCharacteristic);
							Logger.w("REading sensor..:D");
							lHandler.postDelayed(this, 4000);
						}
					}
				};
				lHandler.post(lRunnable);
			}
			if (uuidchara
					.equalsIgnoreCase(GattAttributes.HEART_RATE_MEASUREMENT)) {
				mNotifyCharacteristic = gattCharacteristic;

			}

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.global, menu);
		MenuItem graph = menu.findItem(R.id.graph);
		MenuItem log = menu.findItem(R.id.log);
		graph.setVisible(true);
		log.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		switch (item.getItemId()) {
		case R.id.graph:
			if (graph_layout.getVisibility() != View.VISIBLE) {
				graph_layout.setVisibility(View.VISIBLE);
				item.setIcon(R.drawable.graph_active);
			} else {
				item.setIcon(R.drawable.graph);
				graph_layout.setVisibility(View.GONE);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
