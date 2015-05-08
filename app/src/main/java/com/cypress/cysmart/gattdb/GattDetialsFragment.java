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
 * arising out of the mApplication or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or mApplication assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.cypress.cysmart.gattdb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypress.cysmart.CySmartApplication;
import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

public class GattDetialsFragment extends Fragment implements OnClickListener {

	// TextView variables
	TextView mServiceName;
	TextView mCharacteristiceName;
	TextView mHexValue;
	TextView mAsciivalue;
	TextView mDatevalue;
	TextView mTimevalue;
	TextView mBtnread;
	TextView mBtnwrite;
	TextView mBtnnotify;

	// Application
	CySmartApplication mApplication;

	// Indicate/Notify Flag
	boolean mIsNotify = true;
	boolean mIsIndicate = false;

	// View
	ViewGroup mContainer;

	// Alert dialog
	AlertDialog mAlertDialog;

	// HexValue entered
	EditText mHexvalue;

	// Write dialog buttons
	Button mBtnone;
	Button mBttwo;
	Button mBtthree;
	Button mBtfour;
	Button mBtfive;
	Button mBtsix;
	Button mBtseven;
	Button mBteight;
	Button mBtnine;
	Button mBtzero;
	Button mBta;
	Button mBtb;
	Button mBtc;
	Button mBtd;
	Button mBte;
	Button mBtf;
	Button mBthex;
	Button mBtone;

	// Back buttons
	ImageButton btnback;
	ImageView backbtn;

	// Converting to hex variables
	String hexValueString = "";
	String hexsubstring = "0x";

	//
	public static BluetoothGattCharacteristic mReadCharacteristic;
	public static BluetoothGattCharacteristic mNotifyCharacteristic;

	AlertDialog alert;
	private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Bundle extras = intent.getExtras();
			// Device disconnected
			if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				// Show alert
				Utils.connectionLostalertbox(getActivity(), alert);
				// Adding data to Data Logger
				Logger.datalog(
						getResources().getString(
								R.string.data_logger_device_disconnected),
						getActivity().getApplicationContext());
			}
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				// Data Received
				if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
					byte[] array = intent
							.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
					displayHexValue(array);
					displayASCIIValue(array);
					displayTimeandDate();
				}

			}

		}

	};

	public GattDetialsFragment create() {
		GattDetialsFragment fragment = new GattDetialsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gattdb_details, container,
				false);
		this.mContainer = container;
		mApplication = (CySmartApplication) getActivity().getApplication();
		mServiceName = (TextView) rootView.findViewById(R.id.txtservicename);
		mHexValue = (TextView) rootView.findViewById(R.id.txthex);
		mCharacteristiceName = (TextView) rootView
				.findViewById(R.id.txtcharatrname);
		mBtnnotify = (TextView) rootView.findViewById(R.id.txtnotify);
		mBtnread = (TextView) rootView.findViewById(R.id.txtread);
		mBtnwrite = (TextView) rootView.findViewById(R.id.txtwrite);
		mAsciivalue = (TextView) rootView.findViewById(R.id.txtascii);
		mTimevalue = (TextView) rootView.findViewById(R.id.txttime);
		mDatevalue = (TextView) rootView.findViewById(R.id.txtdate);
		backbtn = (ImageView) rootView.findViewById(R.id.imgback);
		backbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();

			}
		});
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.parent);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		/**
		 * Read button listener
		 */
		mBtnread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				prepareBroadcastDataRead(mReadCharacteristic);
			}
		});
		/**
		 * Notify button listener
		 */
		mBtnnotify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mNotifyCharacteristic = mApplication
						.getBluetoothgattcharacteristic();
				if (mIsNotify) {

					prepareBroadcastDataNotify(mNotifyCharacteristic);
					mIsNotify = false;
					mBtnnotify.setText(getString(R.string.gatt_services_stop));
				} else {
					stopBroadcastDataNotify(mNotifyCharacteristic);
					mIsNotify = true;
					if (mIsIndicate) {
						mBtnnotify
								.setText(getString(R.string.gatt_services_indicate));
					} else {
						mBtnnotify
								.setText(getString(R.string.gatt_services_notify));
					}
				}

			}
		});
		/**
		 * Write button listener
		 */
		mBtnwrite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				try {
					writeHexValue();
				} catch (Exception e) {

				}

			}
		});
		mServiceName.setSelected(true);
		mCharacteristiceName.setSelected(true);
		mAsciivalue.setSelected(true);
		mHexValue.setSelected(true);

		// Getting the characteristics from the application
		mReadCharacteristic = mApplication.getBluetoothgattcharacteristic();
		mNotifyCharacteristic = mApplication.getBluetoothgattcharacteristic();
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			mServiceName.setText(bundle
					.getString(Constants.GATTDB_SELECTED_SERVICE));
			mCharacteristiceName.setText(bundle
					.getString(Constants.GATTDB_SELECTED_CHARACTERISTICE));
		}
		UIbuttonvisibility();
		setHasOptionsMenu(true);
		return rootView;
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
		if (mNotifyCharacteristic != null) {
			stopBroadcastDataNotify(mNotifyCharacteristic);
		}
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
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

	/**
	 * Method to make the Buttons visible to the user
	 */
	private void UIbuttonvisibility() {
		// Getting the properties of each characteristics
		boolean read = false, write = false, notify = false;
		if (getGattCharacteristicsPropertices(
				mReadCharacteristic.getProperties(),
				BluetoothGattCharacteristic.PROPERTY_READ)) {
			// Read property available
			read = true;
		}
		if (getGattCharacteristicsPropertices(
				mReadCharacteristic.getProperties(),
				BluetoothGattCharacteristic.PROPERTY_WRITE)
				| getGattCharacteristicsPropertices(
						mReadCharacteristic.getProperties(),
						BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
			// Write property available
			write = true;
		}
		if (getGattCharacteristicsPropertices(
				mReadCharacteristic.getProperties(),
				BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
			// Notify property available
			notify = true;
		}
		if (getGattCharacteristicsPropertices(
				mReadCharacteristic.getProperties(),
				BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
			// Indicate property available
			notify = true;
			mIsIndicate = true;
			mBtnnotify.setText(getResources().getString(
					R.string.gatt_services_indicate));
		}
		if (read) {
			mBtnread.setVisibility(View.VISIBLE);
			if (write) {
				mBtnwrite.setVisibility(View.VISIBLE);
			}
			if (notify) {
				mBtnnotify.setVisibility(View.VISIBLE);
			}
		} else {
			if (write) {
				mBtnwrite.setVisibility(View.VISIBLE);

				if (notify) {
					mBtnnotify.setVisibility(View.VISIBLE);
				}
			} else {
				mBtnnotify.setVisibility(View.VISIBLE);
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

			// Adding data to data logger
			Logger.datalog(
					getResources().getString(
							R.string.data_logger_characteristic_read_start),
					getActivity().getApplicationContext());
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

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0
				|| (charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
			if (mNotifyCharacteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						mReadCharacteristic, false);
				mNotifyCharacteristic = null;
			}
			mNotifyCharacteristic = characteristic;
			BluetoothLeService.setCharacteristicNotification(characteristic,
					true);

			// Adding data to data logger
			Logger.datalog(
					getResources().getString(
							R.string.data_logger_characteristic_notify_start),
					getActivity().getApplicationContext());
		}

	}

	/**
	 * Stopping Broadcast receiver to broadcast notify characteristics
	 * 
	 * @param gattCharacteristic
	 */
	public void stopBroadcastDataNotify(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0
				|| (charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
			if (mNotifyCharacteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						mNotifyCharacteristic, false);
				mNotifyCharacteristic = null;
				// Adding data to data logger
				Logger.datalog(
						getResources()
								.getString(
										R.string.data_logger_characteristic_notify_stop),
						getActivity().getApplicationContext());
			}

		}

	}

	/**
	 * Method to display the ASCII Value
	 * 
	 * @param array
	 */
	protected void displayASCIIValue(byte[] array) {

		StringBuffer sb = new StringBuffer();
		for (byte byteChar : array) {
			if (byteChar >= 32 && byteChar < 127) {
				sb.append(String.format("%c", byteChar));
			} else {
				sb.append(String.format("%d ", byteChar & 0xFF)); // to convert
																	// >127 to
																	// positive
																	// value
			}
		}
		// Adding data to data logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_ascii_value)
						+ sb.toString(), getActivity().getApplicationContext());
		mAsciivalue.setText(sb.toString());
	}

	/**
	 * Method to display the hexValue
	 * 
	 * @param array
	 */
	protected void displayHexValue(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (byte byteChar : array) {
			sb.append(String.format("%02x", byteChar));
		}
		// Adding data to data logger
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_hex_value)
						+ sb.toString(), getActivity().getApplicationContext());
		mHexValue.setText("0x" + sb.toString());
	}

	/**
	 * Method to display time and date
	 */
	private void displayTimeandDate() {

		mTimevalue.setText(Utils.GetTimeFromMilliseconds());
		mDatevalue.setText(Utils.GetDateFromMilliseconds());
	}

	/**
	 * Clearing all fields
	 */
	private void clearall() {

		mTimevalue.setText("");
		mDatevalue.setText("");
		mAsciivalue.setText("");
		mHexValue.setText("");
	}

	/**
	 * Return the property enabled in the characteristic
	 * 
	 * @param characteristics
	 * @param characteristicsSearch
	 * @return
	 */
	public boolean getGattCharacteristicsPropertices(int characteristics,
			int characteristicsSearch) {

		if ((characteristics & characteristicsSearch) == characteristicsSearch) {
			return true;
		}
		return false;

	}

	/**
	 * Method to display the write keyboard
	 */
	private void writeHexValue() {
		// Setting the alert dialog as custom keyboard
		final Dialog builder;
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(
				R.id.linear);
		View layout = inflater.inflate(R.layout.hex_value_popup, viewGroup);
		builder = new Dialog(getActivity(), R.style.CustomAlertDialog);
		builder.setContentView(layout);
		builder.setCancelable(false);
		builder.show();

		mHexvalue = (EditText) layout.findViewById(R.id.edittext_text);
		mHexvalue.setText("");

		// Hiding the default keyboard
		InputMethodManager imm = (InputMethodManager) layout.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
		}
		// EditText touch listener
		mHexvalue.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.onTouchEvent(event);
				InputMethodManager imm = (InputMethodManager) v.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return true;
			}
		});

		// Custom keyboard Buttons
		Button viewOk = (Button) layout.findViewById(R.id.txtok);
		mBta = (Button) layout.findViewById(R.id.btna);
		mBtb = (Button) layout.findViewById(R.id.btnb);
		mBtc = (Button) layout.findViewById(R.id.btnc);
		mBtd = (Button) layout.findViewById(R.id.btnd);
		mBte = (Button) layout.findViewById(R.id.btne);
		mBtf = (Button) layout.findViewById(R.id.btnf);
		mBtzero = (Button) layout.findViewById(R.id.btnzero);
		btnback = (ImageButton) layout.findViewById(R.id.btnback);
		mBtone = (Button) layout.findViewById(R.id.btnone);
		mBttwo = (Button) layout.findViewById(R.id.btntwo);
		mBtthree = (Button) layout.findViewById(R.id.btnthree);
		mBtfour = (Button) layout.findViewById(R.id.btnfour);
		mBtfive = (Button) layout.findViewById(R.id.btnfive);
		mBtsix = (Button) layout.findViewById(R.id.btnsix);
		mBtseven = (Button) layout.findViewById(R.id.btnseven);
		mBteight = (Button) layout.findViewById(R.id.btneight);
		mBtnine = (Button) layout.findViewById(R.id.btnnine);
		mBthex = (Button) layout.findViewById(R.id.btnhex);

		// Custom keyboard listeners
		mBta.setOnClickListener(this);
		mBtb.setOnClickListener(this);
		mBtc.setOnClickListener(this);
		mBtd.setOnClickListener(this);
		mBte.setOnClickListener(this);
		mBtf.setOnClickListener(this);
		mBtzero.setOnClickListener(this);
		mBtone.setOnClickListener(this);
		mBttwo.setOnClickListener(this);
		mBtthree.setOnClickListener(this);
		mBtfour.setOnClickListener(this);
		mBtfive.setOnClickListener(this);
		mBtsix.setOnClickListener(this);
		mBtseven.setOnClickListener(this);
		mBteight.setOnClickListener(this);
		mBtnine.setOnClickListener(this);
		btnback.setOnClickListener(this);
		mBthex.setOnClickListener(this);
		viewOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mHexvalue.getText().toString().length() > 0) {
					displayTimeandDate();
					convertingTobyteArray();
				} else {
					clearall();
				}
				builder.cancel();
			}

		});
		Button viewCancel = (Button) layout.findViewById(R.id.txtcancel);
		viewCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.cancel();

			}
		});

	}

	@Override
	public void onClick(View v) {
		hexValueString = mHexvalue.getText().toString().trim();
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		switch (v.getId()) {

		case R.id.btna:
			hexValueUpatetemp("A");
			break;
		case R.id.btnb:
			hexValueUpatetemp("B");
			break;
		case R.id.btnc:
			hexValueUpatetemp("C");
			break;
		case R.id.btnd:
			hexValueUpatetemp("D");
			break;
		case R.id.btne:
			hexValueUpatetemp("E");
			break;
		case R.id.btnf:
			hexValueUpatetemp("F");
			break;
		case R.id.btnzero:
			hexValueUpatetemp("0");
			break;
		case R.id.btnone:
			hexValueUpatetemp("1");
			break;
		case R.id.btntwo:
			hexValueUpatetemp("2");
			break;
		case R.id.btnthree:
			hexValueUpatetemp("3");
			break;
		case R.id.btnfour:
			hexValueUpatetemp("4");
			break;
		case R.id.btnfive:
			hexValueUpatetemp("5");
			break;
		case R.id.btnsix:
			hexValueUpatetemp("6");
			break;
		case R.id.btnseven:
			hexValueUpatetemp("7");
			break;
		case R.id.btneight:
			hexValueUpatetemp("8");
			break;
		case R.id.btnnine:
			hexValueUpatetemp("9");
			break;
		case R.id.btnback:
			backbuttonpressed();
			break;
		case R.id.btnhex:
			hexUpdate();
			break;

		}
	}

	/**
	 * Method to convert hex to byteArray
	 */
	private void convertingTobyteArray() {
		hexValueString = mHexvalue.getText().toString().trim();
		String[] splited = hexValueString.split("\\s+");
		byte[] valueByte = new byte[splited.length];
		for (int i = 0; i < splited.length; i++) {
			if (splited[i].length() > 2) {
				String trimmedByte = splited[i].split("x")[1];
				valueByte[i] = (byte) convertstringtobyte(trimmedByte);
			}

		}
		// Displaying the hex and ASCII values
		displayHexValue(valueByte);
		displayASCIIValue(valueByte);

		// Writing the hexValue to the characteristics
		try {
			HomePageActivity.mBluetoothLeService.writeCharacteristicGattDb(
					mReadCharacteristic, valueByte);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Convert the string to byte
	 * 
	 * @param string
	 * @return
	 */
	private int convertstringtobyte(String string) {
		return Integer.parseInt(string, 16);
	}

	/**
	 * HexValue appending with hexSubstring
	 */
	private void hexUpdate() {
		hexsubstring = "0x";
		hexValueString = hexValueString + " " + hexsubstring;
		hexsubstring = "";
		mHexvalue.setText(hexValueString.trim());
		mHexvalue.setSelection(hexValueString.trim().length());
	}

	/**
	 * Custom keyboard back pressed action
	 */
	private void backbuttonpressed() {

		if (hexValueString.length() != 0) {

			String[] splited = hexValueString.split("\\s+");

			int last = splited.length;
			if (last != 0) {
				String substring = splited[last - 1];
				if (substring.length() == 4) {
					substring = substring.substring(0, substring.length() - 1);
					splited[last - 1] = substring;
					hexValueString = "";
					for (int i = 0; i < splited.length; i++) {
						hexValueString = hexValueString + " " + splited[i];
					}
				} else if (substring.length() == 3) {
					substring = substring.substring(0, substring.length() - 1);
					splited[last - 1] = substring;
					hexValueString = "";
					for (int i = 0; i < splited.length; i++) {
						hexValueString = hexValueString + " " + splited[i];
					}
				} else if (substring.length() == 2) {
					hexValueString = "";
					for (int i = 0; i < splited.length - 1; i++) {
						hexValueString = hexValueString + " " + splited[i];
					}
				}
				mHexvalue.setText(hexValueString.trim());
				mHexvalue.setSelection(hexValueString.trim().length());
			}
		}
	}

	/**
	 * Update the editText field with hexValues
	 * 
	 * @param string
	 */
	private void hexValueUpatetemp(String string) {
		if (hexValueString.length() != 0) {

			String[] splited = hexValueString.split("\\s+");

			int arrayCount = splited.length;
			if (arrayCount != 0) {
				String lastValue = splited[arrayCount - 1];
				int last = lastValue.length();
				if (last == 4) {
					hexValueString = hexValueString + " 0x" + string;
				} else if (last == 3) {
					hexValueString = hexValueString + string;
				} else if (last == 2) {
					hexValueString = hexValueString + string;
				}

				mHexvalue.setText(hexValueString.trim());
				mHexvalue.setSelection(hexValueString.trim().length());
			}
		} else {
			hexValueString = "0x" + string;
			mHexvalue.setText(hexValueString.trim());
			mHexvalue.setSelection(hexValueString.trim().length());
		}
	}

}
