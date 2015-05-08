package com.cypress.cysmart.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.R;
import com.cypress.cysmart.fragments.CarouselFragment;
import com.cypress.cysmart.fragments.ProfileControlFragment;
import com.cypress.cysmart.utils.CarouselLinearLayout;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;

/**
 * Adapter class for the CarouselView. extends FragmentPagerAdapter
 */
public class CarouselPagerAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener {

	/**
	 * CarouselLinearLayout variables for animation
	 */
	private CarouselLinearLayout mCurrentCarouselLinearLayout = null;
	private CarouselLinearLayout mNextCarouselLinearLayout = null;
	private HomePageActivity context;
	private ProfileControlFragment containerFragment;
	private FragmentManager fm;
	private float scale;

	public ArrayList<HashMap<String, BluetoothGattService>> currentServiceData;

	public CarouselPagerAdapter(Activity context,
			ProfileControlFragment containerFragment,
			FragmentManager fragmentManager,
			ArrayList<HashMap<String, BluetoothGattService>> currentServiceData) {
		super(fragmentManager);
		this.fm = fragmentManager;
		this.context = (HomePageActivity) context;
		this.containerFragment = containerFragment;
		this.currentServiceData = currentServiceData;

	}

	@Override
	public Fragment getItem(int position) {

		// Make the first pager bigger than others
		if (position == ProfileControlFragment.FIRST_PAGE) {
			scale = ProfileControlFragment.BIG_SCALE;
		} else {
			scale = ProfileControlFragment.SMALL_SCALE;
		}
		position = position % ProfileControlFragment.PAGES;
		HashMap<String, BluetoothGattService> item = currentServiceData
				.get(position);
		BluetoothGattService bgs = item.get("UUID");

		/**
		 * Looking for the image corresponding to the UUID.if no suitable image
		 * resource is found assign the default unknown resource
		 */

		int imageId = GattAttributes.lookupImage(bgs.getUuid().toString(),
				R.drawable.unknown);
		String name = GattAttributes.lookup(
				bgs.getUuid().toString(),
				context.getResources().getString(
						R.string.profile_control_unknown_service));
		String uuid = bgs.getUuid().toString();
		if (uuid.equalsIgnoreCase(GattAttributes.LINK_LOSS_SERVICE)
				|| uuid.equalsIgnoreCase(GattAttributes.TRANSMISSION_POWER_SERVICE)
				|| uuid.equalsIgnoreCase(GattAttributes.IMMEDIATE_ALERT_SERVICE)) {
			name = context.getResources().getString(R.string.findme_fragment);
		}
		if (uuid.equalsIgnoreCase(GattAttributes.CAPSENSE_SERVICE)) {
			List<BluetoothGattCharacteristic> gattCharacteristics = bgs
					.getCharacteristics();
			if (gattCharacteristics.size() > 1) {
				imageId = GattAttributes.lookupImageCapSense(bgs.getUuid()
						.toString(), R.drawable.unknown);
				name = GattAttributes.lookupNameCapSense(
						bgs.getUuid().toString(),
						context.getResources().getString(
								R.string.profile_control_unknown_service));
			} else {
				String characteristicUUID = gattCharacteristics.get(0)
						.getUuid().toString();
				imageId = GattAttributes.lookupImageCapSense(
						characteristicUUID, R.drawable.unknown);
				name = GattAttributes.lookupNameCapSense(
						characteristicUUID,
						context.getResources().getString(
								R.string.profile_control_unknown_service));
			}

		}
		if (uuid.equalsIgnoreCase(GattAttributes.GENERIC_ACCESS_SERVICE)
				|| uuid.equalsIgnoreCase(GattAttributes.GENERIC_ATTRIBUTE_SERVICE)) {
			name = context.getResources().getString(R.string.gatt_db);
		}
		if (uuid.equalsIgnoreCase(GattAttributes.BAROMETER_SERVICE)
				|| uuid.equalsIgnoreCase(GattAttributes.ACCELEROMETER_SERVICE)
				|| uuid.equalsIgnoreCase(GattAttributes.ANALOG_TEMPERATURE_SERVICE)) {
			name = context.getResources().getString(R.string.sen_hub);
		}
		Fragment curFragment = CarouselFragment.newInstance(context, imageId,
				scale, name, uuid, bgs);
		return curFragment;
	}

	@Override
	public int getCount() {
		return ProfileControlFragment.PAGES * ProfileControlFragment.LOOPS;

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		/**
		 * Page scroll animation. Zooming forward and backward based on the
		 * scroll
		 */
		if (positionOffset >= 0f && positionOffset <= 1f) {
			if (position < getCount() - 1) {
				try {
					mCurrentCarouselLinearLayout = getRootView(position);
					mNextCarouselLinearLayout = getRootView(position + 1);
					mCurrentCarouselLinearLayout
							.setScaleBoth(ProfileControlFragment.BIG_SCALE
									- ProfileControlFragment.DIFF_SCALE
									* positionOffset);
					mNextCarouselLinearLayout
							.setScaleBoth(ProfileControlFragment.SMALL_SCALE
									+ ProfileControlFragment.DIFF_SCALE
									* positionOffset);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public void onPageSelected(int position) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	private CarouselLinearLayout getRootView(int position) {
		// Logger.e("Pos " + position + "rootview " + getFragmentTag(position));

		CarouselLinearLayout ly;
		try {
			ly = (CarouselLinearLayout) fm
					.findFragmentByTag(this.getFragmentTag(position)).getView()
					.findViewById(R.id.root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		if (ly != null)
			return ly;
		return null;
	}

	private String getFragmentTag(int position) {
		return "android:switcher:" + containerFragment.pager.getId() + ":"
				+ position;
	}
}
