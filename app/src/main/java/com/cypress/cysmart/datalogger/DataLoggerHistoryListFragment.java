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

package com.cypress.cysmart.datalogger;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.adapters.DataLoggerListAdapter;
import com.cypress.cysmart.utils.Constants;

/**
 * Fragment to show the DataLogger history
 */
public class DataLoggerHistoryListFragment extends Fragment {

	/**
	 * ListView for loading the data logger files
	 */
	private ListView mListFileNames;
	/**
	 * Adapter for ListView
	 */
	DataLoggerListAdapter mAdapter;
	/**
	 * File names
	 */
	ArrayList<String> mNames;
	/**
	 * Directory of the file
	 */
	private String mDirectory;
	/**
	 * File
	 */
	private File mFile;

	public DataLoggerHistoryListFragment create() {
		DataLoggerHistoryListFragment fragment = new DataLoggerHistoryListFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.datalogger_list, container,
				false);
		mListFileNames = (ListView) rootView
				.findViewById(R.id.ListView_gatt_services);
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		mNames = new ArrayList<String>();

		// Getting the directory CySmart
		mDirectory = Environment.getExternalStorageDirectory() + File.separator
				+ getResources().getString(R.string.data_logger_directory);
		mFile = new File(mDirectory);

		// Listing all files in the directory
		final File list[] = mFile.listFiles();
		for (int i = 0; i < list.length; i++) {
			mNames.add(list[i].getName());
		}

		// Adding data to adapter
		DataLoggerListAdapter adapter = new DataLoggerListAdapter(
				getActivity(), mNames);
		mListFileNames.setAdapter(adapter);
		mListFileNames.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				/**
				 * Getting the absolute path. Adding the DataLogger fragment
				 * with the data of the file user selected
				 */
				String path = list[pos].getAbsolutePath();
				Bundle bundle = new Bundle();
				bundle.putString(Constants.DATA_LOGGER_FILE_NAAME, path);
				bundle.putBoolean(Constants.DATA_LOGGER_FLAG, true);
				FragmentManager fragmentManager = getFragmentManager();
				DataLoggerFragment dataloggerfragment = new DataLoggerFragment()
						.create();
				dataloggerfragment.setArguments(bundle);
				fragmentManager.beginTransaction()
						.add(R.id.container, dataloggerfragment)
						.addToBackStack(null).commit();
			}
		});
		return rootView;
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
