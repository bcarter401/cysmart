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

import java.util.ArrayList;

import com.cypress.cysmart.utils.Logger;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * 
 * Class used for parsing Cycling speed and cadence related information
 */
public class CSCParser {

	static ArrayList<String> CSCInfo = new ArrayList<String>();
	static String cyclingSpeed;
	static String cyclingCadence;

	private static int mLastWheelEventTime = 0;
	private static int mLastWheelRevolutions = 0;
	public static final int FIRST_BITMASK = 0x01;
	public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
	public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
	public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
	public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
	public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
	public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
	public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

	/**
	 * Get the Running Speed and Cadence
	 * 
	 * @param characteristic
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getCyclingSpeednCadence(
			BluetoothGattCharacteristic characteristic) {
		boolean flag = true;
		byte byte0 = characteristic.getValue()[0];
		int i = 0 + 1;
		boolean flag1;
		if ((byte0 & 1) > 0) {
			flag1 = flag;
		} else {
			flag1 = false;
		}
		if ((byte0 & 2) <= 0) {
			flag = false;
		}
		if (flag1) {
			int i1 = characteristic.getIntValue(20, i).intValue();
			int j1 = i + 4;
			int k1 = characteristic.getIntValue(18, j1).intValue();
			i = j1 + 2;
			onWheelMeasurementReceived(i1, k1);
		}
		if (flag) {
			int j = characteristic.getIntValue(18, i).intValue();
			int k = i + 2;
			int l = characteristic.getIntValue(18, k).intValue();
			Logger.d("Values are " + j + " " + l);
			cyclingCadence = "" + j;
			CSCInfo.add(1, cyclingCadence);
		}
		return CSCInfo;
	}
	public static void onWheelMeasurementReceived(int i, int j) {
		if (mLastWheelEventTime == j) {
			return;
		}
		if (mLastWheelRevolutions >= 0) {
			float f2;
			if (j < mLastWheelEventTime) {
			} else {
			}
			f2 = ((float) i) / 1000F;
			cyclingSpeed = "" + f2;
			CSCInfo.add(0, cyclingSpeed);
		}
		mLastWheelRevolutions = i;
		mLastWheelEventTime = j;
	}
}
