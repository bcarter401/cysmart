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

package com.cypress.cysmart.profiles;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypress.cysmart.R;
import com.cypress.cysmart.backgroundservices.BluetoothLeService;
import com.cypress.cysmart.utils.Constants;
import com.cypress.cysmart.utils.GattAttributes;
import com.cypress.cysmart.utils.Logger;
import com.cypress.cysmart.utils.Utils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display the running speed and cadence
 */
public class RSCServiceBackUp extends Fragment {

    // GATT Services and characteristics
    public static BluetoothGattService mservice;
    public static BluetoothGattCharacteristic mNotifyCharacteristic;
    public static BluetoothGattCharacteristic mReadCharacteristic;

    // Data view variables
    AlertDialog alert;
    TextView mDistanceRan;
    TextView mAverageSpeed;
    TextView mCaloriesBurnt;
    RelativeLayout mScrollViewRSC;
    Chronometer mTimer;
    EditText mWeightEdittext;

    // Data variables
    float distanceRanInt;
    float weightInt;
    float caloriesBurntInt;
    String weightBurn;

    // Graph view variables
    LinearLayout mgraphLayoutParent;
    private GraphView mgraphView;
    private GraphViewSeries mgraphSeries;
    private double mgraphLastXValue = 1;
    private boolean HANDLER_FLAG = false;

    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Disconnected
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (alert != null && !alert.isShowing()) {
                    Utils.connectionLostalertbox(getActivity(), alert);
                    // Adding data to Data Logger
                    Logger.datalog(
                            getResources().getString(
                                    R.string.data_logger_device_disconnected),
                            getActivity().getApplicationContext());
                }

            }
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Check CSC service
                if (extras.containsKey(Constants.EXTRA_RSC_VALUE)) {
                    ArrayList<String> received_rsc_data = intent
                            .getStringArrayListExtra(Constants.EXTRA_RSC_VALUE);
                    displayLiveData(received_rsc_data);
                }
            }

        }
    };

    public RSCServiceBackUp create(BluetoothGattService service) {
        mservice = service;
        RSCServiceBackUp fragment = new RSCServiceBackUp();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rsc_convert,
                container, false);
        mDistanceRan = (TextView) rootView.findViewById(R.id.running_distance);
        mAverageSpeed = (TextView) rootView.findViewById(R.id.running_speed);
        mCaloriesBurnt = (TextView) rootView.findViewById(R.id.calories_burnt);
        mTimer = (Chronometer) rootView.findViewById(R.id.time_counter);
        mWeightEdittext = (EditText) rootView.findViewById(R.id.weight_data);
        mScrollViewRSC = (RelativeLayout) rootView
                .findViewById(R.id.scroll_rsc);
        LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.parent);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        mgraphLayoutParent = (LinearLayout) rootView
                .findViewById(R.id.graph_rsc_parent);
        LinearLayout mgraphLayout = (LinearLayout) rootView
                .findViewById(R.id.graph_rsc);
        mgraphView = new LineGraphView(getActivity(), "Running Speed");
        mgraphSeries = new GraphViewSeries(
                new GraphViewData[]{new GraphViewData(0, 0)});
        // add data
        mgraphView.addSeries(mgraphSeries);
        // set view port, start=2, size=10
        mgraphView.setViewPort(0, 10);
        // set styles
        mgraphView.getGraphViewStyle().setNumHorizontalLabels(5);
        mgraphView.getGraphViewStyle().setNumVerticalLabels(5);
        mgraphView.setScrollable(true);
        mgraphLayout.addView(mgraphView);

        // Start/Stop listener
        Button start_stop_btn = (Button) rootView
                .findViewById(R.id.start_stop_btn);
        start_stop_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String buttonText = btn.getText().toString();
                String startText = getResources().getString(
                        R.string.blood_pressure_start_btn);
                String stopText = getResources().getString(
                        R.string.blood_pressure_stop_btn);
                if (buttonText.equalsIgnoreCase(startText)) {
                    btn.setText(stopText);
                    mWeightEdittext.setEnabled(false);
                    if (mNotifyCharacteristic != null) {
                        stopBroadcastDataNotify(mNotifyCharacteristic);
                    }
                    getGattData();
                    mScrollViewRSC.setVisibility(View.VISIBLE);
                    mTimer.start();
                    mTimer.setBase(SystemClock.elapsedRealtime());
                } else {
                    mWeightEdittext.setEnabled(true);
                    btn.setText(startText);
                    stopBroadcastDataNotify(mNotifyCharacteristic);
                    mTimer.stop();
                }

            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Display live running data
     */
    private void displayLiveData(final ArrayList<String> rsc_data) {
        if (rsc_data != null) {
            weightBurn = mWeightEdittext.getText().toString();
            mAverageSpeed.setText(rsc_data.get(0).toString());
            mDistanceRan.setText(rsc_data.get(1).toString());
            final Handler lHandler = new Handler();
            Runnable lRunnable = new Runnable() {

                @Override
                public void run() {
                    if (HANDLER_FLAG) {
                        mgraphLastXValue++;
                        float val = Float.valueOf(rsc_data.get(0).toString());
                        mgraphSeries.appendData(new GraphViewData(
                                mgraphLastXValue, val), true, 500);

                    }

                }
            };
            lHandler.postDelayed(lRunnable, 1000);
            try {
                Number numRun = NumberFormat.getInstance().parse(
                        rsc_data.get(1));
                distanceRanInt = numRun.floatValue();
                Number numWeight = NumberFormat.getInstance().parse(weightBurn);
                weightInt = numWeight.floatValue();
                Log.e("Disatanec", "" + distanceRanInt + "  " + weightBurn);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            caloriesBurntInt = (float) weightInt / distanceRanInt;
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(4);
            String finalBurn = formatter.format(caloriesBurntInt);
            mCaloriesBurnt.setText(finalBurn);
        }
        // Adding data to Data Logger
        Logger.datalog(
                getResources()
                        .getString(
                                R.string.data_logger_characteristic_running_speed_cadence),
                getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        HANDLER_FLAG = true;
        getActivity().registerReceiver(mGattUpdateReceiver,
                Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar(getActivity(),
                getResources().getString(R.string.rsc_fragment));
    }

    @Override
    public void onPause() {
        super.onPause();
        HANDLER_FLAG = false;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        if (mNotifyCharacteristic != null) {
            stopBroadcastDataNotify(mNotifyCharacteristic);
        }
        super.onDestroy();
    }

    ;

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    public void stopBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (mNotifyCharacteristic != null) {
                Logger.d("Stopped notification");
                BluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }

        }
        // Adding data to Data Logger
        Logger.datalog(
                getResources().getString(
                        R.string.data_logger_characteristic_notify_stop),
                getActivity().getApplicationContext());
    }

    /**
     * Preparing Broadcast receiver to broadcast read characteristics
     *
     * @param gattCharacteristic
     */
    protected void prepareBroadcastDataRead(
            BluetoothGattCharacteristic gattCharacteristic) {
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            mReadCharacteristic = characteristic;
            BluetoothLeService.readCharacteristic(characteristic);
        }
        // Adding data to Data Logger
        Logger.datalog(
                getResources().getString(
                        R.string.data_logger_characteristic_read_start),
                getActivity().getApplicationContext());
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    protected void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        final BluetoothGattCharacteristic characteristic = gattCharacteristic;
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (mNotifyCharacteristic != null) {
                BluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            mNotifyCharacteristic = characteristic;
            BluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }
        // Adding data to Data Logger
        Logger.datalog(
                getResources().getString(
                        R.string.data_logger_characteristic_notify_start),
                getActivity().getApplicationContext());
    }

    /**
     * Method to get required characteristics from service
     */
    protected void getGattData() {
        List<BluetoothGattCharacteristic> gattCharacteristics = mservice
                .getCharacteristics();
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            String uuidchara = gattCharacteristic.getUuid().toString();
            if (uuidchara.equalsIgnoreCase(GattAttributes.RSC_MEASUREMENT)) {
                mNotifyCharacteristic = gattCharacteristic;
                prepareBroadcastDataNotify(mNotifyCharacteristic);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem graph = menu.findItem(R.id.graph);
        MenuItem log = menu.findItem(R.id.log);
        graph.setVisible(true);
        log.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.graph:
                if (mgraphLayoutParent.getVisibility() != View.VISIBLE) {
                    mgraphLayoutParent.setVisibility(View.VISIBLE);

                } else {
                    mgraphLayoutParent.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
