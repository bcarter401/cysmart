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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.utils.Constants;

/**
 * Fragment to show the DataLogger
 * 
 */
public class DataLoggerFragment extends Fragment {
	/**
	 * DataLogger text
	 */
	private TextView mDataText;
	/**
	 * Progress bar to be shown when data fetching is in progress
	 */
	ProgressBar mProgressbar;
	/**
	 * ScrollView for scrolling down
	 */
	ScrollView mScrollView;
	/**
	 * History option text
	 */
	TextView mDataHistory;
	/**
	 * FilePath of DataLogger
	 */
	String mFilepath;
	/**
	 * visibility flag
	 */
	boolean mVisible = false;

	public DataLoggerFragment create() {
		DataLoggerFragment fragment = new DataLoggerFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.datalogger, container, false);
		mDataText = (TextView) rootView.findViewById(R.id.txtlog);
		mDataHistory = (TextView) rootView.findViewById(R.id.txthistory);
		mProgressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
		mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView1);
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			mFilepath = bundle.getString(Constants.DATA_LOGGER_FILE_NAAME);
			mVisible = bundle.getBoolean(Constants.DATA_LOGGER_FLAG);
		}
		// Handling the history text visibility based on the received Arguments
		if (mVisible) {
			mDataHistory.setVisibility(View.GONE);
		} else {
			mDataHistory.setVisibility(View.VISIBLE);
		}
		mDataHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Adding the DataLoggerHistoryListFragment to the view
				FragmentManager fragmentManager = getFragmentManager();
				DataLoggerHistoryListFragment dataloggerhistory = new DataLoggerHistoryListFragment()
						.create();
				fragmentManager.beginTransaction()
						.add(R.id.container, dataloggerhistory) // ,Constants.GATTDBCHAR_FRAGMENT_TAG
						.addToBackStack(null).commit();
			}
		});
		// Loading data from the logger in separate thread
		new loadLogdata().execute();
		return rootView;
	}

	@Override
	public void onResume() {
		getActivity().setProgressBarIndeterminateVisibility(false);
		super.onResume();
	}

	/**
	 * AsyncTask class for loading logger data
	 */
	private class loadLogdata extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			mProgressbar.setVisibility(View.VISIBLE);
		}

		String data = "";

		protected String doInBackground(Void... params) {
			try {
				data = logdata();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			mProgressbar.setVisibility(View.INVISIBLE);
			mDataText.setText(result);

		}
	}

	/**
	 * Reading the data from the file stored in the FilePath
	 * 
	 * @return {@link String}
	 * @throws FileNotFoundException
	 */
	private String logdata() throws FileNotFoundException {
		File file = new File(mFilepath);
		if (!file.exists()) {
			// No Logs found
			String noLog = getResources()
					.getString(R.string.data_logger_no_log);
			return noLog;
		}
		/**
		 * Reading data using InputStream
		 */
		InputStream inputStream = new FileInputStream(file);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();

		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			buffreader.close();
		} catch (IOException e) {
			return null;
		}

		return text.toString();
	}
}
