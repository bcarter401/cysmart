package com.cypress.cysmart.fragments;

import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.R;
import com.cypress.cysmart.gattdb.GattServicesFragment;
import com.cypress.cysmart.profiles.BatteryInformationService;
import com.cypress.cysmart.profiles.BloodPressureService;
import com.cypress.cysmart.profiles.CSCService;
import com.cypress.cysmart.profiles.CapsenseService;
import com.cypress.cysmart.profiles.DeviceInformationService;
import com.cypress.cysmart.profiles.FindMeService;
import com.cypress.cysmart.profiles.GlucoseService;
import com.cypress.cysmart.profiles.HealthTemperatureService;
import com.cypress.cysmart.profiles.HeartRateService;
import com.cypress.cysmart.profiles.RGBFragment;
import com.cypress.cysmart.profiles.RSCService;
import com.cypress.cysmart.profiles.SensorHubService;
import com.cypress.cysmart.utils.CarouselLinearLayout;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;

public class CarouselFragment extends Fragment {

	/**
	 * BluetoothGattService current
	 */
	public static BluetoothGattService service;
	/**
	 * BluetoothGattCharacteristic Notify
	 */
	public static BluetoothGattCharacteristic mNotifyCharacteristic;
	/**
	 * BluetoothGattCharacteristic Read
	 */
	public static BluetoothGattCharacteristic mReadCharacteristic;

	/**
	 * Current UUID
	 */
	public static String mCurrentUUID;

	/**
	 * CarouselView Image is actually a button
	 */
	public Button mCarouselButton;

	public final static HashMap<String, BluetoothGattService> bleHashMap = new HashMap<String, BluetoothGattService>();

	/**
	 * BluetoothGattCharacteristic List length
	 */
	int mgattCharacteristicsLength = 0;
	/**
	 * BluetoothGattCharacteristic current
	 */
	BluetoothGattCharacteristic mCurrentCharacteristic;

	/**
	 * Argument keys passed between fragments
	 */
	public final static String EXTRA_FRAG_POS = "com.cypress.cysmart.fragments.CarouselFragment.EXTRA_FRAG_POS";
	public final static String EXTRA_FRAG_SCALE = "com.cypress.cysmart.fragments.CarouselFragment.EXTRA_FRAG_SCALE";
	public final static String EXTRA_FRAG_NAME = "com.cypress.cysmart.fragments.CarouselFragment.EXTRA_FRAG_NAME";
	public final static String EXTRA_FRAG_UUID = "com.cypress.cysmart.fragments.CarouselFragment.EXTRA_FRAG_UUID";
	public final static String EXTRA_FRAG_DEVICE_ADDRESS = "com.cypress.cysmart.fragments.CarouselFragment.EXTRA_FRAG_DEVICE_ADDRESS";

	/**
	 * Fragment new Instance creation with arguments
	 * 
	 * @param context
	 * @param pos
	 * @param scale
	 * @param name
	 * @param uuid
	 * @param service
	 * @return CarouselFragment
	 */
	public static Fragment newInstance(HomePageActivity context, int pos,
			float scale, String name, String uuid, BluetoothGattService service) {
		CarouselFragment fragment = new CarouselFragment();
		bleHashMap.put(uuid, service);
		Bundle b = new Bundle();
		b.putInt(EXTRA_FRAG_POS, pos);
		b.putFloat(EXTRA_FRAG_SCALE, scale);
		b.putString(EXTRA_FRAG_NAME, name);
		b.putString(EXTRA_FRAG_UUID, uuid);
		fragment.setArguments(b);
		return fragment;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = (LinearLayout) inflater.inflate(
				R.layout.carousel_fragment_item, container, false);

		final int pos = this.getArguments().getInt(EXTRA_FRAG_POS);
		final String name = this.getArguments().getString(EXTRA_FRAG_NAME);
		final String uuid = this.getArguments().getString(EXTRA_FRAG_UUID);

		TextView tv = (TextView) rootView.findViewById(R.id.text);
		tv.setText(name);

		mCarouselButton = (Button) rootView.findViewById(R.id.content);
		mCarouselButton.setBackgroundResource(pos);
		mCarouselButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Getting the Mapped service from the UUID
				service = bleHashMap.get(uuid);
				mCurrentUUID = service.getUuid().toString();

				// Adding the selected UUID to DataLogger
				Logger.datalog(
						"Selected UUID :" + service.getUuid().toString(),
						getActivity().getApplicationContext());
				
				//Heart rate service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.HEART_RATE_SERVICE)) {
					HeartRateService heartRateMeasurement = new HeartRateService()
							.create(service);
					displayView(heartRateMeasurement);

				}
				//Device information service
				if (service
						.getUuid()
						.toString()
						.equalsIgnoreCase(
								GattAttributes.DEVICE_INFORMATION_SERVICE)) {
					DeviceInformationService deviceInformationMeasurementFragment = new DeviceInformationService()
							.create(service);
					displayView(deviceInformationMeasurementFragment);

				}
				//Battery service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.BATTERY_SERVICE)) {
					BatteryInformationService batteryInfoFragment = new BatteryInformationService()
							.create(service);
					displayView(batteryInfoFragment);
				}
				//Health Temperature Measurement
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.HEALTH_TEMP_SERVICE)) {
					HealthTemperatureService healthTempMeasurement = new HealthTemperatureService()
							.create(service);
					displayView(healthTempMeasurement);

				}
				//Find Me
				if (service
						.getUuid()
						.toString()
						.equalsIgnoreCase(
								GattAttributes.IMMEDIATE_ALERT_SERVICE)
						|| service
								.getUuid()
								.toString()
								.equalsIgnoreCase(
										GattAttributes.LINK_LOSS_SERVICE)
						|| service
								.getUuid()
								.toString()
								.equalsIgnoreCase(
										GattAttributes.TRANSMISSION_POWER_SERVICE)) {
					FindMeService findMeService = new FindMeService().create(
							service,
							ProfileScanningFragment.gattServiceFindMeData);
					displayView(findMeService);
				}
				//CapSense
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.CAPSENSE_SERVICE)) {
					List<BluetoothGattCharacteristic> gattCapSenseCharacteristics = service
							.getCharacteristics();
					CapsenseService capSensePager = new CapsenseService()
							.create(service, gattCapSenseCharacteristics.size());
					displayView(capSensePager);
				}
				//GattDB
				if (service
						.getUuid()
						.toString()
						.equalsIgnoreCase(
								GattAttributes.GENERIC_ATTRIBUTE_SERVICE)
						|| service
								.getUuid()
								.toString()
								.equalsIgnoreCase(
										GattAttributes.GENERIC_ACCESS_SERVICE)) {
					GattServicesFragment gattsericesfragment = new GattServicesFragment()
							.create();
					displayView(gattsericesfragment);

				}
				//RGB Service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.RGB_LED_SERVICE)) {

					RGBFragment rgbfragment = new RGBFragment().create(service);
					displayView(rgbfragment);

				}
				//Glucose Service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.GLUCOSE_SERVICE)) {
					GlucoseService glucosefragment = new GlucoseService()
							.create(service);
					displayView(glucosefragment);

				}
				//Blood Pressure Service
				if (service
						.getUuid()
						.toString()
						.equalsIgnoreCase(GattAttributes.BLOOD_PRESSURE_SERVICE)) {
					BloodPressureService bloodPressureService = new BloodPressureService()
							.create(service);
					displayView(bloodPressureService);

				}
				//Running service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.RSC_SERVICE)) {
					RSCService rscService = new RSCService().create(service);
					displayView(rscService);

				}
				//Cycling service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.CSC_SERVICE)) {
					CSCService cscService = new CSCService().create(service);
					displayView(cscService);
				}
				//Barometer(SensorHub) Service
				if (service.getUuid().toString()
						.equalsIgnoreCase(GattAttributes.BAROMETER_SERVICE)) {
					SensorHubService sensorHubService = new SensorHubService()
							.create(service,
									ProfileScanningFragment.gattServiceSensorHubData);
					displayView(sensorHubService);
				}
			}
		});
		CarouselLinearLayout root = (CarouselLinearLayout) rootView
				.findViewById(R.id.root);
		float scale = this.getArguments().getFloat(EXTRA_FRAG_SCALE);
		root.setScaleBoth(scale);
		return rootView;

	}

	/**
	 * Used for replacing the main content of the view with provided fragments
	 * 
	 * @param fragment
	 * @param tag
	 */
	public void displayView(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().add(R.id.container, fragment)
				.addToBackStack(null).commit();
	}
}
