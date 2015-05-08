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

package com.cypress.cysmart.utils;

import java.util.HashMap;

import com.cypress.cysmart.R;

/**
 * This class includes a subset of standard GATT attributes and carousel image
 * mapping
 */
public class GattAttributes {
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	private static HashMap<String, Integer> attributesImageMap = new HashMap<String, Integer>();
	private static HashMap<String, Integer> attributesCapSenseImageMap = new HashMap<String, Integer>();
	private static HashMap<String, String> attributesCapSense = new HashMap<String, String>();
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String CHARACTERISTIC_PRESENTATION_FORMAT = "00002904-0000-1000-8000-00805f9b34fb";

	/**
	 * Services
	 */
	public static String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
	public static String HEALTH_THERMO_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
	public static String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
	public static String HEALTH_TEMP_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
	public static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
	public static String IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
	public static String CAPSENSE_SERVICE = "0000cab5-0000-1000-8000-00805f9b34fb";
	public static String RGB_LED_SERVICE = "0000cbbb-0000-1000-8000-00805f9b34fb";
	public static String LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb";
	public static String TRANSMISSION_POWER_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
	public static String BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
	public static String GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb";
	public static String RSC_SERVICE = "00001814-0000-1000-8000-00805f9b34fb";
	public static String BAROMETER_SERVICE = "00040001-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_SERVICE = "00040020-0000-1000-8000-00805f9b0131";
	public static String ANALOG_TEMPERATURE_SERVICE = "00040030-0000-1000-8000-00805f9b0131";
	public static String CSC_SERVICE = "00001816-0000-1000-8000-00805f9b34fb";

	/**
	 * Heart rate characteristics
	 */
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String BODY_SENSOR_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
	public static String HEART_RATE_CONTROL_POINT = "00002a39-0000-1000-8000-00805f9b34fb";

	/**
	 * Device information characteristics
	 */
	public static String SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
	public static String MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
	public static String SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
	public static String FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
	public static String HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
	public static String SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
	public static String MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
	public static String PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";
	public static String IEEE = "00002a2a-0000-1000-8000-00805f9b34fb";
	/**
	 * Battery characteristics
	 */
	public static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

	/**
	 * Health Thermometer characteristics
	 */
	public static String HEALTH_TEMP_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
	public static String TEMPERATURE_TYPE = "00002a1d-0000-1000-8000-00805f9b34fb";
	public static String TEMPERATURE_INTERMEDIATE = "00002a1e-0000-1000-8000-00805f9b34fb";
	public static String TEMPERATURE_MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb";

	/**
	 * Gatt services
	 */
	public static String GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
	public static String GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";

	/**
	 * Find me characteristics
	 */
	public static String ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";

	public static String TRANSMISSION_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb";

	/**
	 * Capsense characteristics
	 */
	public static String CAPSENSE_PROXIMITY = "0000caa1-0000-1000-8000-00805f9b34fb";
	public static String CAPSENSE_SLIDER = "0000caa2-0000-1000-8000-00805f9b34fb";
	public static String CAPSENSE_BUTTONS = "0000caa3-0000-1000-8000-00805f9b34fb";

	/**
	 * RGB characteristics
	 */
	public static String RGB_LED = "0000cbb1-0000-1000-8000-00805f9b34fb";

	/**
	 * GlucoseService characteristics
	 */
	public static String GLUCOSE_COCNTRN = "00002a18-0000-1000-8000-00805f9b34fb";
	public static String GLUCOSE_MESUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb";
	public static String GLUCOSE_FEATURE = "00002a51-0000-1000-8000-00805f9b34fb";
	public static String RECORD_ACCESS_CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";
	/**
	 * Blood Pressure service Characteristics
	 */
	public static String BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb";
	public static String BLOOD_INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
	public static String BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb";

	/**
	 * Running Speed & Cadence Characteristics
	 */
	public static String RSC_MEASUREMENT = "00002a53-0000-1000-8000-00805f9b34fb";
	public static String RSC_FEATURE = "00002a54-0000-1000-8000-00805f9b34fb";
	public static String SC_SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb";
	public static String SC_CONTROL_POINT = "00002a55-0000-1000-8000-00805f9b34fb";

	/**
	 * Cycling Speed & Cadence Characteristics
	 */
	public static String CSC_MEASUREMENT = "00002a5b-0000-1000-8000-00805f9b34fb";
	public static String CSC_FEATURE = "00002a5c-0000-1000-8000-00805f9b34fb";

	/**
	 * Barometer service characteristics
	 */
	public static String BAROMETER_DIGITAL_SENSOR = "00040002-0000-1000-8000-00805f9b0131";
	public static String BAROMETER_SENSOR_SCAN_INTERVAL = "00040004-0000-1000-8000-00805f9b0131";
	public static String BAROMETER_DATA_ACCUMULATION = "00040007-0000-1000-8000-00805f9b0131";
	public static String BAROMETER_READING = "00040009-0000-1000-8000-00805f9b0131";
	public static String BAROMETER_THRESHOLD_FOR_INDICATION = "0004000d-0000-1000-8000-00805f9b0131";
	/**
	 * Accelerometer service characteristics
	 */
	public static String ACCELEROMETER_ANALOG_SENSOR = "00040021-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_SENSOR_SCAN_INTERVAL = "00040023-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_DATA_ACCUMULATION = "00040026-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_READING_X = "00040028-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_READING_Y = "0004002b-0000-1000-8000-00805f9b0131";
	public static String ACCELEROMETER_READING_Z = "0004002d-0000-1000-8000-00805f9b0131";
	/**
	 * Analog Temperature service characteristics
	 */
	public static String TEMPERATURE_ANALOG_SENSOR = "00040031-0000-1000-8000-00805f9b0131";
	public static String TEMPERATURE_SENSOR_SCAN_INTERVAL = "00040032-0000-1000-8000-00805f9b0131";
	public static String TEMPERATURE_READING = "00040033-0000-1000-8000-00805f9b0131";

	static {
		// Services.
		attributes.put(HEART_RATE_SERVICE, "Heart Rate Service");
		attributes.put(HEALTH_THERMO_SERVICE, "Health Thermometer Service");
		attributes.put(GENERIC_ACCESS_SERVICE, "Generic Access Service");
		attributes.put(GENERIC_ATTRIBUTE_SERVICE, "Generic Attribute Service");
		attributes
				.put(DEVICE_INFORMATION_SERVICE, "Device Information Service");
		attributes.put(BATTERY_SERVICE,// "0000180f-0000-1000-8000-00805f9b34fb",
				"Battery Service");
		attributes.put(IMMEDIATE_ALERT_SERVICE, "Immediate Alert");
		attributes.put(LINK_LOSS_SERVICE, "Link Loss");
		attributes.put(TRANSMISSION_POWER_SERVICE, "Tx Power");
		attributes.put(CAPSENSE_SERVICE, "CapSense Service");
		attributes.put(RGB_LED_SERVICE, "RGB LED Service");
		attributes.put(GLUCOSE_SERVICE, "Glucose Service");
		attributes.put(BLOOD_PRESSURE_SERVICE, "Blood Pressure Service");
		attributes.put(RSC_SERVICE, "Running Speed & Cadence Service");
		attributes.put(BAROMETER_SERVICE, "Barometer Service");
		attributes.put(ACCELEROMETER_SERVICE, "Accelerometer Service");
		attributes
				.put(ANALOG_TEMPERATURE_SERVICE, "Analog Temperature Service");
		attributes.put(CSC_SERVICE, "Cycling Speed & Cadence Service");

		// Heart Rate Characteristics.
		attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
		attributes.put(BODY_SENSOR_LOCATION, "Body Sensor Location");
		attributes.put(HEART_RATE_CONTROL_POINT, "Heart Rate Control Point");

		// Health thermometer Characteristics.
		attributes.put(HEALTH_TEMP_MEASUREMENT,
				"Health thermometer Measurement");
		attributes.put(TEMPERATURE_TYPE, "Temperature Type");
		attributes.put(TEMPERATURE_INTERMEDIATE, "Intermediate Temperature");
		attributes
				.put(TEMPERATURE_MEASUREMENT_INTERVAL, "Measurement Interval");

		// Device Information Characteristics
		attributes.put(SYSTEM_ID, "System ID");
		attributes.put(MODEL_NUMBER_STRING, "Model Number String");
		attributes.put(SERIAL_NUMBER_STRING, "Serial Number String");
		attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
		attributes.put(HARDWARE_REVISION_STRING, "Hardware Revision String");
		attributes.put(SOFTWARE_REVISION_STRING, "Software Revision String");
		attributes.put(MANUFACTURER_NAME_STRING, "Manufacturer Name String");
		attributes.put(PNP_ID, "PnP ID");
		attributes.put(IEEE,
				"IEEE 11073-20601 Regulatory Certification Data List");

		// Battery service characteristics
		attributes.put(BATTERY_LEVEL, "Battery Level");

		// Find me service characteristics
		attributes.put(ALERT_LEVEL, "Alert Level");
		attributes.put(TRANSMISSION_POWER_LEVEL, "Tx Power Level");

		// Capsense Characteristics
		attributes.put(CAPSENSE_BUTTONS, "Capsense Button");
		attributes.put(CAPSENSE_PROXIMITY, "Capsense Proximity");
		attributes.put(CAPSENSE_SLIDER, "Capsense Slider");

		// RGB Characteristics
		attributes.put(RGB_LED, "RGB Led");

		// Glucose Characteristics
		attributes.put(GLUCOSE_COCNTRN, "Glucose Measurement");
		attributes.put(GLUCOSE_MESUREMENT_CONTEXT,
				"Glucose Measurement Context");
		attributes.put(GLUCOSE_FEATURE, "Glucose Feature");
		attributes.put(RECORD_ACCESS_CONTROL_POINT,
				"Record Access Control Point");

		// Blood pressure service characteristics
		attributes.put(BLOOD_INTERMEDIATE_CUFF_PRESSURE,
				"Intermediate Cuff Pressure");
		attributes.put(BLOOD_PRESSURE_FEATURE, "Blood Pressure Feature");
		attributes
				.put(BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");

		// SensorHub Characteristics
		attributes.put(ACCELEROMETER_ANALOG_SENSOR,
				"Accelerometer Analog Sensor");
		attributes.put(ACCELEROMETER_DATA_ACCUMULATION,
				"Accelerometer Data Accumulation");
		attributes.put(ACCELEROMETER_READING_X, "Accelerometer X Reading");
		attributes.put(ACCELEROMETER_READING_Y, "Accelerometer Y Reading");
		attributes.put(ACCELEROMETER_READING_Z, "Accelerometer Z Reading");
		attributes.put(ACCELEROMETER_SENSOR_SCAN_INTERVAL,
				"Accelerometer Sensor Scan Interval");
		attributes.put(BAROMETER_DATA_ACCUMULATION,
				"Barometer Data Accumulation");
		attributes.put(BAROMETER_DIGITAL_SENSOR, "Barometer Digital Sensor");
		attributes.put(BAROMETER_READING, "Barometer Reading");
		attributes.put(BAROMETER_SENSOR_SCAN_INTERVAL,
				"Barometer Sensor Scan Interval");
		attributes.put(BAROMETER_THRESHOLD_FOR_INDICATION,
				"Barometer Threshold for Indication");
		attributes.put(TEMPERATURE_ANALOG_SENSOR, "Temperature Analog Sensor");
		attributes.put(TEMPERATURE_READING, "Temperature Reading");
		attributes.put(TEMPERATURE_SENSOR_SCAN_INTERVAL,
				"Temperature Sensor Scan Interval");

		// Running Speed Characteristics
		attributes.put(RSC_MEASUREMENT, "Running Speed Cadence Measurement");
		attributes.put(RSC_FEATURE, "Running Speed Feature");
		attributes.put(SC_CONTROL_POINT, "Speed and Cadence Control Point");
		attributes.put(SC_SENSOR_LOCATION, "Speed and Cadence Sensor Location");

		// Cycling Speed Characteristics
		attributes.put(CSC_MEASUREMENT, "Cycling Speed Cadence Measurement");
		attributes.put(CSC_FEATURE, "Cycling Speed Feature");

		// Services Image Mapping.
		attributesImageMap.put(HEART_RATE_SERVICE,// "0000180d-0000-1000-8000-00805f9b34fb",
				R.drawable.heart_rate);
		attributesImageMap.put(HEALTH_TEMP_SERVICE,
				R.drawable.thermometer_carousel);
		attributesImageMap.put(GENERIC_ACCESS_SERVICE,// "00001800-0000-1000-8000-00805f9b34fb",
				R.drawable.gatt_db_dark_blue);
		attributesImageMap.put(GENERIC_ATTRIBUTE_SERVICE,// "00001801-0000-1000-8000-00805f9b34fb",
				R.drawable.gatt_db_dark_blue);
		attributesImageMap.put(DEVICE_INFORMATION_SERVICE,// "0000180a-0000-1000-8000-00805f9b34fb",
				R.drawable.device_info);
		attributesImageMap.put(BATTERY_SERVICE,// "0000180f-0000-1000-8000-00805f9b34fb",
				R.drawable.battery);
		attributesImageMap.put(CAPSENSE_SERVICE, R.drawable.capsense);
		attributesImageMap.put(CAPSENSE_SLIDER, R.drawable.capsense_slider);
		attributesImageMap.put(CAPSENSE_PROXIMITY, R.drawable.capsense_slider);
		attributesImageMap.put(CAPSENSE_BUTTONS, R.drawable.capsense_buttons);
		attributesImageMap.put(RGB_LED_SERVICE, R.drawable.rgb_raw_view);
		attributesImageMap.put(IMMEDIATE_ALERT_SERVICE, R.drawable.find_me);
		attributesImageMap.put(LINK_LOSS_SERVICE, R.drawable.find_me);
		attributesImageMap.put(TRANSMISSION_POWER_SERVICE, R.drawable.find_me);
		attributesImageMap.put(GLUCOSE_SERVICE, R.drawable.glucose);
		attributesImageMap.put(BLOOD_PRESSURE_SERVICE,
				R.drawable.blood_pressure);
		attributesImageMap.put(RSC_SERVICE, R.drawable.rsc);
		attributesImageMap.put(CSC_SERVICE, R.drawable.cpc);
		attributesImageMap.put(BAROMETER_SERVICE, R.drawable.sensor_hub);
		// CapSense Image Mapping
		attributesCapSenseImageMap.put(CAPSENSE_SERVICE, R.drawable.capsense);
		attributesCapSenseImageMap.put(CAPSENSE_PROXIMITY,
				R.drawable.capsense_btn_proximity);
		attributesCapSenseImageMap.put(CAPSENSE_SLIDER,
				R.drawable.capsense_slider);
		attributesCapSenseImageMap.put(CAPSENSE_BUTTONS,
				R.drawable.capsense_buttons);

		// Capsense Characteristics
		attributesCapSense.put(CAPSENSE_SERVICE, "Capsense Services");
		attributesCapSense.put(CAPSENSE_BUTTONS, "Capsense Button");
		attributesCapSense.put(CAPSENSE_PROXIMITY, "Capsense Proximity");
		attributesCapSense.put(CAPSENSE_SLIDER, "Capsense Slider");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}

	public static int lookupImage(String uuid, int defaultImage) {
		int imageId = attributesImageMap.get(uuid) != null ? attributesImageMap
				.get(uuid) : defaultImage;
		return imageId;
	}

	public static String lookupNameCapSense(String uuid, String defaultName) {
		String name = attributesCapSense.get(uuid);
		return name == null ? defaultName : name;
	}

	public static int lookupImageCapSense(String uuid, int defaultImage) {
		int imageId = attributesCapSenseImageMap.get(uuid) != null ? attributesCapSenseImageMap
				.get(uuid) : defaultImage;
		return imageId;
	}

	public static boolean lookupreqHRMCharacateristics(String uuid) {
		String name = attributes.get(uuid);
		return name == null ? false : true;

	}

	public static String getname(String uuid) {
		String name = attributes.get(uuid);
		return name == null ? "Not found" : name;
	}
}