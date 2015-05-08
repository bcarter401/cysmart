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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

/**
 * Fragment to display the heart rate service
 * 
 */
public class HealthTemperatureService extends Fragment {

	// Data view variables
	private TextView mDataField_HTM;
	private TextView mDataField_HSL;
	AlertDialog alert;

	// GATT service nad characteristics
	public static BluetoothGattService mservice;
	public static BluetoothGattCharacteristic mNotifyCharacteristic;
	public static BluetoothGattCharacteristic mReadCharacteristic;

	// Flags
	public static boolean mThermoValueSet = false;
	public static boolean mThermoSensorSet = false;
	private boolean HANDLER_FLAG = true;

	// Graph variables
	LinearLayout graph_layout_parent;
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
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Utils.connectionLostalertbox(getActivity(), alert);
				// Adding data to Data Logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_device_connected),
						getActivity().getApplicationContext());
			}
			// GATT data available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

				// Check Health temperature
				if (extras.containsKey(Constants.EXTRA_HTM_VALUE)) {
					String received_htm_data = intent
							.getStringExtra(Constants.EXTRA_HTM_VALUE);
					mThermoValueSet = true;
					displayLiveData(received_htm_data);

				}
				// Check health sensor location
				else {
					String received_hsl_data = intent
							.getStringExtra(Constants.EXTRA_HSL_VALUE);
					prepareBroadcastDataNotify(mNotifyCharacteristic);
					mThermoSensorSet = true;
					displayBSLData(received_hsl_data);
				}

			}

		}
	};

	public HealthTemperatureService create(BluetoothGattService service) {
		HealthTemperatureService fragment = new HealthTemperatureService();
		mservice = service;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.health_temp_measurement,
				container, false);

		mDataField_HTM = (TextView) rootView.findViewById(R.id.temp_measure);
		mDataField_HSL = (TextView) rootView.findViewById(R.id.hsl_sensor_data);
		LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		
			}
		});
		mDataField_HSL.setSelected(true);

		setHasOptionsMenu(true);
		LinearLayout graph_layout = (LinearLayout) rootView
				.findViewById(R.id.graph_htm);
		// Graph view
		graph_layout_parent = (LinearLayout) rootView
				.findViewById(R.id.graph_htm_parent);
		graphView = new LineGraphView(getActivity(), "");

		graphSeries = new GraphViewSeries(
				new GraphViewData[] { new GraphViewData(0, 0) });
		// add data
		graphView.addSeries(graphSeries);
		// set view port, start=2, size=10
		graphView.setViewPort(0, 300);
		// set styles
		graphView.setScalable(true);

		graphView.getGraphViewStyle().setNumHorizontalLabels(5);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);

		graphView.setScrollable(true);
		graph_layout.addView(graphView);
		return rootView;
	}

	private void displayLiveData(String htm_data) {
		if (htm_data != null) {
			mDataField_HTM.setText(htm_data);
			final float val = Float.valueOf(htm_data);
			final Handler lHandler = new Handler();
			Runnable lRunnable = new Runnable() {

				@Override
				public void run() {
					if (HANDLER_FLAG) {
						graph2LastXValue++;
						try {
							graphSeries.appendData(new GraphViewData(
									graph2LastXValue, val), true, 500);
						} catch (NumberFormatException e) {

						}

					}

				}
			};
			lHandler.postDelayed(lRunnable, 1000);

		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_health_temperature_value),
				getActivity().getApplicationContext());
	}

	private void displayBSLData(String htm_sensor_data) {
		if (htm_sensor_data != null) {
			mDataField_HSL.setText(htm_sensor_data);
		}
		// Adding data to Data Logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_health_temperature_sensor_location),
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
				getResources().getString(R.string.health_thermometer_fragment));
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
	public  void stopBroadcastDataNotify(
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
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();
			if (uuidchara.equalsIgnoreCase(GattAttributes.TEMPERATURE_TYPE)) {
				mReadCharacteristic = gattCharacteristic;
				final Handler lHandler = new Handler();
				prepareBroadcastDataRead(mReadCharacteristic);
				Runnable lRunnable = new Runnable() {

					@Override
					public void run() {
						if (HANDLER_FLAG) {
							prepareBroadcastDataRead(mReadCharacteristic);
							Logger.d("REading sensor..:D");
							lHandler.postDelayed(this, 4000);
						}

					}
				};
				lHandler.post(lRunnable);

			}
			if (uuidchara
					.equalsIgnoreCase(GattAttributes.HEALTH_TEMP_MEASUREMENT)) {
				mNotifyCharacteristic = gattCharacteristic;
			}
		}
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
		graph.setVisible(true);
		log.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here.
		switch (item.getItemId()) {
		case R.id.graph:
			if (graph_layout_parent.getVisibility() != View.VISIBLE) {
				graph_layout_parent.setVisibility(View.VISIBLE);

			} else {

				graph_layout_parent.setVisibility(View.GONE);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
