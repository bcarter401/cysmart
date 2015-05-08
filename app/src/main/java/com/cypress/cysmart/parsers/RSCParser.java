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
 * Class used for parsing Running speed related information
 */
public class RSCParser {

	static ArrayList<String> rscInfo = new ArrayList<String>();

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
	public static ArrayList<String> getRunningSpeednCadence(
			BluetoothGattCharacteristic characteristic) {

		String runningSpeed;
		String distanceRan;
		String currCadence; // bkc
		String currStrideLen; // bkc
		String extraChar; // bkc
		byte byte0 = characteristic.getValue()[0];
		int i = 0 + 1;
		boolean flag;
		boolean flag1;
		boolean flag2;
		float f;
		int j;
		int k;
		int l;
		int m; // bkc
		float f1;
		float f2;
		int i1;
		if ((byte0 & 1) > 0) {
			flag = true;
		} else {
			flag = false;
		}
		if ((byte0 & 2) > 0) {
			flag1 = true;
		} else {
			flag1 = false;
		}
		if ((byte0 & 4) > 0) {
			flag2 = true;
		} else {
			flag2 = false;
		}
		f = 3.6F * ((float) characteristic.getIntValue(18, i).intValue() / 256F);
		runningSpeed = "" + f;
		rscInfo.add(0, runningSpeed);
		j = i + 2;
		k = characteristic.getIntValue(17, j).intValue();
		l = j + 1;
		f1 = -1F;
		if (flag) {
			f1 = characteristic.getIntValue(18, l).intValue();
			l += 2;
		}
		f2 = -1F;
		if (flag1) {
			f2 = (float) characteristic.getIntValue(20, l).intValue() / 10F;
			distanceRan = "" + f2;
			rscInfo.add(1, distanceRan);
		}
		if (flag2) {
			i1 = 1;
		} else {
			i1 = 0;
		}

		m = characteristic.getIntValue(17, 10).intValue(); //bkc


		currCadence = "" + k; // bkc
		currStrideLen = "" + f1; // bkc
		extraChar = "" + m; // bkc
		rscInfo.add(2, currCadence); // bkc, cadence
		rscInfo.add(3, currStrideLen); // bkc, strideLen
		rscInfo.add(4, extraChar); // bkc, extraChar

		Logger.d("Values are " + f + " " + k + " " + f2 + " " + f1 + " " + i1);
		Logger.d("Values are " + m);
		return rscInfo;
	}

}
