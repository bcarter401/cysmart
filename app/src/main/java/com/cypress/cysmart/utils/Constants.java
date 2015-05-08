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

/**
 * Constants used in the project
 * 
 */
public class Constants {

	/**
	 * Fragment Tags
	 */
	public static String PROFILE_SCANNING_FRAGMENT_TAG = "profile scanning";
	public static String ABOUT_FRAGMENT_TAG = "about cysmart";
	public static String BLE_PRODUCTS_FRAGMENT_TAG = "ble products";
	public static String CONTACT_FRAGMENT_TAG = "contact cypress";
	public static String SETTINGS_FRAGMENT_TAG = "settings";
	public static String PROFILE_CONTROL_FRAGMENT_TAG = "profile control";
	public static String DEVICE_INFORMATION_SERVICE_FRAGMENT_TAG = "device information";
	public static String BATTERY_SERVICE_FRAGMENT_TAG = "battery";
	public static String HEART_RATE_SERVICE_FRAGMENT_TAG = "heart rate service";
	public static String THERMOMETER_SERVICE_FRAGMENT_TAG = "thermometer service";
	public static String FIND_ME_SERVICE_FRAGMENT_TAG = "find me service";
	public static String CAPSENSE_SERVICE_FRAGMENT_TAG = "capsense service";
	public static String RGB_SERVICE_FRAGMENT_TAG = "rgb service";
	public static String GATTDB_FRAGMENT_TAG = "gatt db service";
	public static String GATTDBCHAR_FRAGMENT_TAG = "gatt db characterisitics";
	public static String GATTDB_SELECTED_SERVICE = "gatt db service";
	public static String GATTDB_SELECTED_CHARACTERISTICE = "selected characterisitics";
	/**
	 * DataLogger constants
	 */
	public static String DATA_LOGGER_FILE_NAAME = "file name";
	public static String DATA_LOGGER_FLAG = "Data Logger Flag";

	/**
	 * SharedPreference Constants
	 */
	public static String SHARED_PREFRENCE_KEY_CURRENT_DATE = "Current Date";
	public static String SHARED_PREFRENCE_KEY_LOG_COUNT = "Log Count";

	/**
	 * Extras Constants
	 */
	public static final String EXTRA_HRM_EEVALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HRM_EEVALUE";
	public static final String EXTRA_HRM_RRVALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HRM_RRVALUE";
	public static final String EXTRA_HRM_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HRM_VALUE";
	public static final String EXTRA_BSL_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_BSL_VALUE";

	public static final String EXTRA_MNS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_MNS_VALUE";
	public static final String EXTRA_MONS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_MONS_VALUE";
	public static final String EXTRA_SNS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SNS_VALUE";
	public static final String EXTRA_HRS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HRS_VALUE";
	public static final String EXTRA_SRS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SRS_VALUE";
	public static final String EXTRA_PNP_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_PNP_VALUE";
	public static final String EXTRA_SID_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SID_VALUE";

	public static final String EXTRA_HTM_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HTM_VALUE";
	public static final String EXTRA_HSL_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_HSL_VALUE";

	public static final String EXTRA_BTL_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_BTL_VALUE";

	public static final String EXTRA_CAPPROX_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_CAPPROX_VALUE";
	public static final String EXTRA_CAPSLIDER_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_CAPSLIDER_VALUE";
	public static final String EXTRA_CAPBUTTONS_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_CAPBUTTONS_VALUE";

	public static final String EXTRA_ALERT_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ALERT_VALUE";
	public static final String EXTRA_POWER_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_POWER_VALUE";

	public static final String EXTRA_RGB_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_RGB_VALUE";

	public static final String EXTRA_GLUCOSE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_GLUCOSE_VALUE";

	public static final String EXTRA_BYTE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_BYTE_VALUE";
	public static final String EXTRA_PRESURE_SYSTOLIC_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_PRESURE_SYSTOLIC_VALUE";
	public static final String EXTRA_PRESURE_DIASTOLIC_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_PRESURE_DIASTOLIC_VALUE";
	public static final String EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE";
	public static final String EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE";

	public static final String EXTRA_RSC_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_RSC_VALUE";
	public static final String EXTRA_CSC_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_CSC_VALUE";
	public static final String EXTRA_ACCX_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACCX_VALUE";
	public static final String EXTRA_ACCY_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACCY_VALUE";
	public static final String EXTRA_ACCZ_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACCZ_VALUE";
	public static final String EXTRA_STEMP_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_STEMP_VALUE";
	public static final String EXTRA_SPRESSURE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SPRESSURE_VALUE";
	public static final String EXTRA_ACC_SENSOR_SCAN_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACC_SENSOR_SCAN_VALUE";
	public static final String EXTRA_ACC_SENSOR_TYPE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACC_SENSOR_TYPE_VALUE";
	public static final String EXTRA_ACC_FILTER_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_ACC_FILTER_VALUE";
	public static final String EXTRA_STEMP_SENSOR_SCAN_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_STEMP_SENSOR_SCAN_VALUE";
	public static final String EXTRA_STEMP_SENSOR_TYPE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_STEMP_SENSOR_TYPE_VALUE";
	public static final String EXTRA_SPRESSURE_SENSOR_SCAN_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SPRESSURE_SENSOR_SCAN_VALUE";
	public static final String EXTRA_SPRESSURE_SENSOR_TYPE_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SPRESSURE_SENSOR_TYPE_VALUE";
	public static final String EXTRA_SPRESSURE_THRESHOLD_VALUE = "com.cypress.cysmart.backgroundservices.EXTRA_SPRESSURE_THRESHOLD_VALUE";

	/**
	 * Links
	 */
	public static final String LINK_CONTACT_US = "http://www.cypress.com/?id=7&source=header";
	public static final String LINK_BLE_PRODUCTS = "http://www.cypress.com/?id=2&source=header";
}
