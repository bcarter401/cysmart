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

package com.cypress.cysmart.fragments;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.cypress.cysmart.R;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.Utils;

/**
 * 
 * Fragment to show the Cypress BLE Products
 */
public class CypressBLEProductsFragmnet extends Fragment {
	WebView mWebview;
	private ProgressDialog mProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_content, container,
				false);
		/**
		 * Checking the network and displaying the Cypress BLE products WebPage
		 * inside WebView.
		 */
		if (Utils.checkNetwork(getActivity())) {
			mWebview = (WebView) rootView.findViewById(R.id.fragment_content);
			WebSettings settings = mWebview.getSettings();
			settings.setJavaScriptEnabled(true);
			mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			mProgressBar = ProgressDialog.show(getActivity(),
					getString(R.string.app_name), getString(R.string.loading));
			mWebview.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}

				public void onPageFinished(WebView view, String url) {
					if (mProgressBar.isShowing()) {
						mProgressBar.dismiss();
					}
				}

			});
			mWebview.loadUrl(Constants.LINK_BLE_PRODUCTS);
			setUpActionBar();
		} else {
			Toast.makeText(getActivity(), R.string.alert_message_no_internet,
					Toast.LENGTH_LONG).show();
		}

		return rootView;
	}

	@Override
	public void onResume() {
		getActivity().setProgressBarIndeterminateVisibility(false);
		super.onResume();
		setUpActionBar();
	}

	public void setUpActionBar() {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setIcon(new ColorDrawable(getResources().getColor(
				android.R.color.transparent)));
		actionBar.setTitle(R.string.title_products);
	}
}
