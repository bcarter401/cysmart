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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Fragment to display the RGB service
 * 
 */
public class RGBFragment extends Fragment {

	// GATT service and characteristics
	public static BluetoothGattService mCurrentservice;
	public static BluetoothGattCharacteristic mReadCharacteristic;

	// Data view variables
	ImageView mRGBcanavs;
	ImageView mcolorpicker;
	ViewGroup mViewContainer;
	TextView mTextred;
	TextView mTextgreen;
	TextView mTextblue;
	TextView mTextalpha;
	ImageView mColorindicator;
	SeekBar mIntencityBar;
	RelativeLayout mParent;
	private Bitmap mBmp;
	AlertDialog alert;

	public RGBFragment create(BluetoothGattService currentservice) {
		mCurrentservice = currentservice;
		RGBFragment fragment = new RGBFragment();
		return fragment;
	}

	// Data variables
	float mWidth, mHeight;
	private int r = 0, g = 0, b = 0, intensity = 0;
	String hexRed, hexGreen, hexBlue;
	View rootView;

	// Flag
	boolean isReaded = false;

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
								R.string.data_logger_device_connected),
						getActivity().getApplicationContext());
			}
			// GATT Data available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (extras.containsKey(Constants.EXTRA_RGB_VALUE)) {
					byte[] array = intent
							.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
					StringBuffer sb = new StringBuffer();
					for (byte byteChar : array) {
						sb.append(String.format("%02x", byteChar));
					}
					isReaded = true;
					hexRed = String.format("0x%02x", array[0]);
					hexGreen = String.format("0x%02x", array[1]);
					hexBlue = String.format("0x%02x", array[2]);
					String hexintencity = String.format("0x%02x", array[3]);
					ColorDrawable drawable = (ColorDrawable) mColorindicator
							.getBackground();
					int color = drawable.getColor();
					r = Color.red(color);
					g = Color.green(color);
					b = Color.blue(color);

					String hexColor = String.format("#%02x%02x%02x%02x",
							array[3], array[0], array[1], array[2]);
					mColorindicator.setBackgroundColor(Color
							.parseColor(hexColor));
					mTextred.setText(hexRed);
					mTextblue.setText(hexBlue);
					mTextgreen.setText(hexGreen);
					mTextalpha.setText(hexintencity);

				}

			}

		}

	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int currentOrientation = getResources().getConfiguration().orientation;
		if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			rootView = inflater.inflate(R.layout.rgb_view_landscape, container,
					false);
		} else {
			rootView = inflater.inflate(R.layout.rgb_view_portrait, container,
					false);
		}
		getActivity().getActionBar().setTitle(R.string.rgb_led);
		setUpControls();
		return rootView;
	}

	/**
	 * Method to set up the GAMOT view
	 */
	public void setUpControls() {
		mRGBcanavs = (ImageView) rootView.findViewById(R.id.imgrgbcanvas);
		mcolorpicker = (ImageView) rootView.findViewById(R.id.imgcolorpicker);
		mTextalpha = (TextView) rootView.findViewById(R.id.txtintencity);
		mTextred = (TextView) rootView.findViewById(R.id.txtred);
		mTextgreen = (TextView) rootView.findViewById(R.id.txtgreen);
		mTextblue = (TextView) rootView.findViewById(R.id.txtblue);
		mColorindicator = (ImageView) rootView
				.findViewById(R.id.txtcolorindicator);
		mViewContainer = (ViewGroup) rootView.findViewById(R.id.viewgroup);
		mIntencityBar = (SeekBar) rootView.findViewById(R.id.intencitychanger);
		mParent = (RelativeLayout) rootView.findViewById(R.id.parent);
		mParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		BitmapDrawable bmpdwbl = (BitmapDrawable) mRGBcanavs.getDrawable();
		mBmp = bmpdwbl.getBitmap();
		mRGBcanavs.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float x = event.getX();
					float y = event.getY();
					if (x >= 0 && y >= 0) {

						int x1 = (int) x;
						int y1 = (int) y;
						if (x < mBmp.getWidth() && y < mBmp.getHeight()) {
							int p = mBmp.getPixel(x1, y1);
							if (p != 0) {
								if (x > mRGBcanavs.getMeasuredWidth())
									x = mRGBcanavs.getMeasuredWidth();
								if (y > mRGBcanavs.getMeasuredHeight())
									y = mRGBcanavs.getMeasuredHeight();
								setx(x);
								sety(y);
								setwidth(1.f / mRGBcanavs.getMeasuredWidth()
										* x);
								setheight(1.f - (1.f / mRGBcanavs
										.getMeasuredHeight() * y));
								r = Color.red(p);
								g = Color.green(p);
								b = Color.blue(p);
								UIupdation();
								isReaded = false;
								moveTarget();
								return true;
							}
						}
					}
				}
				return false;
			}
		});
		intensity = mIntencityBar.getProgress();
		mTextalpha.setText(String.format("0x%02x", intensity));
		// Seek bar progress change listener
		mIntencityBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				intensity = progress;
				UIupdation();
				isReaded = false;

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				isReaded = false;
				BluetoothLeService.writeCharacteristicRGB(mReadCharacteristic,
						r, g, b, intensity);

			}
		});
	}

	@Override
	public void onResume() {
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		getGattData();
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.rgb_led));
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		super.onDestroy();
	};

	private void UIupdation() {
		String hexColor = String
				.format("#%02x%02x%02x%02x", intensity, r, g, b);
		mColorindicator.setBackgroundColor(Color.parseColor(hexColor));
		mTextalpha.setText(String.format("0x%02x", intensity));
		hexRed = String.format("0x%02x", r);
		hexGreen = String.format("0x%02x", g);
		hexBlue = String.format("0x%02x", b);
		mTextred.setText(hexRed);
		mTextblue.setText(hexBlue);
		mTextgreen.setText(hexGreen);
		mTextalpha.setText(String.format("0x%02x", intensity));
		Logger.datalog(
				getResources().getString(
						R.string.data_logger_characteristic_rgb_write)
						+ hexColor, getActivity().getApplicationContext());
		try {
			BluetoothLeService.writeCharacteristicRGB(mReadCharacteristic, r,
					g, b, intensity);
		} catch (Exception e) {

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
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		List<BluetoothGattCharacteristic> gattCharacteristics = mCurrentservice
				.getCharacteristics();
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			String uuidchara = gattCharacteristic.getUuid().toString();
			if (uuidchara.equalsIgnoreCase(GattAttributes.RGB_LED)) {
				mReadCharacteristic = gattCharacteristic;
				prepareBroadcastDataRead(gattCharacteristic);
				break;
			}
		}
	}

	/**
	 * Moving the color picker object
	 */

	protected void moveTarget() {
		float x = getwidth() * mRGBcanavs.getMeasuredWidth();
		float y = (1.f - getheigth()) * mRGBcanavs.getMeasuredHeight();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mcolorpicker
				.getLayoutParams();
		layoutParams.leftMargin = (int) (mRGBcanavs.getLeft() + x
				- Math.floor(mcolorpicker.getMeasuredWidth() / 2) - mViewContainer
				.getPaddingLeft());
		layoutParams.topMargin = (int) (mRGBcanavs.getTop() + y
				- Math.floor(mcolorpicker.getMeasuredHeight() / 2) - mViewContainer
				.getPaddingTop());
		mcolorpicker.setLayoutParams(layoutParams);
	}

	private float getwidth() {
		return mWidth;
	}

	private float getheigth() {
		return mHeight;
	}

	private void setwidth(float sat) {
		mWidth = sat;
	}

	private void setheight(float val) {
		mHeight = val;
	}

	private void setx(float x) {
	}

	private void sety(float y) {
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rootView = inflater.inflate(R.layout.rgb_view_landscape, null);
			ViewGroup rootViewG = (ViewGroup) getView();
			// Remove all the existing views from the root view.
			rootViewG.removeAllViews();
			rootViewG.addView(rootView);
			setUpControls();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rootView = inflater.inflate(R.layout.rgb_view_portrait, null);
			ViewGroup rootViewG = (ViewGroup) getView();
			// Remove all the existing views from the root view.
			rootViewG.removeAllViews();
			rootViewG.addView(rootView);
			setUpControls();

		}

	}
}
