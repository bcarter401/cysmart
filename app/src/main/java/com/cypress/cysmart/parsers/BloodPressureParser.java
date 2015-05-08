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

package com.cypress.cysmart.parsers;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.cypress.cysmart.R;
import com.cypress.cysmart.utils.Logger;
/**
 * 
 * Class used for parsing Blood pressure related information
 */
public class BloodPressureParser {

	public static final int FIRST_BITMASK = 0x01;
	public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
	public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
	public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
	public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
	public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
	public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
	public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

	/**
	 * Get the Blood Pressure
	 * 
	 * @param characteristic
	 * @return string
	 */
	public static String getSystolicBloodPressure(
			BluetoothGattCharacteristic characteristic, Context ctx) {
		String pressure = "";
		if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {

			float valueSYS = characteristic.getFloatValue(
					BluetoothGattCharacteristic.FORMAT_SFLOAT, 1).floatValue();
			pressure = String.format("%3.3f", valueSYS);
			Logger.i("Systolic Pressure>>>>" + valueSYS);
		} else {
			float valueSYS = characteristic.getFloatValue(
					BluetoothGattCharacteristic.FORMAT_SFLOAT, 1).floatValue();
			pressure = String.format("%3.3f", valueSYS);
			Logger.i("Systolic Pressure>>>>" + valueSYS);
		}
		return pressure;
	}

	public static String getSystolicBloodPressureUnit(
			BluetoothGattCharacteristic characteristic, Context ctx) {

		String unit = "";
		if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {
			unit = ctx.getResources().getString(R.string.blood_pressure_kPa);

		} else {
			unit = ctx.getResources().getString(R.string.blood_pressure_mmHg);

		}
		return unit;
	}

	public static String getDiaStolicBloodPressure(
			BluetoothGattCharacteristic characteristic, Context ctx) {
		String pressure = "";
		if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {
			float valueDIA = characteristic.getFloatValue(
					BluetoothGattCharacteristic.FORMAT_SFLOAT, 3).floatValue();
			pressure = String.format("%3.3f", valueDIA);
			Logger.i("Diastolic Pressure>>>>" + valueDIA);

		} else {
			float valueDIA = characteristic.getFloatValue(
					BluetoothGattCharacteristic.FORMAT_SFLOAT, 3).floatValue();
			new DecimalFormat("#.###").format(valueDIA);
			pressure = String.format("%3.3f", valueDIA);
			Logger.i("Diastolic Pressure>>>>" + valueDIA);
		}
		return pressure;
	}

	public static String getDiaStolicBloodPressureUnit(
			BluetoothGattCharacteristic characteristic, Context ctx) {

		String unit = "";
		if (BloodPressureUnitsFlagSet(characteristic.getValue()[0])) {
			unit = ctx.getResources().getString(R.string.blood_pressure_kPa);

		} else {
			unit = ctx.getResources().getString(R.string.blood_pressure_mmHg);

		}
		return unit;
	}

	public static String getBloodPressureTimeStamp(
			BluetoothGattCharacteristic characteristic, Context ctx) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		String time = "";
		if (BloodPressureTimeStampFlagSet(characteristic.getValue()[0])) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(
					1,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT16, 7)
							.intValue());
			calendar.set(
					2,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 9)
							.intValue());
			calendar.set(
					5,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 10)
							.intValue());
			calendar.set(
					11,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 11)
							.intValue());
			calendar.set(
					12,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 12)
							.intValue());
			calendar.set(
					13,
					characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 13)
							.intValue());

			time = sdf.format(calendar.getTime());
			Logger.i("Blood Pressure time stamp set value==" + time);
			return time;
		} else {
			return time;
		}
	}

	/**
	 * Checking the unitsFlag of blood Pressure
	 * 
	 * @param flags
	 * @return
	 */
	public static boolean BloodPressureUnitsFlagSet(byte flags) {
		if ((flags & FIRST_BITMASK) != 0)
			return true;
		return false;
	}

	/**
	 * Checking the TimeStamp Flag of blood Pressure
	 * 
	 * @param flags
	 * @return
	 */
	public static boolean BloodPressureTimeStampFlagSet(byte flags) {
		if ((flags & THIRD_BITMASK) != 0)
			return true;
		return false;
	}

	/**
	 * Checking the Pulse Rate Flag of blood Pressure
	 * 
	 * @param flags
	 * @return
	 */
	public static boolean BloodPressurePulseRateFlagSet(byte flags) {
		if ((flags & FOURTH_BITMASK) != 0)
			return true;
		return false;
	}

	/**
	 * Checking the User ID Flag of blood Pressure
	 * 
	 * @param flags
	 * @return
	 */
	public static boolean BloodPressureUserIDFlagSet(byte flags) {
		if ((flags & FIFTH_BITMASK) != 0)
			return true;
		return false;
	}

	/**
	 * Checking the Measurement Status Flag of blood Pressure
	 * 
	 * @param flags
	 * @return
	 */
	public static boolean BloodPressureMeasurementStatusFlagSet(byte flags) {
		if ((flags & SIXTH_BITMASK) != 0)
			return true;
		return false;
	}
}
