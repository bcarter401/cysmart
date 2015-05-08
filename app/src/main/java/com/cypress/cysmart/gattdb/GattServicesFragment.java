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

package com.cypress.cysmart.gattdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.R;
import com.cypress.cysmart.adapters.GattseriveceListAdapter;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;

/**
 * Fragment to show the GATT services details in GATT DB
 * 
 */
public class GattServicesFragment extends Fragment {

	// BluetoothGattService
	public static BluetoothGattService mService;

	// HashMap to store service
	public static ArrayList<HashMap<String, BluetoothGattService>> mGattServiceData;
	public static ArrayList<HashMap<String, BluetoothGattService>> mModifiedServiceData;

	// GattCharacteristics list
	public static List<BluetoothGattCharacteristic> mGattCharacteristics;

	// Application
	CySmartApplication mApplication;

	// ListView
	private ListView mGattListView;

	//
	ImageView mBackButton;

	public GattServicesFragment create() {
		GattServicesFragment fragment = new GattServicesFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_gatt_list,
				container, false);
		mApplication = (CySmartApplication) getActivity().getApplication();
		mGattListView = (ListView) rootView
				.findViewById(R.id.ListView_gatt_services);
		mBackButton = (ImageView) rootView.findViewById(R.id.imgback);
		mBackButton.setVisibility(View.GONE);
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		getActivity().getActionBar().setTitle(R.string.gatt_db);

		// Getting the service data from the application
		mGattServiceData = mApplication.getGattServiceMasterData();

		// Preparing list data
		mModifiedServiceData = new ArrayList<HashMap<String, BluetoothGattService>>();
		for (int i = 0; i < mGattServiceData.size(); i++) {
			if (!(mGattServiceData.get(i).get("UUID").getUuid().toString()
					.equalsIgnoreCase(GattAttributes.GENERIC_ATTRIBUTE_SERVICE) || mGattServiceData
					.get(i).get("UUID").getUuid().toString()
					.equalsIgnoreCase(GattAttributes.GENERIC_ACCESS_SERVICE))) {
				mModifiedServiceData.add(mGattServiceData.get(i));

			}
		}
		// Setting adapter
		GattseriveceListAdapter adapter = new GattseriveceListAdapter(
				getActivity(), mModifiedServiceData);
		mGattListView.setAdapter(adapter);

		// List listener
		mGattListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				mService = mModifiedServiceData.get(pos).get("UUID");
				mGattCharacteristics = mService.getCharacteristics();
				String selected_service_name = GattAttributes.lookup(
						mService.getUuid().toString(),
						getResources().getString(
								R.string.profile_control_unknown_service));

				// Adding data to data logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_selected_service_uuid)
								+ mService.getUuid().toString()
								+ " : "
								+ selected_service_name, getActivity()
								.getApplicationContext());

				mApplication.setGattCharacteristics(mGattCharacteristics);

				// PAssing service details to GattCharacteristicsFragment and
				// adding that fragment to the current view
				Bundle bundle = new Bundle();
				bundle.putString(Constants.GATTDB_SELECTED_SERVICE,
						selected_service_name);
				FragmentManager fragmentManager = getFragmentManager();
				GattCharacteristicsFragment gattcharacteristicsfragment = new GattCharacteristicsFragment()
						.create();
				gattcharacteristicsfragment.setArguments(bundle);
				fragmentManager.beginTransaction()
						.add(R.id.container, gattcharacteristicsfragment)
						.addToBackStack(null).commit();
			}
		});
		setHasOptionsMenu(true);
		return rootView;
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

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
