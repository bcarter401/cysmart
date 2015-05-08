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

package com.cypress.cysmart;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout;

import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.fragments.AboutFragment;
import com.cypress.cysmart.fragments.ContactUsFragment;
import com.cypress.cysmart.fragments.CypressBLEProductsFragmnet;
import com.cypress.cysmart.fragments.NavigationDrawerFragment;
import com.cypress.cysmart.fragments.ProfileControlFragment;
import com.cypress.cysmart.fragments.ProfileScanningFragment;
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
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;

/**
 * Base activity to hold all fragments
 * 
 */
public class HomePageActivity extends FragmentActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to manage connections of the Blue tooth LE Device
	 */
	public static BluetoothLeService mBluetoothLeService;

	/**
	 * Code to manage Service life cycle.
	 */
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			// Initializing the service
			if (!mBluetoothLeService.initialize()) {
				Logger.d("Service not initialized");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	public static DrawerLayout parentView;
	public static FrameLayout containerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isTablet(this)) {
			Logger.d("tablet");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		} else {
			Logger.d("Phone");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		// Getting the id of the navigation fragment from the attached xml
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);

		getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		parentView = (DrawerLayout) findViewById(R.id.drawer_layout);
		containerView = (FrameLayout) findViewById(R.id.container);

		Intent gattServiceIntent = new Intent(getApplicationContext(),
				BluetoothLeService.class);
		startService(gattServiceIntent);

		/**
		 * Attaching the profileScanning fragment to start scanning for nearby
		 * devices
		 */
		ProfileScanningFragment profileScanningFragment = new ProfileScanningFragment();
		displayView(profileScanningFragment,
				Constants.PROFILE_SCANNING_FRAGMENT_TAG);

	}

	/**
	 * Release the service when user exit from application
	 */

	@Override
	protected void onDestroy() {
		if (mBluetoothLeService != null && mBluetoothLeService.mBound) {
			unbindService(mServiceConnection);
		}
		super.onDestroy();
	}

	/**
	 * Handling the back pressed actions
	 */
	@Override
	public void onBackPressed() {

		// Getting the current active fragment
		Fragment currentFragment = getSupportFragmentManager()
				.findFragmentById(R.id.container);

		// Profile scanning fragment active
		if (currentFragment instanceof ProfileScanningFragment) {
			if (parentView.isDrawerOpen(Gravity.START)) {
				parentView.closeDrawer(Gravity.START);
			} else {
				alertbox();
			}

		} else if (currentFragment instanceof CypressBLEProductsFragmnet
				|| currentFragment instanceof AboutFragment
				|| currentFragment instanceof ContactUsFragment) {
			if (parentView.isDrawerOpen(Gravity.START)) {
				parentView.closeDrawer(Gravity.START);
			} else {
				// Guiding the user back to profile scanning fragment
				Intent intent = getIntent();
				finish();
				overridePendingTransition(R.anim.slide_left, R.anim.push_left);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right, R.anim.push_right);
			}

		} else if (currentFragment instanceof ProfileControlFragment) {
			if (parentView.isDrawerOpen(Gravity.START)) {
				parentView.closeDrawer(Gravity.START);
			} else {
				// Guiding the user back to profile scanning fragment
				BluetoothLeService.disconnect();
				Intent intent = getIntent();
				finish();
				overridePendingTransition(R.anim.slide_right, R.anim.push_right);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left, R.anim.push_left);
				super.onBackPressed();
			}

		} else if (currentFragment instanceof HeartRateService
				|| currentFragment instanceof HealthTemperatureService
				|| currentFragment instanceof DeviceInformationService
				|| currentFragment instanceof BatteryInformationService
				|| currentFragment instanceof BloodPressureService
				|| currentFragment instanceof CapsenseService
				|| currentFragment instanceof CSCService
				|| currentFragment instanceof FindMeService
				|| currentFragment instanceof GlucoseService
				|| currentFragment instanceof RGBFragment
				|| currentFragment instanceof RSCService
				|| currentFragment instanceof SensorHubService
				|| currentFragment instanceof GattServicesFragment) {
			if (parentView.isDrawerOpen(Gravity.START)) {
				parentView.closeDrawer(Gravity.START);
			} else {
				Utils.setUpActionBar(
						this,
						getResources().getString(
								R.string.profile_control_fragment));
				super.onBackPressed();
			}
		} else {
			if (parentView.isDrawerOpen(Gravity.START)) {
				parentView.closeDrawer(Gravity.START);
			} else {
				super.onBackPressed();

			}

		}

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		/**
		 * Update the main content by replacing fragments with user selected
		 * option
		 */
		switch (position) {
		case 1:
			/**
			 * BLE Devices
			 */
			Intent intent = getIntent();
			finish();
			overridePendingTransition(R.anim.slide_left, R.anim.push_left);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right, R.anim.push_right);

			break;
		case 2:
			/**
			 * Cypress BLE Products
			 */
			CypressBLEProductsFragmnet cypressBLEProductsFragmnet = new CypressBLEProductsFragmnet();
			displayView(cypressBLEProductsFragmnet,
					Constants.BLE_PRODUCTS_FRAGMENT_TAG);

			break;
		case 3:
			/**
			 * Contact us
			 */
			ContactUsFragment contactUsFragment = new ContactUsFragment();
			displayView(contactUsFragment, Constants.CONTACT_FRAGMENT_TAG);

			break;
		case 4:
			/**
			 * About
			 */
			AboutFragment aboutFragment = new AboutFragment();
			displayView(aboutFragment, Constants.ABOUT_FRAGMENT_TAG);
			break;
		default:
			break;
		}

	}

	/**
	 * Used for replacing the main content of the view with provided fragments
	 * 
	 * @param fragment
	 * @param tag
	 */
	public void displayView(Fragment fragment, String tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, fragment, tag).commit();
	}

	/**
	 * Used to change the title in action bar
	 * 
	 * @param number
	 */
	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			getString(R.string.title_devices);
			break;
		case 2:
			getString(R.string.title_products);
			break;
		case 3:
			getString(R.string.title_contact);
			break;
		case 4:
			getString(R.string.title_about);
			break;

		}
	}

	/**
	 * Method to detect whether the device is phone or tablet
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Method to create an alert before user exit from the application
	 */
	protected void alertbox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				HomePageActivity.this);
		builder.setMessage(
				getResources().getString(R.string.alert_message_exit))
				.setCancelable(false)
				.setTitle(getResources().getString(R.string.app_name))
				.setPositiveButton(
						getResources()
								.getString(R.string.alert_message_exit_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Finish the current activity
								HomePageActivity.this.finish();
							}
						})
				.setNegativeButton(
						getResources().getString(
								R.string.alert_message_exit_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Cancel the dialog box
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
