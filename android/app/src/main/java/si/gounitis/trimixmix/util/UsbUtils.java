package si.gounitis.trimixmix.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class UsbUtils {
    private static final int USB_VENDOR_ID = 0x1A86;
    private static UsbDeviceConnection connection;
    private static UsbSerialDevice serialDevice;

    public static void startUsbConnection(UsbManager usbManager, UsbSerialInterface.UsbReadCallback callback) {
        Map<String, UsbDevice> connectedDevices = usbManager.getDeviceList();

        if (!connectedDevices.isEmpty()) {
            for (UsbDevice device : connectedDevices.values()) {
                if (device.getVendorId() == USB_VENDOR_ID ) {
                    startSerialConnection(usbManager, device, callback);
                    return;
                }
            }
        }
    }

    public static final BroadcastReceiver usbDetachedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && device.getVendorId() == USB_VENDOR_ID) {
                    stopUsbConnection();
                }
            }
        }
    };

    private static void startSerialConnection(UsbManager usbManager, UsbDevice device, UsbSerialInterface.UsbReadCallback callback) {
        connection = usbManager.openDevice(device);
        serialDevice = UsbSerialDevice.createUsbSerialDevice(device, connection);
        if (serialDevice != null) {
            if (serialDevice.open()) {
                serialDevice.setBaudRate(9600);
                serialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                serialDevice.read(callback);
            } else {
            }
        } else {
        }
    }

    public static void stopUsbConnection() {
        try {
            if (serialDevice != null) {
                serialDevice.close();
            }

            if (connection != null) {
                connection.close();
            }
        } finally {
            serialDevice = null;
            connection = null;
        }
    }
}
