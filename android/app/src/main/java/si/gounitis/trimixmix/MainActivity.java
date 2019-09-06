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
import android.widget.EditText;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Date;

import si.gounitis.trimixmix.model.ReadData;
import si.gounitis.trimixmix.model.SensorData;
import si.gounitis.trimixmix.model.SensorStatus;
import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.model.Voltages;
import si.gounitis.trimixmix.util.UsbUtils;
import si.gounitis.trimixmix.util.Utils;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabItem tabCalibrate;
    private TabItem tabMix;
    private TabItem tabHelp;
    private PageAdapter pageAdapter;

    // arduiono USB
    private UsbManager usbManager;
    private static final ReadData readData = new ReadData();
    private static final TrimixData trimixData = new TrimixData();

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
                EditText desiredTxOxygen = findViewById(R.id.desiredTxOxygen);
                EditText desiredTxHelium = findViewById(R.id.desiredTxHelium);

                attachCalibrateHandlers(); // todo - find better place

                trimixData.setDesiredFractionOxygen(getDesiredValue(desiredTxOxygen));
                trimixData.setDesiredFractionHelium(getDesiredValue(desiredTxHelium));

                updateOutputFields();

                updateHandler.postDelayed(this, 300);
            }
        },300);
    }

    private float getDesiredValue(EditText text) {
        if (text==null)
            return 0f;

        float rv;
        try {
            String fValue = text.getText().toString();
            if (fValue.equals(""))
                return 0;
            rv = Float.parseFloat(fValue);
            if (rv<0f || rv >90f) {
                rv = 0;
                String rvText = NumberFormat.getInstance(MainActivity.this.getResources().getConfiguration().locale).format(rv);
                text.setText(rvText);
            }
        } catch (Exception e) {
            rv = 0;
            // https://stackoverflow.com/questions/23791303/how-to-call-settext-using-a-float
            String rvText = NumberFormat.getInstance(MainActivity.this.getResources().getConfiguration().locale).format(rv);
            text.setText(rvText);
        }

        return rv;
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
        Button greenCalibrateButton = findViewById(R.id.greenCalibrateButton);
        TextView trimixDisplay = findViewById(R.id.trimixDisplay);
        TextView firstSensorDisplay = findViewById(R.id.firstSensorDisplay);
        TextView secondSensorDisplay = findViewById(R.id.secondSensorDisplay);

        try {
            getMeasurenments();

            SensorData sensorData = trimixData.getRedSensor();
            redCalibrateButton.setText("Calibrate (" + String.format("%.1f", (sensorData.getSensorVoltage() - sensorData.getSensorOffset())) + "mV)");
            setQxygenDisplay(trimixData.getRedSensor(), findViewById(R.id.redSensorDisplay));

            sensorData = trimixData.getGreenSensor();
            greenCalibrateButton.setText("Calibrate (" + String.format("%.1f", (sensorData.getSensorVoltage() - sensorData.getSensorOffset())) + "mV)");
            setQxygenDisplay(trimixData.getGreenSensor(), findViewById(R.id.greenSensorDisplay));

            if (trimixData.getRedSensor().getStatus().equals(SensorStatus.CALIBRATED) && trimixData.getGreenSensor().getStatus().equals(SensorStatus.CALIBRATED) && trimixData.isCalculated()) {
                trimixDisplay.setText("Tx" + String.format("%.1f", trimixData.getFractionOxygen()) + "/" + String.format("%.1f", trimixData.getFractionHelium()));
            } else {
                trimixDisplay.setText("--------");
            }

            if (trimixData.getRedSensor().getStatus().equals(SensorStatus.CALIBRATED) && trimixData.getGreenSensor().getStatus().equals(SensorStatus.CALIBRATED) && trimixData.isCalculated()) {
                float afterOxygenData = Utils.calculateAfterOxygenSensor(trimixData);
                float afterHeliumData =  Utils.calculateAfterHeliumSensor(trimixData);
                if (dataOK(afterOxygenData, afterHeliumData )) {
                    firstSensorDisplay.setText(String.format("%.1f", afterOxygenData));
                    secondSensorDisplay.setText(String.format("%.1f", afterHeliumData));
                } else {
                    firstSensorDisplay.setText("--------");
                    secondSensorDisplay.setText("--------");
                }
            } else {
                firstSensorDisplay.setText("--------");
                secondSensorDisplay.setText("--------");
            }

        } catch (Exception e) {
        }
    }

    private void setQxygenDisplay(SensorData sensorData, TextView sensorDisplay) {
        if (sensorData.getStatus().equals(SensorStatus.NOT_CALIBRATED)) {
            sensorDisplay.setText("NotCal");
        } else if (sensorData.getStatus().equals(SensorStatus.CALIBRATING)) {
            sensorDisplay.setText("Cal");
        } else if (sensorData.getStatus().equals(SensorStatus.VOLTAGE_LOW)) {
            sensorDisplay.setText("LowV");
        } else if (sensorData.getStatus().equals(SensorStatus.VOLTAGE_HIGH)) {
            sensorDisplay.setText("HiV");
        } else if (sensorData.getStatus().equals(SensorStatus.DISCONECTED)) {
            sensorDisplay.setText("Disc");
        } else if (sensorData.getStatus().equals(SensorStatus.CALIBRATED)) {
            sensorDisplay.setText("" + String.format("%.1f", sensorData.getFractionOxygen()) + "%");
        } else {
            sensorDisplay.setText("Unknown");
        }
    }

    private boolean dataOK(float afterOxygenData, float afterHeliumData) {
        return afterOxygenData<90f && afterOxygenData>0f & afterHeliumData <40f & afterHeliumData>0f;
    }

    private void attachCalibrateHandlers() {
        try {
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
        } catch (Exception e) {
        }
    }

    // calculations
    private static boolean restoreStatus = false;
    private void getMeasurenments() {
        if (restoreStatus) {
            trimixData.getRedSensor().setStatus(SensorStatus.NOT_CALIBRATED);
            trimixData.getGreenSensor().setStatus(SensorStatus.NOT_CALIBRATED);
        }
        Date fiveSecondsAgo = new Date(System.currentTimeMillis() - (5 * 1000));
        // readData.setTimestamp(new Date(System.currentTimeMillis())); // todo uncoment for test
        // check if sensor readings are not older than 5 seconds
        if (readData.getTimestamp()!=null && fiveSecondsAgo.before(readData.getTimestamp())) {
            Voltages voltages = Utils.toJson(readData.getValue());
            if (voltages==null)
                return;

            restoreStatus = false;
            SensorData sensorData;
            sensorData = trimixData.getRedSensor();
            sensorData.setSensorVoltage(voltages.getAds0mv());
            Utils.calculateOxygen(sensorData);
            sensorData = trimixData.getGreenSensor();
            sensorData.setSensorVoltage(voltages.getAds1mv());
            Utils.calculateOxygen(sensorData);

            Utils.calculateTrimix(trimixData);
        } else {
            restoreStatus = true;
            trimixData.getRedSensor().setStatus(SensorStatus.DISCONECTED);
            trimixData.getRedSensor().setSensorVoltage(0);
            trimixData.getRedSensor().setFractionOxygen(0);
            trimixData.getGreenSensor().setStatus(SensorStatus.DISCONECTED);
            trimixData.getGreenSensor().setSensorVoltage(0);
            trimixData.getGreenSensor().setFractionOxygen(0);
            trimixData.setCalculated(false);
        }
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
                            readData.setValue(dataStr);
                            readData.setTimestamp(new Date(System.currentTimeMillis()));
                        }
                    });
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
    };
}
