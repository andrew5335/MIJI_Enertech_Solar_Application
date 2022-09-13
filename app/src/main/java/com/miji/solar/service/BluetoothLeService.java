package com.miji.solar.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.miji.solar.attribute.SampleGattAttributes;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le3.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le3.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le3.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le3.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String EXTRA_DATA = "com.example.bluetooth.le3.EXTRA_DATA";
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_DISCONNECTED = 0;
    /* access modifiers changed from: private */
    public static final String TAG = "miji";
    public static final UUID UUID_FBL770_ADC0_NOTIFY = UUID.fromString(SampleGattAttributes.FBL770_ADC0_NOTIFY);
    public static final UUID UUID_FBL770_ADC1_NOTIFY = UUID.fromString(SampleGattAttributes.FBL770_ADC1_NOTIFY);
    public static final UUID UUID_FBL770_INIT_SETTING = UUID.fromString(SampleGattAttributes.FBL770_INIT_SETTING);
    public static final UUID UUID_FBL770_PIOREAD_NOTIFY = UUID.fromString(SampleGattAttributes.FBL770_PIOREAD_NOTIFY);
    public static final UUID UUID_FBL770_SPP_NOTIFY = UUID.fromString(SampleGattAttributes.FBL770_SPP_NOTIFY);
    public static final UUID UUID_FBL770_SPP_WRITE = UUID.fromString(SampleGattAttributes.FBL770_SPP_WRITE);
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    private final IBinder mBinder = new LocalBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    /* access modifiers changed from: private */
    public BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    /* access modifiers changed from: private */
    public int mConnectionState = 0;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            if (i2 == 2) {
                int unused = BluetoothLeService.this.mConnectionState = 2;
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_CONNECTED);
                Log.i(BluetoothLeService.TAG, "Connected to GATT server.");
                String access$200 = BluetoothLeService.TAG;
                Log.i(access$200, "Attempting to start service discovery:" + BluetoothLeService.this.mBluetoothGatt.discoverServices());
            } else if (i2 == 0) {
                int unused2 = BluetoothLeService.this.mConnectionState = 0;
                Log.i(BluetoothLeService.TAG, "Disconnected from GATT server.");
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_DISCONNECTED);
            }
        }

        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            if (i == 0) {
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
                Log.w(BluetoothLeService.TAG, "Discovered ===========================");
                return;
            }
            String access$200 = BluetoothLeService.TAG;
            Log.w(access$200, "onServicesDiscovered received: " + i);
        }

        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            Log.w(BluetoothLeService.TAG, "Read ===========================");
            if (i == 0) {
                BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, bluetoothGattCharacteristic);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            Log.w(BluetoothLeService.TAG, "onCharacteristicChanged ===========================");
            BluetoothLeService.this.broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE, bluetoothGattCharacteristic);
        }
    };

    /* access modifiers changed from: private */
    public void broadcastUpdate(String str) {
        sendBroadcast(new Intent(str));
    }

    /* access modifiers changed from: private */
    public void broadcastUpdate(String str, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        int i;
        byte[] bArr = new byte[22];
        bArr[0] = 0;
        bArr[1] = 0;
        Intent intent = new Intent(str);
        if (UUID_HEART_RATE_MEASUREMENT.equals(bluetoothGattCharacteristic.getUuid())) {
            if ((bluetoothGattCharacteristic.getProperties() & 1) != 0) {
                i = 18;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                i = 17;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            int intValue = bluetoothGattCharacteristic.getIntValue(i, 1).intValue();
            Log.d(TAG, String.format("Received heart rate: %d", new Object[]{Integer.valueOf(intValue)}));
            intent.putExtra(EXTRA_DATA, String.valueOf(intValue));
        } else if (UUID_FBL770_INIT_SETTING.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.d(TAG, "UUID_FBL770_INIT_SETTING");
            bArr[0] = 85;
            bArr[1] = 51;
            byte[] value = bluetoothGattCharacteristic.getValue();
            if (value != null && value.length > 0) {
                StringBuilder sb = new StringBuilder(value.length);
                int length = value.length;
                int i2 = 2;
                int i3 = 0;
                while (i3 < length) {
                    byte b = value[i3];
                    sb.append(String.format("%02X ", new Object[]{Byte.valueOf(b)}));
                    bArr[i2] = b;
                    i3++;
                    i2++;
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, sb.toString());
            }
        } else if (UUID_FBL770_PIOREAD_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.d(TAG, "UUID_FBL770_PIOREAD_NOTIFY");
            bArr[0] = 85;
            bArr[1] = 0;
            byte[] value2 = bluetoothGattCharacteristic.getValue();
            if (value2 != null && value2.length > 0) {
                StringBuilder sb2 = new StringBuilder(value2.length);
                int length2 = value2.length;
                int i4 = 2;
                int i5 = 0;
                while (i5 < length2) {
                    byte b2 = value2[i5];
                    sb2.append(String.format("%02X ", new Object[]{Byte.valueOf(b2)}));
                    bArr[i4] = b2;
                    i5++;
                    i4++;
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, sb2.toString());
            }
        } else if (UUID_FBL770_ADC0_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.d(TAG, "UUID_FBL770_ADC0_NOTIFY");
            bArr[0] = 85;
            bArr[1] = 1;
            byte[] value3 = bluetoothGattCharacteristic.getValue();
            if (value3 != null && value3.length > 0) {
                StringBuilder sb3 = new StringBuilder(value3.length);
                int length3 = value3.length;
                int i6 = 2;
                int i7 = 0;
                while (i7 < length3) {
                    byte b3 = value3[i7];
                    sb3.append(String.format("%02X ", new Object[]{Byte.valueOf(b3)}));
                    bArr[i6] = b3;
                    i7++;
                    i6++;
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, sb3.toString());
            }
        } else if (UUID_FBL770_ADC1_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.d(TAG, "UUID_FBL770_ADC1_NOTIFY");
            bArr[0] = 85;
            bArr[1] = 2;
            byte[] value4 = bluetoothGattCharacteristic.getValue();
            if (value4 != null && value4.length > 0) {
                StringBuilder sb4 = new StringBuilder(value4.length);
                int length4 = value4.length;
                int i8 = 2;
                int i9 = 0;
                while (i9 < length4) {
                    byte b4 = value4[i9];
                    sb4.append(String.format("%02X ", new Object[]{Byte.valueOf(b4)}));
                    bArr[i8] = b4;
                    i9++;
                    i8++;
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, sb4.toString());
            }
        } else if (UUID_FBL770_SPP_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.d(TAG, "UUID_FBL770_SPP_NOTIFY");
            bArr[0] = 85;
            bArr[1] = 3;
            byte[] value5 = bluetoothGattCharacteristic.getValue();
            if (value5 != null && value5.length > 0) {
                StringBuilder sb5 = new StringBuilder(value5.length);
                int length5 = value5.length;
                int i10 = 2;
                int i11 = 0;
                while (i11 < length5) {
                    byte b5 = value5[i11];
                    sb5.append(String.format("%02X ", new Object[]{Byte.valueOf(b5)}));
                    bArr[i10] = b5;
                    i11++;
                    i10++;
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, sb5.toString());
            }
        } else {
            byte[] value6 = bluetoothGattCharacteristic.getValue();
            if (value6 != null && value6.length > 0) {
                StringBuilder sb6 = new StringBuilder(value6.length);
                int length6 = value6.length;
                for (int i12 = 0; i12 < length6; i12++) {
                    sb6.append(String.format("%02X ", new Object[]{Byte.valueOf(value6[i12])}));
                }
                intent.putExtra("init", bArr);
                intent.putExtra(EXTRA_DATA, new String(value6) + "\n" + sb6.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter != null) {
            return true;
        }
        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String str) throws SecurityException {
        if (this.mBluetoothAdapter == null || str == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        } else if (this.mBluetoothDeviceAddress == null || !str.equals(this.mBluetoothDeviceAddress) || this.mBluetoothGatt == null) {
            BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(str);
            if (remoteDevice == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            this.mBluetoothGatt = remoteDevice.connectGatt(this, false, this.mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");
            this.mBluetoothDeviceAddress = str;
            this.mConnectionState = 1;
            return true;
        } else {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (!this.mBluetoothGatt.connect()) {
                return false;
            }
            this.mConnectionState = 1;
            return true;
        }
    }

    public void disconnect() throws SecurityException {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() throws SecurityException {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) throws SecurityException {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte b) throws SecurityException {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattCharacteristic.setValue(new byte[]{b});
        boolean writeCharacteristic = this.mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        String str = TAG;
        Log.d(str, "writeCharacteristic : " + writeCharacteristic);
    }

    public void writeCharacteristics(BluetoothGattCharacteristic bluetoothGattCharacteristic, String str) throws SecurityException {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattCharacteristic.setValue(str.getBytes());
        boolean writeCharacteristic = this.mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        String str2 = TAG;
        Log.d(str2, "writeCharacteristic : " + writeCharacteristic);
    }

    private byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z) throws SecurityException {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        this.mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, z);
        if (UUID_FBL770_PIOREAD_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.w(TAG, "UUID_FBL770_PIOREAD_NOTIFY ===========");
            BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor);
        }
        if (UUID_FBL770_ADC0_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.w(TAG, "UUID_FBL770_ADC0_NOTIFY ===========");
            BluetoothGattDescriptor descriptor2 = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor2.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor2);
        }
        if (UUID_FBL770_ADC1_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.w(TAG, "UUID_FBL770_ADC1_NOTIFY ===========");
            BluetoothGattDescriptor descriptor3 = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor3.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor3);
        }
        if (UUID_FBL770_SPP_NOTIFY.equals(bluetoothGattCharacteristic.getUuid())) {
            Log.w(TAG, "UUID_FBL770_SPP_NOTIFY ===========");
            BluetoothGattDescriptor descriptor4 = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor4.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor4);
        }
        if (UUID_HEART_RATE_MEASUREMENT.equals(bluetoothGattCharacteristic.getUuid())) {
            BluetoothGattDescriptor descriptor5 = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor5.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor5);
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }


}