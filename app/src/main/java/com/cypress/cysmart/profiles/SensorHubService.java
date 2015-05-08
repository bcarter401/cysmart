package com.cypress.cysmart.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.CustomSlideAnimation;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.UUIDDatabase;
import com.cypress.cysmart.utils.Utils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

/**
 * Fragment to display the sensor hub service
 * 
 */
public class SensorHubService extends Fragment {

	// GATT service and characteristics
	public static BluetoothGattService mCurrentservice;
	public static BluetoothGattService mAccservice;
	public static BluetoothGattService mStempservice;
	public static BluetoothGattService mSpressureservice;
	public static ArrayList<HashMap<String, BluetoothGattService>> mExtraservice;
	public static BluetoothGattCharacteristic mNotifyACCXCharacteristic;
	public static BluetoothGattCharacteristic mNotifyACCYCharacteristic;
	public static BluetoothGattCharacteristic mNotifyACCZCharacteristic;
	public static BluetoothGattCharacteristic mNotifyBATCharacteristic;
	public static BluetoothGattCharacteristic mNotifySTEMPCharacteristic;
	public static BluetoothGattCharacteristic mIndicateSPRESSURECharacteristic;
	public static BluetoothGattCharacteristic mWriteAlertCharacteristic;
	public static BluetoothGattCharacteristic mReadACCXCharacteristic;
	public static BluetoothGattCharacteristic mReadACCYCharacteristic;
	public static BluetoothGattCharacteristic mReadACCZCharacteristic;
	public static BluetoothGattCharacteristic mReadBATCharacteristic;
	public static BluetoothGattCharacteristic mReadSTEMPCharacteristic;
	public static BluetoothGattCharacteristic mReadSPRESSURECharacteristic;
	public static BluetoothGattCharacteristic mReadACCSensorScanCharacteristic;
	String ACCSensorScanCharacteristic = "";
	public static BluetoothGattCharacteristic mReadACCSensorTypeCharacteristic;
	String ACCSensorTypeCharacteristic = "";
	public static BluetoothGattCharacteristic mReadACCFilterConfigurationCharacteristic;
	public static BluetoothGattCharacteristic mReadSTEMPSensorScanCharacteristic;
	String STEMPSensorScanCharacteristic = "";
	public static BluetoothGattCharacteristic mReadSTEMPSensorTypeCharacteristic;
	String STEMPSensorTypeCharacteristic = "";
	public static BluetoothGattCharacteristic mReadSPRESSURESensorScanCharacteristic;
	String SPRESSURESensorScanCharacteristic = "";
	public static BluetoothGattCharacteristic mReadSPRESSURESensorTypeCharacteristic;
	String SPRESSURESensorTypeCharacteristic = "";
	public static BluetoothGattCharacteristic mReadSPRESSUREFilterConfigurationCharacteristic;
	public static BluetoothGattCharacteristic mReadSPRESSUREThresholdCharacteristic;
	String SPRESSUREThresholdCharacteristic = "";

	// Immediate alert constants
	public static final int IMM_NO_ALERT = 0x00;
	public static final int IMM_MID_ALERT = 0x01;
	public static final int IMM_HIGH_ALERT = 0x02;

	int height = 200;
	AlertDialog alert;
	TextView accX;
	TextView accY;
	TextView accZ;
	TextView BAT;
	TextView STEMP;
	TextView Spressure;
	EditText acc_scan_interval;
	EditText stemp_scan_interval;
	TextView acc_sensortype;
	TextView stemp_sensortype;
	EditText spressure_scan_interval;
	TextView spressure_sensortype;
	EditText spressure_threshold_value;
	boolean accNotifySet = false;

	private GraphView ACCgraphView;
	private GraphViewSeries ACCXgraphSeries;
	private GraphViewSeries ACCYgraphSeries;
	private GraphViewSeries ACCZgraphSeries;
	private double ACCXgraph2LastXValue = 1;
	private double ACCYgraph2LastXValue = 1;
	private double ACCZgraph2LastXValue = 1;

	private GraphView STEMPgraphView;
	private GraphViewSeries STEMPgraphSeries;
	private double STEMPgraph2LastXValue = 1;

	private GraphView SPressuregraphView;
	private GraphViewSeries SPressuregraphSeries;
	private double SPressuregraph2LastXValue = 1;

	private boolean HANDLER_FLAG = true;
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
									R.string.data_logger_device_disconnected),
							getActivity().getApplicationContext());
				}

			}
			// GATT Data Available
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (extras.containsKey(Constants.EXTRA_ACCX_VALUE)) {
					int received_acc_x = extras
							.getInt(Constants.EXTRA_ACCX_VALUE);
					displayXData("" + received_acc_x);
					if (mReadACCYCharacteristic != null) {
						prepareBroadcastDataRead(mReadACCYCharacteristic);
					}

				}
				if (extras.containsKey(Constants.EXTRA_ACCY_VALUE)) {
					int received_acc_y = extras
							.getInt(Constants.EXTRA_ACCY_VALUE);
					displayYData("" + received_acc_y);
					if (mReadACCZCharacteristic != null) {
						prepareBroadcastDataRead(mReadACCZCharacteristic);
					}

				}
				if (extras.containsKey(Constants.EXTRA_ACCZ_VALUE)) {
					int received_acc_z = extras
							.getInt(Constants.EXTRA_ACCZ_VALUE);
					displayZData("" + received_acc_z);
					prepareBroadcastDataRead(mReadBATCharacteristic);

				}
				if (extras.containsKey(Constants.EXTRA_BTL_VALUE)) {
					String received_bat = extras
							.getString(Constants.EXTRA_BTL_VALUE);
					displayBATData(received_bat);
					prepareBroadcastDataRead(mReadSTEMPCharacteristic);
				}
				if (extras.containsKey(Constants.EXTRA_STEMP_VALUE)) {
					float received_stemp = extras
							.getFloat(Constants.EXTRA_STEMP_VALUE);
					displaySTEMPData("" + received_stemp);
					prepareBroadcastDataRead(mReadSPRESSURECharacteristic);
				}
				if (extras.containsKey(Constants.EXTRA_SPRESSURE_VALUE)) {
					int received_spressure = extras
							.getInt(Constants.EXTRA_SPRESSURE_VALUE);

					displaySPressureData("" + received_spressure);
					if (mReadACCSensorScanCharacteristic != null) {
						prepareBroadcastDataRead(mReadACCSensorScanCharacteristic);
					}

				}
				if (extras.containsKey(Constants.EXTRA_ACC_SENSOR_SCAN_VALUE)) {
					int received_acc_scan_interval = extras
							.getInt(Constants.EXTRA_ACC_SENSOR_SCAN_VALUE);
					ACCSensorScanCharacteristic = ""
							+ received_acc_scan_interval;

					if (mReadACCSensorTypeCharacteristic != null) {
						prepareBroadcastDataRead(mReadACCSensorTypeCharacteristic);
					}

				}
				if (extras.containsKey(Constants.EXTRA_ACC_SENSOR_TYPE_VALUE)) {
					int received_acc_type = extras
							.getInt(Constants.EXTRA_ACC_SENSOR_TYPE_VALUE);
					ACCSensorTypeCharacteristic = "" + received_acc_type;

					if (mReadSTEMPSensorScanCharacteristic != null) {
						prepareBroadcastDataRead(mReadSTEMPSensorScanCharacteristic);
					}
				}
				if (extras.containsKey(Constants.EXTRA_STEMP_SENSOR_SCAN_VALUE)) {
					int received_stemp_scan_interval = extras
							.getInt(Constants.EXTRA_STEMP_SENSOR_SCAN_VALUE);
					STEMPSensorScanCharacteristic = ""
							+ received_stemp_scan_interval;
					Logger.w("sensor scan notified");
					if (mReadSTEMPSensorTypeCharacteristic != null) {
						prepareBroadcastDataRead(mReadSTEMPSensorTypeCharacteristic);
					}

				}
				if (extras.containsKey(Constants.EXTRA_STEMP_SENSOR_TYPE_VALUE)) {
					int received_stemp_type = extras
							.getInt(Constants.EXTRA_STEMP_SENSOR_TYPE_VALUE);
					STEMPSensorTypeCharacteristic = "" + received_stemp_type;
					if (mReadSPRESSURESensorScanCharacteristic != null) {
						prepareBroadcastDataRead(mReadSPRESSURESensorScanCharacteristic);
					}

				}
				if (extras
						.containsKey(Constants.EXTRA_SPRESSURE_SENSOR_SCAN_VALUE)) {
					int received_pressure_scan_interval = extras
							.getInt(Constants.EXTRA_SPRESSURE_SENSOR_SCAN_VALUE);
					SPRESSURESensorScanCharacteristic = ""
							+ received_pressure_scan_interval;
					if (mReadSPRESSURESensorTypeCharacteristic != null) {
						prepareBroadcastDataRead(mReadSPRESSURESensorTypeCharacteristic);
					}

				}
				if (extras
						.containsKey(Constants.EXTRA_SPRESSURE_SENSOR_TYPE_VALUE)) {
					int received_pressure_sensor = extras
							.getInt(Constants.EXTRA_SPRESSURE_SENSOR_TYPE_VALUE);
					SPRESSURESensorTypeCharacteristic = ""
							+ received_pressure_sensor;
					if (mReadSPRESSUREThresholdCharacteristic != null) {
						prepareBroadcastDataRead(mReadSPRESSUREThresholdCharacteristic);
					}

				}
				if (extras
						.containsKey(Constants.EXTRA_SPRESSURE_THRESHOLD_VALUE)) {
					int received_threshold_value = extras
							.getInt(Constants.EXTRA_SPRESSURE_THRESHOLD_VALUE);
					SPRESSUREThresholdCharacteristic = ""
							+ received_threshold_value;
					Logger.w("received_threshold_value "
							+ received_threshold_value);
					if (!accNotifySet) {
						accNotifySet = true;
						prepareBroadcastDataNotify(mNotifyACCXCharacteristic);
						prepareBroadcastDataNotify(mNotifyACCYCharacteristic);
						prepareBroadcastDataNotify(mNotifyACCZCharacteristic);
						prepareBroadcastDataNotify(mNotifyBATCharacteristic);
						prepareBroadcastDataNotify(mNotifySTEMPCharacteristic);
						prepareBroadcastDataIndicate(mIndicateSPRESSURECharacteristic);
					}
				}
			}

		}
	};

	public SensorHubService create(
			BluetoothGattService service,
			ArrayList<HashMap<String, BluetoothGattService>> gattExtraServiceData) {
		SensorHubService fragment = new SensorHubService();
		mCurrentservice = service;
		mExtraservice = gattExtraServiceData;
		return fragment;
	}

	/**
	 * Display the atmospheric pressure threshold data
	 * 
	 * @param string
	 */
	protected void displaySPressureThresholdData(String string) {
		if (spressure_threshold_value != null) {
			spressure_threshold_value.setText(string);
		}

	}

	/**
	 * Display atmospheric pressure data
	 * 
	 * @param pressure
	 */
	protected void displaySPressureData(String pressure) {
		Spressure.setText(pressure);
		final int value = Integer.valueOf(pressure);
		final Handler lHandler = new Handler();
		Runnable lRunnable = new Runnable() {

			@Override
			public void run() {
				if (HANDLER_FLAG) {
					SPressuregraph2LastXValue++;
					try {
						SPressuregraphSeries.appendData(new GraphViewData(
								SPressuregraph2LastXValue, value), true, 1500);
					} catch (NumberFormatException e) {

					}

				}

			}
		};
		lHandler.postDelayed(lRunnable, 1000);

	}

	/**
	 * Display temperature data
	 * 
	 * @param received_stemp
	 */
	protected void displaySTEMPData(String received_stemp) {
		STEMP.setText(received_stemp);
		final float value = Float.valueOf(received_stemp);
		final Handler lHandler = new Handler();
		Runnable lRunnable = new Runnable() {

			@Override
			public void run() {
				if (HANDLER_FLAG) {
					STEMPgraph2LastXValue++;
					try {
						STEMPgraphSeries.appendData(new GraphViewData(
								STEMPgraph2LastXValue, value), true, 1500);
					} catch (NumberFormatException e) {

					}

				}

			}
		};
		lHandler.postDelayed(lRunnable, 1000);
	}

	/**
	 * Display battery information
	 * 
	 * @param val
	 */
	protected void displayBATData(String val) {
		BAT.setText(val);
	}

	/**
	 * Display accelerometer X Value
	 * 
	 * @param val
	 */
	protected void displayXData(String val) {
		accX.setText(val);
		final int value = Integer.valueOf(val);
		final Handler lHandler = new Handler();
		Runnable lRunnable = new Runnable() {

			@Override
			public void run() {
				if (HANDLER_FLAG) {
					ACCXgraph2LastXValue++;
					try {
						ACCXgraphSeries.appendData(new GraphViewData(
								ACCXgraph2LastXValue, value), true, 1500);
					} catch (NumberFormatException e) {

					}

				}

			}
		};
		lHandler.postDelayed(lRunnable, 1000);

	}

	/**
	 * Display accelerometer Y Value
	 * 
	 * @param val
	 */
	protected void displayYData(String val) {
		accY.setText(val);
		final int value = Integer.valueOf(val);
		final Handler lHandler = new Handler();
		Runnable lRunnable = new Runnable() {

			@Override
			public void run() {
				if (HANDLER_FLAG) {
					ACCYgraph2LastXValue++;
					try {
						ACCYgraphSeries.appendData(new GraphViewData(
								ACCYgraph2LastXValue, value), true, 1500);
					} catch (NumberFormatException e) {

					}

				}

			}
		};
		lHandler.postDelayed(lRunnable, 1000);
	}

	/**
	 * Display accelerometer Z Value
	 * 
	 * @param val
	 */
	protected void displayZData(String val) {
		accZ.setText(val);
		final int value = Integer.valueOf(val);
		final Handler lHandler = new Handler();
		Runnable lRunnable = new Runnable() {

			@Override
			public void run() {
				if (HANDLER_FLAG) {
					ACCZgraph2LastXValue++;
					try {
						ACCZgraphSeries.appendData(new GraphViewData(
								ACCZgraph2LastXValue, value), true, 1500);
					} catch (NumberFormatException e) {

					}

				}

			}
		};
		lHandler.postDelayed(lRunnable, 1000);
	}

	/**
	 * Display accelerometer scan interval data
	 * 
	 * @param val
	 */
	protected void displayAccSensorScanData(String val) {
		if (acc_scan_interval != null) {
			acc_scan_interval.setText(val);
		}

	}

	/**
	 * Display accelerometer sensor type data
	 * 
	 * @param val
	 */

	protected void displayAccSensorTypeData(String val) {
		if (acc_sensortype != null) {
			acc_sensortype.setText(val);
		}

	}

	/**
	 * Display temperature sensor scan interval
	 * 
	 * @param val
	 */
	protected void displayStempSensorScanData(String val) {
		if (stemp_scan_interval != null) {
			stemp_scan_interval.setText(val);
		}

	}

	/**
	 * Display temperature sensor type
	 * 
	 * @param val
	 */

	protected void displayStempSensorTypeData(String val) {
		if (stemp_sensortype != null) {
			stemp_sensortype.setText(val);
		}

	}

	/**
	 * Display pressure sensor scan interval
	 * 
	 * @param val
	 */
	protected void displaySpressureSensorScanData(String val) {
		if (spressure_scan_interval != null) {
			spressure_scan_interval.setText(val);
		}
	}

	/**
	 * Display pressure sensor type
	 * 
	 * @param val
	 */

	protected void displaySPressureSensorTypeData(String val) {
		if (spressure_sensortype != null) {
			spressure_sensortype.setText(val);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.sensor_hub, container,
				false);
		LinearLayout parent = (LinearLayout) rootView
				.findViewById(R.id.parent_sensorhub);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		accX = (TextView) rootView.findViewById(R.id.acc_x_value);
		accY = (TextView) rootView.findViewById(R.id.acc_y_value);
		accZ = (TextView) rootView.findViewById(R.id.acc_z_value);
		BAT = (TextView) rootView.findViewById(R.id.bat_value);
		STEMP = (TextView) rootView.findViewById(R.id.temp_value);
		Spressure = (TextView) rootView.findViewById(R.id.pressure_value);

		// Locate device button listener
		Button locateDevice = (Button) rootView
				.findViewById(R.id.locate_device);
		locateDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Button btn = (Button) v;
				String buttonText = btn.getText().toString();
				String startText = getResources().getString(
						R.string.sen_hub_locate);
				String stopText = getResources().getString(
						R.string.sen_hub_locate_stop);
				if (buttonText.equalsIgnoreCase(startText)) {
					btn.setText(stopText);
					if (mWriteAlertCharacteristic != null) {
						BluetoothLeService.writeCharacteristicNoresponse(
								mWriteAlertCharacteristic, IMM_HIGH_ALERT);
					}

				} else {
					btn.setText(startText);
					if (mWriteAlertCharacteristic != null) {
						BluetoothLeService.writeCharacteristicNoresponse(
								mWriteAlertCharacteristic, IMM_NO_ALERT);
					}
				}

			}
		});
		final ImageButton acc_more = (ImageButton) rootView
				.findViewById(R.id.acc_more);
		final ImageButton stemp_more = (ImageButton) rootView
				.findViewById(R.id.stemp_more);
		final ImageButton spressure_more = (ImageButton) rootView
				.findViewById(R.id.spressure_more);

		final LinearLayout acc_layLayout = (LinearLayout) rootView
				.findViewById(R.id.acc_context_menu);
		final LinearLayout stemp_layLayout = (LinearLayout) rootView
				.findViewById(R.id.stemp_context_menu);
		final LinearLayout spressure_layLayout = (LinearLayout) rootView
				.findViewById(R.id.spressure_context_menu);

		// expand listener
		acc_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (acc_layLayout.getVisibility() != View.VISIBLE) {
					acc_more.setRotation(90);
					CustomSlideAnimation a = new CustomSlideAnimation(
							acc_layLayout, 500, CustomSlideAnimation.EXPAND);
					a.setHeight(height);
					acc_layLayout.startAnimation(a);
					acc_scan_interval = (EditText) rootView
							.findViewById(R.id.acc_sensor_scan_interval);
					if (ACCSensorScanCharacteristic != null) {
						acc_scan_interval.setText(ACCSensorScanCharacteristic);
					}
					acc_sensortype = (TextView) rootView
							.findViewById(R.id.acc_sensor_type);
					if (ACCSensorTypeCharacteristic != null) {
						acc_sensortype.setText(ACCSensorTypeCharacteristic);
					}
					acc_scan_interval
							.setOnEditorActionListener(new OnEditorActionListener() {

								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									if (actionId == EditorInfo.IME_ACTION_DONE) {
										int myNum = 0;

										try {
											myNum = Integer
													.parseInt(acc_scan_interval
															.getText()
															.toString());
										} catch (NumberFormatException nfe) {

										}

										BluetoothLeService
												.writeCharacteristicNoresponse(
														mReadACCSensorScanCharacteristic,
														myNum);
									}
									return false;
								}
							});
					Spinner spinner_filterconfiguration = (Spinner) rootView
							.findViewById(R.id.acc_filter_configuration);
					// Create an ArrayAdapter using the string array and a
					// default
					// spinner layout
					ArrayAdapter<CharSequence> adapter_filterconfiguration = ArrayAdapter
							.createFromResource(getActivity(),
									R.array.filter_configuration_alert_array,
									android.R.layout.simple_spinner_item);
					// Specify the layout to use when the list of choices
					// appears
					adapter_filterconfiguration
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					spinner_filterconfiguration
							.setAdapter(adapter_filterconfiguration);

				} else {
					acc_more.setRotation(-90);

					acc_scan_interval.setText("");
					acc_sensortype.setText("");
					CustomSlideAnimation a = new CustomSlideAnimation(
							acc_layLayout, 500, CustomSlideAnimation.COLLAPSE);
					height = a.getHeight();
					acc_layLayout.startAnimation(a);
				}
			}
		});
		// expand listener
		stemp_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (stemp_layLayout.getVisibility() != View.VISIBLE) {
					stemp_more.setRotation(90);
					CustomSlideAnimation a = new CustomSlideAnimation(
							stemp_layLayout, 500, CustomSlideAnimation.EXPAND);
					a.setHeight(height);
					stemp_layLayout.startAnimation(a);
					stemp_scan_interval = (EditText) rootView
							.findViewById(R.id.stemp_sensor_scan_interval);
					if (STEMPSensorScanCharacteristic != null) {
						stemp_scan_interval
								.setText(STEMPSensorScanCharacteristic);
					}
					stemp_sensortype = (TextView) rootView
							.findViewById(R.id.stemp_sensor_type);
					if (STEMPSensorTypeCharacteristic != null) {
						stemp_sensortype.setText(STEMPSensorTypeCharacteristic);
					}
					stemp_scan_interval
							.setOnEditorActionListener(new OnEditorActionListener() {

								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									if (actionId == EditorInfo.IME_ACTION_DONE) {
										int myNum = 0;

										try {
											myNum = Integer
													.parseInt(stemp_scan_interval
															.getText()
															.toString());
										} catch (NumberFormatException nfe) {

										}

										BluetoothLeService
												.writeCharacteristicNoresponse(
														mReadSTEMPSensorScanCharacteristic,
														myNum);
									}
									return false;
								}
							});

				} else {
					stemp_more.setRotation(-90);
					stemp_scan_interval.setText("");
					stemp_sensortype.setText("");
					CustomSlideAnimation a = new CustomSlideAnimation(
							stemp_layLayout, 500, CustomSlideAnimation.COLLAPSE);
					height = a.getHeight();
					stemp_layLayout.startAnimation(a);
				}
			}
		});
		// expand listener
		spressure_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (spressure_layLayout.getVisibility() != View.VISIBLE) {
					spressure_more.setRotation(90);
					CustomSlideAnimation a = new CustomSlideAnimation(
							spressure_layLayout, 500,
							CustomSlideAnimation.EXPAND);
					a.setHeight(height);
					spressure_layLayout.startAnimation(a);
					spressure_scan_interval = (EditText) rootView
							.findViewById(R.id.spressure_sensor_scan_interval);
					if (SPRESSURESensorScanCharacteristic != null) {
						spressure_scan_interval
								.setText(SPRESSURESensorScanCharacteristic);
					}
					spressure_sensortype = (TextView) rootView
							.findViewById(R.id.spressure_sensor_type);
					if (SPRESSURESensorTypeCharacteristic != null) {
						spressure_sensortype
								.setText(SPRESSURESensorTypeCharacteristic);
					}
					spressure_scan_interval
							.setOnEditorActionListener(new OnEditorActionListener() {

								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									if (actionId == EditorInfo.IME_ACTION_DONE) {
										int myNum = 0;

										try {
											myNum = Integer
													.parseInt(stemp_scan_interval
															.getText()
															.toString());
										} catch (NumberFormatException nfe) {

										}

										BluetoothLeService
												.writeCharacteristicNoresponse(
														mReadSPRESSURESensorScanCharacteristic,
														myNum);
									}
									return false;
								}
							});
					Spinner spinner_filterconfiguration = (Spinner) rootView
							.findViewById(R.id.spressure_filter_configuration);
					// Create an ArrayAdapter using the string array and a
					// default
					// spinner layout
					ArrayAdapter<CharSequence> adapter_filterconfiguration = ArrayAdapter
							.createFromResource(getActivity(),
									R.array.filter_configuration_alert_array,
									android.R.layout.simple_spinner_item);
					// Specify the layout to use when the list of choices
					// appears
					adapter_filterconfiguration
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					spinner_filterconfiguration
							.setAdapter(adapter_filterconfiguration);
					spressure_threshold_value = (EditText) rootView
							.findViewById(R.id.spressure_threshold);
					spressure_threshold_value
							.setOnEditorActionListener(new OnEditorActionListener() {

								@Override
								public boolean onEditorAction(TextView v,
										int actionId, KeyEvent event) {
									if (actionId == EditorInfo.IME_ACTION_DONE) {
										int myNum = 0;

										try {
											myNum = Integer
													.parseInt(spressure_threshold_value
															.getText()
															.toString());
										} catch (NumberFormatException nfe) {

										}

										BluetoothLeService
												.writeCharacteristicNoresponse(
														mReadSPRESSUREThresholdCharacteristic,
														myNum);
									}
									return false;
								}
							});

				} else {
					spressure_more.setRotation(-90);
					spressure_scan_interval.setText("");
					spressure_sensortype.setText("");
					spressure_threshold_value.setText("");
					CustomSlideAnimation a = new CustomSlideAnimation(
							spressure_layLayout, 500,
							CustomSlideAnimation.COLLAPSE);
					height = a.getHeight();
					spressure_layLayout.startAnimation(a);
				}

			}
		});
		ImageButton acc_graph = (ImageButton) rootView
				.findViewById(R.id.acc_graph);
		LinearLayout graph_layout = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_accelerometer);
		ACCgraphView = new LineGraphView(getActivity(), "");
		ACCXgraphSeries = new GraphViewSeries("X Data",
				new GraphViewSeriesStyle(Color.rgb(249, 19, 19), 3),
				new GraphViewData[] { new GraphViewData(0, 0) });
		ACCYgraphSeries = new GraphViewSeries("Y Data",
				new GraphViewSeriesStyle(Color.rgb(65, 3, 252), 3),
				new GraphViewData[] { new GraphViewData(0, 0) });
		ACCZgraphSeries = new GraphViewSeries("Z Data",
				new GraphViewSeriesStyle(Color.rgb(0, 0, 0), 3),
				new GraphViewData[] { new GraphViewData(0, 0) });
		STEMPgraphSeries = new GraphViewSeries("", new GraphViewSeriesStyle(
				R.color.main_bg_color, 3),
				new GraphViewData[] { new GraphViewData(0, 0) });

		ACCgraphView.setViewPort(0, 10);
		// set styles
		ACCgraphView.setScalable(true);

		ACCgraphView.getGraphViewStyle().setNumHorizontalLabels(5);
		ACCgraphView.getGraphViewStyle().setNumVerticalLabels(5);

		ACCgraphView.setScrollable(true);
		// add data
		ACCgraphView.addSeries(ACCXgraphSeries);
		ACCgraphView.addSeries(ACCYgraphSeries);
		ACCgraphView.addSeries(ACCZgraphSeries);

		// optional - legend
		// set legend
		ACCgraphView.setShowLegend(true);
		ACCgraphView.setLegendAlign(LegendAlign.BOTTOM);
		ACCgraphView.setLegendWidth(200);
		graph_layout.addView(ACCgraphView);
		final LinearLayout graph_layout_parent = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_accelerometer_parent);
		acc_graph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (graph_layout_parent.getVisibility() != View.VISIBLE) {
					graph_layout_parent.setVisibility(View.VISIBLE);

				} else {

					graph_layout_parent.setVisibility(View.GONE);
				}

			}
		});
		ImageButton stemp_graph = (ImageButton) rootView
				.findViewById(R.id.temp_graph);
		LinearLayout graph_layout_stemp = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_temperature);
		STEMPgraphView = new LineGraphView(getActivity(), "");
		STEMPgraphSeries = new GraphViewSeries("", new GraphViewSeriesStyle(
				Color.rgb(249, 19, 19), 3),
				new GraphViewData[] { new GraphViewData(0, 0) });

		STEMPgraphView.setViewPort(0, 10);
		// set styles
		STEMPgraphView.setScalable(true);

		STEMPgraphView.getGraphViewStyle().setNumHorizontalLabels(5);
		STEMPgraphView.getGraphViewStyle().setNumVerticalLabels(5);

		STEMPgraphView.setScrollable(true);
		// add data
		STEMPgraphView.addSeries(STEMPgraphSeries);
		graph_layout_stemp.addView(STEMPgraphView);

		final LinearLayout graph_layout_parent_Stemp = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_temperature_parent);
		stemp_graph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (graph_layout_parent_Stemp.getVisibility() != View.VISIBLE) {
					graph_layout_parent_Stemp.setVisibility(View.VISIBLE);

				} else {

					graph_layout_parent_Stemp.setVisibility(View.GONE);
				}

			}
		});
		ImageButton spressure_graph = (ImageButton) rootView
				.findViewById(R.id.pressure_graph);
		LinearLayout graph_layout_spresure = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_pressure);
		SPressuregraphView = new LineGraphView(getActivity(), "");
		SPressuregraphSeries = new GraphViewSeries("",
				new GraphViewSeriesStyle(Color.rgb(249, 19, 19), 3),
				new GraphViewData[] { new GraphViewData(0, 0) });

		SPressuregraphView.setViewPort(0, 10);
		// set styles
		SPressuregraphView.setScalable(true);

		SPressuregraphView.getGraphViewStyle().setNumHorizontalLabels(5);
		SPressuregraphView.getGraphViewStyle().setNumVerticalLabels(5);

		SPressuregraphView.setScrollable(true);
		// add data
		SPressuregraphView.addSeries(SPressuregraphSeries);
		graph_layout_spresure.addView(SPressuregraphView);

		final LinearLayout graph_layout_parent_Spressure = (LinearLayout) rootView
				.findViewById(R.id.graph_shub_pressure_parent);
		spressure_graph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (graph_layout_parent_Spressure.getVisibility() != View.VISIBLE) {
					graph_layout_parent_Spressure.setVisibility(View.VISIBLE);

				} else {

					graph_layout_parent_Spressure.setVisibility(View.GONE);
				}

			}
		});
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		HANDLER_FLAG = true;
		getGattData();
		getActivity().registerReceiver(mGattUpdateReceiver,
				Utils.makeGattUpdateIntentFilter());
		Utils.setUpActionBar(getActivity(),
				getResources().getString(R.string.sen_hub));
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		HANDLER_FLAG = false;
		getActivity().unregisterReceiver(mGattUpdateReceiver);
		stopBroadcastDataNotify(mNotifyACCXCharacteristic);
		stopBroadcastDataNotify(mNotifyACCYCharacteristic);
		stopBroadcastDataNotify(mNotifyACCZCharacteristic);
		stopBroadcastDataNotify(mNotifyBATCharacteristic);
		stopBroadcastDataNotify(mNotifySTEMPCharacteristic);
		stopBroadcastDataIndicate(mIndicateSPRESSURECharacteristic);
	}

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
			if (characteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						characteristic, false);

			}

		}

	}

	/**
	 * Stopping Broadcast receiver to broadcast indicate characteristics
	 * 
	 * @param gattCharacteristic
	 */
	public static void stopBroadcastDataIndicate(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
			if (characteristic != null) {
				BluetoothLeService.setCharacteristicNotification(
						characteristic, false);

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
			BluetoothLeService.setCharacteristicNotification(characteristic,
					true);

		}

	}

	/**
	 * Preparing Broadcast receiver to broadcast indicate characteristics
	 * 
	 * @param gattCharacteristic
	 */
	protected void prepareBroadcastDataIndicate(
			BluetoothGattCharacteristic gattCharacteristic) {
		final BluetoothGattCharacteristic characteristic = gattCharacteristic;
		final int charaProp = characteristic.getProperties();

		if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
			BluetoothLeService.setCharacteristicNotification(characteristic,
					true);

		}

	}

	/**
	 * Method to get required characteristics from service
	 */
	protected void getGattData() {
		for (int position = 0; position < mExtraservice.size(); position++) {
			HashMap<String, BluetoothGattService> item = mExtraservice
					.get(position);
			BluetoothGattService bgs = item.get("UUID");
			if (bgs.getUuid().equals(UUIDDatabase.UUID_ACCELEROMETER_SERVICE)) {
				mAccservice = bgs;
			}
			List<BluetoothGattCharacteristic> gattCharacteristicsCurrent = bgs
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicsCurrent) {
				String uuidchara = gattCharacteristic.getUuid().toString();
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_READING_X)) {
					mReadACCXCharacteristic = gattCharacteristic;
					mNotifyACCXCharacteristic = gattCharacteristic;

					prepareBroadcastDataRead(mReadACCXCharacteristic);
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_READING_Y)) {
					mReadACCYCharacteristic = gattCharacteristic;
					mNotifyACCYCharacteristic = gattCharacteristic;

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_READING_Z)) {
					mReadACCZCharacteristic = gattCharacteristic;
					mNotifyACCZCharacteristic = gattCharacteristic;
				}
				if (uuidchara.equalsIgnoreCase(GattAttributes.BATTERY_LEVEL)) {
					mReadBATCharacteristic = gattCharacteristic;
					mNotifyBATCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.TEMPERATURE_READING)) {
					mReadSTEMPCharacteristic = gattCharacteristic;
					mNotifySTEMPCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.BAROMETER_READING)) {
					mReadSPRESSURECharacteristic = gattCharacteristic;
					mIndicateSPRESSURECharacteristic = gattCharacteristic;
				}
				if (uuidchara.equalsIgnoreCase(GattAttributes.ALERT_LEVEL)) {
					mWriteAlertCharacteristic = gattCharacteristic;

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_SENSOR_SCAN_INTERVAL)) {
					mReadACCSensorScanCharacteristic = gattCharacteristic;

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_ANALOG_SENSOR)) {
					mReadACCSensorTypeCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.ACCELEROMETER_DATA_ACCUMULATION)) {
					mReadACCFilterConfigurationCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.TEMPERATURE_SENSOR_SCAN_INTERVAL)) {
					mReadSTEMPSensorScanCharacteristic = gattCharacteristic;

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.TEMPERATURE_ANALOG_SENSOR)) {
					mReadSTEMPSensorTypeCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.BAROMETER_SENSOR_SCAN_INTERVAL)) {
					mReadSPRESSURESensorScanCharacteristic = gattCharacteristic;

				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.BAROMETER_DIGITAL_SENSOR)) {
					mReadSPRESSURESensorTypeCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.BAROMETER_DATA_ACCUMULATION)) {
					mReadSPRESSUREFilterConfigurationCharacteristic = gattCharacteristic;
				}
				if (uuidchara
						.equalsIgnoreCase(GattAttributes.BAROMETER_THRESHOLD_FOR_INDICATION)) {
					mReadSPRESSUREThresholdCharacteristic = gattCharacteristic;
				}
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
