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

package com.cypress.cysmart.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.datalogger.DataLoggerFragment;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Utils;

public class ProfileScanningFragment extends Fragment {

	// Adapter for Devices list
	LeDeviceListAdapter mLeDeviceListAdapter;

	// Scanning status flag
	public static boolean mScanning;

	// Swipe refresh layout
	SwipeRefreshLayout mswipeLayout;

	// ListView
	private ListView mProfileListView;

	// No device found view
	private RelativeLayout mNoDeviceFound;

	// View variable
	private View mrootView;

	// device details
	private String mDeviceName = "name";
	private String mDeviceAddress = "address";

	// progress dialog variable
	private ProgressDialog mpdia;

	// Devices
	public static ArrayList<BluetoothDevice> mLeDevices;

	// RSSI values
	Map<String, Integer> mdevRssiValues;

	// Handler flag
	private boolean HANDLER_FLAG = true;

	// Stops scanning after 5 seconds.
	private static final long SCAN_PERIOD = 10000;

	// Device key
	private final String LIST_UUID = "UUID";

	AlertDialog alert;

	/**
	 * Blue tooth GATT service data
	 */
	static ArrayList<HashMap<String, BluetoothGattService>> gattServiceMasterData = new ArrayList<HashMap<String, BluetoothGattService>>();
	static ArrayList<HashMap<String, BluetoothGattService>> gattServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();
	static ArrayList<HashMap<String, BluetoothGattService>> gattServiceFindMeData = new ArrayList<HashMap<String, BluetoothGattService>>();
	static ArrayList<HashMap<String, BluetoothGattService>> gattServiceSensorHubData = new ArrayList<HashMap<String, BluetoothGattService>>();
	public static ArrayList<HashMap<String, BluetoothGattService>> gattdbServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();

	// Application
	CySmartApplication application;

	// Blue tooth adapter for BLE device scan
	public static BluetoothAdapter mBluetoothAdapter;

	// Activity request constant
	private static final int REQUEST_ENABLE_BT = 1;

	// Flag for device found
	public static boolean nearByLeDeviceFound = false;

	private Handler mHandler;

	/**
	 * BroadcastReceiver for receiving the GATT server status
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// Connected to GATT Server
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				if (mScanning) {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanning = false;
				}

				BluetoothLeService.discoverServices();

			}
			// Disconnected from GATT Server
			else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				if (mpdia != null && mpdia.isShowing()) {
					mpdia.dismiss();
				}

			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {

					Utils.connectionLostalertbox(getActivity(), alert);

				}

			}
			// Services Discovered from GATT Server
			else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				getGattServices(BluetoothLeService.getSupportedGattServices());

			}

		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// View related variable initialization
		mrootView = inflater.inflate(R.layout.fragment_profile_scan, container,
				false);
		mHandler = new Handler();
		mdevRssiValues = new HashMap<String, Integer>();
		application = (CySmartApplication) getActivity().getApplication();
		mswipeLayout = (SwipeRefreshLayout) mrootView
				.findViewById(R.id.swipe_container);
		mswipeLayout.setColorScheme(R.color.dark_blue, R.color.medium_blue,
				R.color.light_blue, R.color.faint_blue);
		mProfileListView = (ListView) mrootView
				.findViewById(R.id.listView_profiles);
		mNoDeviceFound = (RelativeLayout) mrootView
				.findViewById(R.id.no_dev_found);
		setHasOptionsMenu(true);

		// Use this check to determine whether BLE is supported on the device.
		if (!getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(getActivity(), R.string.device_ble_not_supported,
					Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}
		// Initializes a Blue tooth adapter.
		final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			// Device does not support Blue tooth
			Toast.makeText(getActivity(),
					R.string.device_bluetooth_not_supported, Toast.LENGTH_SHORT)
					.show();
			getActivity().finish();
		} else {
			/**
			 * Ensures Blue tooth is enabled on the device. If Blue tooth is not
			 * currently enabled, fire an intent to display a dialog asking the
			 * user to grant permission to enable it.
			 */
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				// Prepare list view and initiate scanning
				mLeDeviceListAdapter = new LeDeviceListAdapter();
				mProfileListView.setAdapter(mLeDeviceListAdapter);
				mLeDeviceListAdapter.notifyDataSetChanged();
				scanLeDevice(true);

			}

		}
		/**
		 * List view item click listener.When a user select a device on the list
		 * view stop the scanning and connect to the device..
		 */

		mProfileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mLeDeviceListAdapter.getCount() > 0) {
					final BluetoothDevice device = mLeDeviceListAdapter
							.getDevice(position);
					if (device == null) {
						return;
					} else {
						if (mScanning) {
							scanLeDevice(false);
							mswipeLayout.setRefreshing(false);
						}

						connectDevice(device);
					}
				}

			}
		});
		/**
		 * Swipe listener,initiate a new scan on refresh. Stop the swipe refresh
		 * after 5 seconds
		 */
		mswipeLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

					@Override
					public void onRefresh() {
						if (mLeDeviceListAdapter != null)
							mLeDeviceListAdapter.clear();
						scanLeDevice(true);
						mScanning = true;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								mScanning = false;
								mswipeLayout.setRefreshing(false);
							}
						}, 5000);
					}
				});

		return mrootView;
	}

	/**
	 * Method to connect to the device selected. The time allotted for having a
	 * connection is 8 seconds. After 8 seconds it will disconnect if not
	 * connected and initiate scan once more
	 * 
	 * @param device
	 */

	protected void connectDevice(BluetoothDevice device) {
		mpdia = new ProgressDialog(getActivity());
		mpdia.setTitle(getResources().getString(
				R.string.alert_message_connect_title));
		mpdia.setMessage(getResources().getString(
				R.string.alert_message_connect)
				+ "\n"
				+ device.getName()
				+ "\n"
				+ device.getAddress()
				+ getResources().getString(R.string.alert_message_wait));
		mpdia.show();
		mDeviceAddress = device.getAddress();
		mDeviceName = device.getName();

		// Get the connection status of the device
		if (BluetoothLeService.getConnectionState() == 0) {
			// Disconnected,so connect
			BluetoothLeService.connect(mDeviceAddress, getActivity());
		} else {
			// Not disconnected,so disconnect and then connect
			BluetoothLeService.disconnect();
			BluetoothLeService.connect(mDeviceAddress, getActivity());
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (HANDLER_FLAG) {
					mpdia.dismiss();
					BluetoothLeService.disconnect();
					try {
						Toast.makeText(getActivity(),
								R.string.profile_control_delay_message,
								Toast.LENGTH_SHORT).show();
						if (mLeDeviceListAdapter != null)
							mLeDeviceListAdapter.clear();
						mNoDeviceFound.setVisibility(View.VISIBLE);
						scanLeDevice(true);
						mScanning = true;

					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
			}
		}, 8000);

	}

	/**
	 * Method to scan BLE Devices. The status of the scan will be detected in
	 * the BluetoothAdapter.LeScanCallback
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					if (getActivity() != null) {
						if (mLeDeviceListAdapter.getCount() == 0 && !mScanning) {
							mswipeLayout.setRefreshing(false);
							mNoDeviceFound.setVisibility(View.VISIBLE);
						}
						mswipeLayout.setRefreshing(false);
					}

				}
			}, SCAN_PERIOD);
			mLeDeviceListAdapter.notifyDataSetChanged();
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			mswipeLayout.setRefreshing(true);
			mNoDeviceFound.setVisibility(View.GONE);

		} else {
			mScanning = false;
			mswipeLayout.setRefreshing(false);
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// Register the broadcast receiver for connection status
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		// Initializes ActionBar as required
		setUpActionBar();
		/**
		 * Ensures Blue tooth is enabled on the device. If Blue tooth is not
		 * currently enabled, fire an intent to display a dialog asking the user
		 * to grant permission to enable it.
		 */
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			// Prepare list view and initiate scanning
			mLeDeviceListAdapter = new LeDeviceListAdapter();
			mProfileListView.setAdapter(mLeDeviceListAdapter);
			mLeDeviceListAdapter.notifyDataSetChanged();
			scanLeDevice(true);

		}
	}

	@Override
	public void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (mLeDeviceListAdapter != null)
			mLeDeviceListAdapter.clear();
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		mswipeLayout.setRefreshing(false);
	}

	/**
	 * 
	 * List Adapter for holding devices found through scanning.
	 * 
	 */
	private class LeDeviceListAdapter extends BaseAdapter {
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = getActivity().getLayoutInflater();
		}

		private void addDevice(BluetoothDevice device, int rssi) {
			boolean deviceFound = false;
			// Check for the device is already displayed in the list
			for (BluetoothDevice listDev : mLeDevices) {
				if (listDev.getAddress().equals(device.getAddress())) {
					deviceFound = true;
					break;
				}
			}
			// New device found
			if (!deviceFound) {
				mdevRssiValues.put(device.getAddress(), rssi);
				mLeDevices.add(device);
			}
		}

		/**
		 * Getter method to get the blue tooth device
		 * 
		 * @param position
		 * @return BluetoothDevice
		 */
		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		/**
		 * Clearing all values in the device array list
		 */
		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, viewGroup,
						false);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				viewHolder.deviceRssi = (TextView) view
						.findViewById(R.id.device_rssi);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			/**
			 * Setting the name and the RSSI of the BluetoothDevice. provided it
			 * is a valid one
			 */
			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0) {
				viewHolder.deviceName.setText(deviceName);
				viewHolder.deviceAddress.setText(device.getAddress());
				byte rssival = (byte) mdevRssiValues.get(device.getAddress())
						.intValue();
				if (rssival != 0) {
					viewHolder.deviceRssi.setText(String.valueOf(rssival));
				}
			} else {
				viewHolder.deviceName.setText(R.string.device_unknown);
				viewHolder.deviceAddress.setText(device.getAddress());
			}
			return view;
		}
	}

	/**
	 * Holder class for the list view view widgets
	 * 
	 */
	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView deviceRssi;
	}

	/**
	 * Call back for BLE Scan
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					nearByLeDeviceFound = true;
					mLeDeviceListAdapter.addDevice(device, rssi);
					mLeDeviceListAdapter.notifyDataSetChanged();
					if (mLeDevices.size() > 0) {
						mNoDeviceFound.setVisibility(View.GONE);

					}
				}
			});
		}
	};

	/**
	 * Getting the GATT Services
	 * 
	 * @param gattServices
	 */
	private void getGattServices(List<BluetoothGattService> gattServices) {
		// Optimization code for Sensor HUb
		if (isSensorHubPresent(gattServices)) {
			prepareSensorHubData(gattServices);
		} else {
			prepareData(gattServices);
		}

	}

	/**
	 * Check whether SensorHub related services are present in the discovered
	 * services
	 * 
	 * @param gattServices
	 * @return {@link Boolean}
	 */
	public boolean isSensorHubPresent(List<BluetoothGattService> gattServices) {
		boolean present = false;
		for (BluetoothGattService gattService : gattServices) {
			String uuid = gattService.getUuid().toString();
			if (uuid.equalsIgnoreCase(GattAttributes.BAROMETER_SERVICE)) {
				present = true;
			}
		}
		return present;
	}

	/**
	 * Prepare GATTServices data.
	 * 
	 * @param gattServices
	 */
	private void prepareData(List<BluetoothGattService> gattServices) {
		boolean mFindmeSet = false;
		boolean mGattSet = false;
		if (gattServices == null)
			return;
		// Clear all array list before entering values.
		gattServiceData.clear();
		gattServiceFindMeData.clear();
		gattServiceMasterData.clear();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, BluetoothGattService> currentServiceData = new HashMap<String, BluetoothGattService>();
			String uuid = gattService.getUuid().toString();

			// Optimization code for FindMe Profile
			if (uuid.equalsIgnoreCase(GattAttributes.LINK_LOSS_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.TRANSMISSION_POWER_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.IMMEDIATE_ALERT_SERVICE)) {
				currentServiceData.put(LIST_UUID, gattService);
				gattServiceMasterData.add(currentServiceData);
				if (!gattServiceFindMeData.contains(currentServiceData)) {
					gattServiceFindMeData.add(currentServiceData);
				}
				if (!mFindmeSet) {
					mFindmeSet = true;
					gattServiceData.add(currentServiceData);
				}

			} // Optimization code for GATTDB
			else if (uuid
					.equalsIgnoreCase(GattAttributes.GENERIC_ACCESS_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.GENERIC_ATTRIBUTE_SERVICE)) {
				currentServiceData.put(LIST_UUID, gattService);
				gattdbServiceData.add(currentServiceData);
				if (!mGattSet) {
					mGattSet = true;
					gattServiceData.add(currentServiceData);
				}

			} else {
				currentServiceData.put(LIST_UUID, gattService);
				gattServiceMasterData.add(currentServiceData);
				gattServiceData.add(currentServiceData);
			}

		}
		application.setGattServiceData(gattServiceData);
		application.setGattServiceMasterData(gattServiceMasterData);

		// All service discovered and optimized.Dismiss the alert dialog
		if (gattdbServiceData.size() > 0) {
			if (mpdia != null && mpdia.isShowing()) {
				mpdia.dismiss();
			}
			/**
			 * Setting the handler flag to false. adding new fragment
			 * ProfileControlFragment to the view
			 */
			HANDLER_FLAG = false;
			FragmentManager fragmentManager = getFragmentManager();
			ProfileControlFragment profileControlFragment = new ProfileControlFragment()
					.create(mDeviceName, mDeviceAddress);
			fragmentManager
					.beginTransaction()
					.add(R.id.container, profileControlFragment,
							Constants.PROFILE_CONTROL_FRAGMENT_TAG)
					.addToBackStack(null).commit();
		}

	}

	private void prepareSensorHubData(List<BluetoothGattService> gattServices) {

		boolean mGattSet = false;
		boolean mSensorHubSet = false;

		if (gattServices == null)
			return;
		// Clear all array list before entering values.
		gattServiceData.clear();
		gattServiceMasterData.clear();
		gattServiceSensorHubData.clear();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices) {
			HashMap<String, BluetoothGattService> currentServiceData = new HashMap<String, BluetoothGattService>();
			String uuid = gattService.getUuid().toString();

			// Optimization code for SensorHub Profile
			if (uuid.equalsIgnoreCase(GattAttributes.LINK_LOSS_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.TRANSMISSION_POWER_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.IMMEDIATE_ALERT_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.BAROMETER_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.ACCELEROMETER_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.ANALOG_TEMPERATURE_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.BATTERY_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.DEVICE_INFORMATION_SERVICE)) {
				currentServiceData.put(LIST_UUID, gattService);
				gattServiceMasterData.add(currentServiceData);
				if (!gattServiceSensorHubData.contains(currentServiceData)) {
					gattServiceSensorHubData.add(currentServiceData);
				}
				if (!mSensorHubSet
						&& uuid.equalsIgnoreCase(GattAttributes.BAROMETER_SERVICE)) {
					mSensorHubSet = true;
					gattServiceData.add(currentServiceData);
				}

			} // Optimization code for GATTDB
			else if (uuid
					.equalsIgnoreCase(GattAttributes.GENERIC_ACCESS_SERVICE)
					|| uuid.equalsIgnoreCase(GattAttributes.GENERIC_ATTRIBUTE_SERVICE)) {
				currentServiceData.put(LIST_UUID, gattService);
				gattdbServiceData.add(currentServiceData);
				if (!mGattSet) {
					mGattSet = true;
					gattServiceData.add(currentServiceData);
				}

			} else {
				currentServiceData.put(LIST_UUID, gattService);
				gattServiceMasterData.add(currentServiceData);
				gattServiceData.add(currentServiceData);
			}
		}
		application.setGattServiceMasterData(gattServiceMasterData);
		application.setGattServiceData(gattServiceData);

		// All service discovered and optimized.Dismiss the alert dialog
		if (gattdbServiceData.size() > 0) {
			if (mpdia != null && mpdia.isShowing()) {
				mpdia.dismiss();
			}
			/**
			 * Setting the handler flag to false. adding new fragment
			 * ProfileControlFragment to the view
			 */
			HANDLER_FLAG = false;
			FragmentManager fragmentManager = getFragmentManager();
			ProfileControlFragment profileControlFragment = new ProfileControlFragment()
					.create(mDeviceName, mDeviceAddress);
			fragmentManager
					.beginTransaction()
					.add(R.id.container, profileControlFragment,
							Constants.PROFILE_CONTROL_FRAGMENT_TAG)
					.addToBackStack(null).commit();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable BlueTooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			getActivity().finish();
			return;
		} else {
			// Check which request we're responding to
			if (requestCode == REQUEST_ENABLE_BT) {

				// Make sure the request was successful
				if (resultCode == Activity.RESULT_OK) {
					Toast.makeText(
							getActivity(),
							getResources().getString(
									R.string.device_bluetooth_on),
							Toast.LENGTH_SHORT).show();
					mLeDeviceListAdapter = new LeDeviceListAdapter();
					mProfileListView.setAdapter(mLeDeviceListAdapter);
					scanLeDevice(true);
				} else {
					getActivity().finish();
				}
			}
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.global, menu);
		MenuItem item = menu.findItem(R.id.graph);
		MenuItem log = menu.findItem(R.id.log);
		item.setVisible(false);
		log.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar item clicks here.
		switch (item.getItemId()) {
		case R.id.log:
			// DataLogger
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "CySmart" + File.separator
					+ "datalogefile_" + Utils.GetDate() + ".txt");
			String path = file.getAbsolutePath();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.DATA_LOGGER_FILE_NAAME, path);
			bundle.putBoolean(Constants.DATA_LOGGER_FLAG, false);
			/**
			 * Adding new fragment DataLoggerFragment to the view
			 */
			FragmentManager fragmentManager = getFragmentManager();
			DataLoggerFragment dataloggerfragment = new DataLoggerFragment()
					.create();
			dataloggerfragment.setArguments(bundle);
			fragmentManager.beginTransaction()
					.add(R.id.container, dataloggerfragment)
					.addToBackStack(null).commit();
			return true;
		case R.id.share:
			// Share
			HomePageActivity.containerView.invalidate();
			View v1 = getActivity().getWindow().getDecorView().getRootView();
			Utils.screenShotMethod(v1);// HomePageActivity.containerView);
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file:///sdcard/temporary_file.jpg"));
			shareIntent.setType("image/jpeg");
			startActivity(Intent.createChooser(shareIntent, getResources()
					.getText(R.string.send_to)));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Setting up the ActionBar
	 */
	public void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		actionBar.setTitle(R.string.profile_scan_fragment);
	}

}
