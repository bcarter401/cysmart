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

package com.cypress.cysmart.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.datamodels.CapSenseButtonsGridModel;

/**
 * Adapter class for CapSense Buttons. Uses CapSenseButtonsGridModel data model
 */
public class CapSenseButtonsGridAdapter extends
		ArrayAdapter<CapSenseButtonsGridModel> {

	Context mContext;
	/**
	 * Resource identifier
	 */
	int mResourceId;
	/**
	 * CapSenseButtonsGridModel data list.
	 */
	ArrayList<CapSenseButtonsGridModel> data = new ArrayList<CapSenseButtonsGridModel>();
	ArrayList<Integer> status = new ArrayList<Integer>();
	int status8bit, status16bit;

	public CapSenseButtonsGridAdapter(Context context, int layoutResourceId,
			ArrayList<CapSenseButtonsGridModel> data,
			ArrayList<Integer> statusMapping) {
		super(context, layoutResourceId, data);
		this.mContext = context;
		this.mResourceId = layoutResourceId;
		this.data = data;
		this.status = statusMapping;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		ViewHolder holder = null;
		// General GridView Optimization code
		if (itemView == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = layoutInflater.inflate(R.layout.capsense_buttons_item,
					parent, false);

			holder = new ViewHolder();
			holder.imgItem = (ImageView) itemView
					.findViewById(R.id.button_image);
			holder.txtItem = (TextView) itemView.findViewById(R.id.txtItem);
			itemView.setTag(holder);
		} else {
			holder = (ViewHolder) itemView.getTag();
		}
		/**
		 * Setting the ImageResource and title using the data model.
		 */
		CapSenseButtonsGridModel item = data.get(position);
		if (item != null) {
			holder.imgItem.setImageResource(item.getImage());
			holder.txtItem.setText(item.getTitle());
		}
		// Getting the status
		status8bit = status.get(2);
		status16bit = status.get(1);

		// Setting the status indication on the image
		if (position > 7) {
			int k = 1 << position - 8;
			if ((status16bit & k) > 0) {
				holder.imgItem.setImageResource(R.drawable.green_color_btn);
			} else {
				holder.imgItem.setImageResource(R.drawable.capsense_btn_bg);

			}

		} else {
			int k = 1 << position;
			if ((status8bit & k) > 0) {
				holder.imgItem.setImageResource(R.drawable.green_color_btn);
			} else {
				holder.imgItem.setImageResource(R.drawable.capsense_btn_bg);
			}
		}

		return itemView;
	}

	/**
	 * Holder class for GridView items
	 */
	static class ViewHolder {
		ImageView imgItem;
		TextView txtItem;
	}
}
