package si.gounitis.trimixmix;

import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;

import si.gounitis.trimixmix.model.SensorData;
import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.model.Voltages;
import si.gounitis.trimixmix.util.UsbUtils;
import si.gounitis.trimixmix.util.Utils;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    TabLayout tabLayout;
    TabItem tabCalibrate;
    TabItem tabMix;
    TabItem tabHelp;

    PageAdapter pageAdapter;

    // arduiono USB
    private UsbManager usbManager;
    private String readData;

    //
    private TrimixData trimixData = new TrimixData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // arduino USB connection
        usbManager = getSystemService(UsbManager.class);

        // Detach events are sent as a system-wide broadcast
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(UsbUtils.usbDetachedReceiver, filter);

        // GUI
        tabLayout = findViewById(R.id.tablayout);
        tabCalibrate = findViewById(R.id.tabCalibrate);
        tabMix = findViewById(R.id.tabMix);
        tabHelp = findViewById(R.id.tabHelp);
        viewPager = findViewById(R.id.viewPager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                        if (tab.getPosition() == 1) {
                            tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                    R.color.crimson));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                                        R.color.crimson));
                            }
                        } else if (tab.getPosition() == 2) {
                            tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                    R.color.green));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                                        R.color.green));
                            }
                        } else {
                            tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                                    R.color.steelblue));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,
                                        R.color.steelblue));
                            }
                            attachCalibrateHandlers(); // todo tole daj nekam drugam, če ne moraš tja in nazaj, da se pokliče
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        // start updateOutFieldsTask thread
        final Handler updateHandler = new Handler();
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateOutputFields();
                updateHandler.postDelayed(this, 300);
            }
        },300);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UsbUtils.startUsbConnection(usbManager, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(UsbUtils.usbDetachedReceiver);
        UsbUtils.stopUsbConnection();
    }

    // GUI helpers
    private void updateOutputFields() {
        Button redCalibrateButton = findViewById(R.id.redCalibrateButton);
        TextView redSensorDisplay = findViewById(R.id.redSensorDisplay);
        Button greenCalibrateButton = findViewById(R.id.greenCalibrateButton);
        TextView greenSensorDisplay = findViewById(R.id.greenSensorDisplay);
        TextView trimixDisplay = findViewById(R.id.trimixDisplay);

        getMeasurenments();

        SensorData sensorData;
        sensorData = trimixData.getRedSensor();
        if (redCalibrateButton!=null) redCalibrateButton.setText("Calibrate (" + String.format("%.1f",(sensorData.getSensorVoltage()-sensorData.getSensorOffset())) + "mV)");
        if (redSensorDisplay!=null && sensorData.isCalibrated() ) redSensorDisplay.setText(""+String.format("%.1f",sensorData.getFractionOxygen()) + "%");
        sensorData = trimixData.getGreenSensor();
        if (greenCalibrateButton!=null) greenCalibrateButton.setText("Calibrate (" + String.format("%.1f",(sensorData.getSensorVoltage()-sensorData.getSensorOffset())) + "mV)");
        if (greenSensorDisplay!=null && sensorData.isCalibrated() ) greenSensorDisplay.setText("" + String.format("%.1f",sensorData.getFractionOxygen()) + "%");

        if (trimixData.getRedSensor().isCalibrated() &&  trimixData.getGreenSensor().isCalibrated() && trimixData.isCalculated()) {
            if (trimixDisplay!=null) trimixDisplay.setText("Tx" + String.format("%.1f", trimixData.getFractionOxygen()) + "/" + String.format("%.1f", trimixData.getFractionHelium()));
        } else {
            if (trimixDisplay!=null) trimixDisplay.setText("--------");
        }
    }

    private void attachCalibrateHandlers() {
        final Button redCalibrateButton = findViewById(R.id.redCalibrateButton);
        redCalibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.calibrate(trimixData.getRedSensor());
            }
        });
        final Button greenCalibrateButton = findViewById(R.id.greenCalibrateButton);
        greenCalibrateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.calibrate(trimixData.getGreenSensor());
            }
        });
    }

    // calculations
    private void getMeasurenments() {
        Voltages voltages = Utils.toJson(readData);
        if (voltages==null) return;

        SensorData sensorData;
        sensorData = trimixData.getRedSensor();
        sensorData.setSensorVoltage(voltages.getAds0mv());
        Utils.calculateOxygen(sensorData);
        sensorData = trimixData.getGreenSensor();
        sensorData.setSensorVoltage(voltages.getAds1mv());
        Utils.calculateOxygen(sensorData);

        Utils.calculateTrimix(trimixData);
    }

    // USB
    private static String buffer = "";
    private UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data) {
            try {
                String dataUtf8 = new String(data, "UTF-8");
                buffer += dataUtf8;
                int index;
                while ((index = buffer.indexOf('\n')) != -1) {
                    final String dataStr = buffer.substring(0, index + 1).trim();
                    buffer = buffer.length() == index ? "" : buffer.substring(index + 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            readData=dataStr;
                        }
                    });
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
    };
}
