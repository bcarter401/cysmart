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

import java.util.ArrayList;
import java.util.HashMap;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.R;
import com.cypress.cysmart.adapters.CarouselPagerAdapter;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

public class ProfileControlFragment extends Fragment {

	// Argument Constants
	public static final String ARG_DEVICE_NAME = "devicename";
	public static final String ARG_DEVICE_ADDRESS = "deviceaddress";

	// Device name and address variables
	public static String mDeviceNameProfile;
	public static String mDeviceAddressProfile;

	// BluetoothGattCharacteristic list variable
	public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

	// GattService and Characteristics Mapping
	ArrayList<HashMap<String, BluetoothGattService>> gattServiceData;

	// Base Layout
	RelativeLayout relativeLayout;

	// ViewPager for CarouselView
	public ViewPager pager;

	// Adapter for loading data to CarouselView
	public CarouselPagerAdapter adapter;

	// CarouselView related variables
	public static int PAGES = 0;
	public final static int LOOPS = 100;
	public static int FIRST_PAGE = PAGES * LOOPS / 2;
	public static float BIG_SCALE = 1.0f;
	public static float SMALL_SCALE = 0.7f;
	public static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
	int width = 0;

	// Application variable
	CySmartApplication application;
	AlertDialog alert;

	/**
	 * BroadcastReceiver for receiving the Device disconnected status
	 */
	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Utils.connectionLostalertbox(getActivity(), alert);
			}
		}
	};

	/**
	 * Method for passing data between fragments when created.
	 * 
	 * @param device_name
	 * @param device_address
	 * @return ProfileControlFragment
	 */
	public ProfileControlFragment create(String device_name,
			String device_address) {
		ProfileControlFragment fragment = new ProfileControlFragment();
		Bundle args = new Bundle();
		args.putString(ARG_DEVICE_NAME, device_name);
		args.putString(ARG_DEVICE_ADDRESS, device_address);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profile_control, container,
				false);
		relativeLayout = (RelativeLayout) rootView
				.findViewById(R.id.gatt_service_carousel);
		pager = (ViewPager) rootView.findViewById(R.id.myviewpager);
		application = (CySmartApplication) getActivity().getApplication();
		PAGES = 0;
		setCarouselView();
		/**
		 * Getting the orientation of the device. Set margin for pages as a
		 * negative number, so a part of next and previous pages will be showed
		 */
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			pager.setPageMargin(-width / 3);
		} else if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			pager.setPageMargin(-width / 2);
		}
		return rootView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Getting the width on orientation changed
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		/**
		 * Getting the orientation of the device. Set margin for pages as a
		 * negative number, so a part of next and previous pages will be showed
		 */
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			pager.setPageMargin(-width / 2);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			pager.setPageMargin(-width / 3);
		}
		pager.refreshDrawableState();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Getting the device name and address passed
		mDeviceNameProfile = getArguments().getString(ARG_DEVICE_NAME);
		mDeviceAddressProfile = getArguments().getString(ARG_DEVICE_ADDRESS);
		application = (CySmartApplication) getActivity().getApplication();

		// Entering the connected status to the DataLogger
		Logger.datalog(
				"connected : " + mDeviceNameProfile + mDeviceNameProfile,
				getActivity().getApplicationContext());

		// Getting the width of the device
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;

	}

	@Override
	public void onResume() {
		super.onResume();
		// Registering the broadcast receiver
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		// Initialize ActionBar as per requirement
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.profile_control_fragment));

	}

	@Override
	public void onPause() {
		// UnRegistering the broadcast receiver
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Setting the CarouselView with data
	 */
	private void setCarouselView() {

		// Getting the number of services discovered
		PAGES = ProfileScanningFragment.gattServiceData.size();
		FIRST_PAGE = PAGES * LOOPS / 2;

		// Setting the adapter
		adapter = new CarouselPagerAdapter(getActivity(),
				ProfileControlFragment.this, getActivity()
						.getSupportFragmentManager(),
				ProfileScanningFragment.gattServiceData);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(adapter);

		// Set current item to the middle page so we can fling to both
		// directions left and right
		pager.setCurrentItem(FIRST_PAGE);

		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		pager.setOffscreenPageLimit(3);

	}

}
