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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * This is a custom log class that will manage logs in the project. Using the
 * <b>disableLog()</b> all the logs can be disabled in the project during the
 * production stage <b> enableLog()</b> will allow to enable the logs , by
 * default the logs will be visible.<br>
 * *
 */
public class Logger {

	private static final String LOG_TAG = "CySmart Android";
	private static boolean logflag = true;
	private static boolean datalogflag = true;
	private static String TAG = "Logger";

	public static final void d(String message) {
		show(Log.DEBUG, LOG_TAG, message);

	}

	public static final void d(String tag, String message) {
		show(Log.DEBUG, tag, message);

	}

	public static final void w(String message) {
		show(Log.WARN, LOG_TAG, message);

	}

	public static final void i(String message) {
		show(Log.INFO, LOG_TAG, message);

	}

	public static final void datalog(String message, Context mContext) {
		// show(Log.INFO, LOG_TAG, message);
		saveLogData(message, mContext);

	}

	/**
	 * print log for info/error/debug/warn/verbose
	 * 
	 * @param type
	 *            : <br>
	 *            Log.INFO <br>
	 *            Log.ERROR <br>
	 *            Log.DEBUG <br>
	 *            Log.WARN <br>
	 *            Log.VERBOSE Log.
	 */
	private static void show(int type, String tag, String msg) {

		if (msg.length() > 4000) {
			Log.i("Length ", msg.length() + "");

			while (msg.length() > 4000) {
				show(type, tag, msg.substring(0, 4000));
				msg = msg.substring(4000, msg.length());

			}
		}
		if (logflag)
			switch (type) {
			case Log.INFO:
				Log.i(tag, msg);
				break;
			case Log.ERROR:
				Log.e(tag, msg);
				break;
			case Log.DEBUG:
				Log.d(tag, msg);
				break;
			case Log.WARN:
				Log.w(tag, msg);
				break;
			case Log.VERBOSE:
				Log.v(tag, msg);
				break;
			case Log.ASSERT:
				Log.wtf(tag, msg);
				break;
			default:
				break;
			}

	}

	/**
	 * printStackTrace for exception *
	 */
	public static void show(Exception exception) {
		try {
			if (logflag)
				exception.printStackTrace();

		} catch (NullPointerException e) {
			Logger.show(e);
		}
	}

	public static boolean enableLog() {
		logflag = true;
		return logflag;
	}

	public static boolean disableLog() {
		logflag = false;
		return logflag;
	}

	/**
	 * Show heap size of the device *
	 */
	public static void showHeapSize() {
		int mb = 1024 * 1024;
		show(Log.INFO, TAG, "Heap : " + Runtime.getRuntime().totalMemory() / mb
				+ " MB");
	}
	private static void saveLogData(String message, Context mContext) {

		File datalogfile = null,filedir=null,previousfile=null;
		message = Utils.GetTimeandDate() + ": " + message;
		try {
			filedir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "CySmart");
			if (!filedir.exists()) {
				filedir.mkdir();
			}
			datalogfile = new File(filedir.getAbsoluteFile() + File.separator
					+ "datalogefile_" + Utils.GetDate() + ".txt");
			if (!datalogfile.exists()) {
				datalogfile.createNewFile();
			}
			previousfile = new File(filedir.getAbsoluteFile() + File.separator
					+ "datalogefile_" + Utils.GetDateSevenDaysBack() + ".txt");
			if (previousfile.exists()) {
				previousfile.delete();
			}
			try {

				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(datalogfile, true),
						"UTF-8");
				BufferedWriter fbw = new BufferedWriter(writer);
				fbw.write(message);
				fbw.newLine();
				fbw.close();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void saveLogDataqq(String message, Context mContext) {

		File datalogfile = null,filedir=null;
		int date_count=1;
		message = Utils.GetTimeandDate() + ": " + message;
		try {
			filedir=new File(Environment.getExternalStorageDirectory()+File.separator+"CySmart");
			if(!filedir.exists()) {
				filedir.mkdir();
				
			}
		if (!Utils.getStringSharedPreference(mContext,Constants.SHARED_PREFRENCE_KEY_CURRENT_DATE)
						.equalsIgnoreCase(Utils.GetDateFromMilliseconds())) {
		
        String count=Utils.getStringSharedPreference(mContext, Constants.SHARED_PREFRENCE_KEY_LOG_COUNT);
        if (count.length()==0) {
			date_count++;
			Utils.setStringSharedPreference(mContext, Constants.SHARED_PREFRENCE_KEY_LOG_COUNT, ""+date_count);
		}
        else
        {
        	date_count=Integer.parseInt(count);
        	date_count++;
        	if (date_count>7) {
        		if(filedir.exists()) {
    				filedir.delete();
    				
    			}
        		if(!filedir.exists()) {
    				filedir.mkdir();
    				
    			}
        		date_count=1;
        		Utils.setStringSharedPreference(mContext, Constants.SHARED_PREFRENCE_KEY_LOG_COUNT, ""+date_count);
			}
        	Utils.setStringSharedPreference(mContext, Constants.SHARED_PREFRENCE_KEY_LOG_COUNT, ""+date_count);
        }
		
		}
			
		//	Utils.setStringSharedPreference(context, key, value)
			
			
			
			
			
			
			datalogfile = new File(filedir.getAbsoluteFile()
					+ File.separator + "datalogefile"+date_count+".txt");
			if (!datalogfile.exists()) {
				// Utils.
				datalogfile.createNewFile();
				Utils.setStringSharedPreference(mContext,
						Constants.SHARED_PREFRENCE_KEY_CURRENT_DATE,
						Utils.GetDateFromMilliseconds());
			}
			if (datalogfile.exists()) {
				if (Utils.getStringSharedPreference(mContext,
						Constants.SHARED_PREFRENCE_KEY_CURRENT_DATE)
						.equalsIgnoreCase(Utils.GetDateFromMilliseconds())) {
					try {

						OutputStreamWriter writer = new OutputStreamWriter(
								new FileOutputStream(datalogfile, true),
								"UTF-8");
						BufferedWriter fbw = new BufferedWriter(writer);
						fbw.write(message);
						fbw.newLine();
						fbw.close();
					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
					}
				}
				else
				{
					//datalogfile.delete();
					//datalogfile.createNewFile();
					Utils.setStringSharedPreference(mContext,
							Constants.SHARED_PREFRENCE_KEY_CURRENT_DATE,
							Utils.GetDateFromMilliseconds());
					try {

						OutputStreamWriter writer = new OutputStreamWriter(
								new FileOutputStream(datalogfile, true),
								"UTF-8");
						BufferedWriter fbw = new BufferedWriter(writer);
						fbw.write(message);
						fbw.newLine();
						fbw.close();
					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
					}
				}

			}

		} catch (Exception e) {
			Log.i("Test", e.getMessage());
		}
		Log.i(LOG_TAG, message);
		// TODO Auto-generated method stub

	}

}
