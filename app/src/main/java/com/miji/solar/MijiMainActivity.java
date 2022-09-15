package com.miji.solar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.ficat.easyble.BleDevice;
import com.ficat.easyble.BleManager;
import com.ficat.easyble.scan.BleScanCallback;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.IBinder;

import com.miji.solar.fragment.TabFragment1;
import com.miji.solar.fragment.TabFragment2;
import com.miji.solar.fragment.TabFragment3;
import com.miji.solar.fragment.TabFragment4;
import com.miji.solar.ui.main.SectionsPagerAdapter;
import com.miji.solar.databinding.ActivityMijiMainBinding;
import com.miji.solar.service.BluetoothLeService;
import com.miji.solar.attribute.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MijiMainActivity extends AppCompatActivity {

    private ActivityMijiMainBinding binding;
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private ListView listView;
    private AlertDialog.Builder builder;

    private BleManager bleManager;
    private BleDevice mDevice;
    private List<BleDevice> deviceList = new ArrayList<>();

    private BluetoothLeService mBluetoothLeService;
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    public String TAG = "miji";
    public boolean mConnected = false;

    String ret$1, ret$2, ret$3,ret$4, ret$5;
    String ret1, ret2, ret3, ret4, ret5;
    String reta1, reta2, reta3, reta4, reta5;
    String ddd = "";
    byte[] sendByte = new byte[20];
    char[] send_arr = {'\\', '0', '/', '0', '/', '0', '/', '0', '/', '0', '/', '0', 13, 10};
    String[] send_arr2 = {"\\", "0", "/", "0", "/", "0", "/", "0", "/", "0", "/", "0"};
    String[] send_arr2p = {"\\", "0", "/", "1", "\r\n"};

    // 연동 명령어 세팅
    String sendRefresh = "@";    // 블루투스 연결된 경우 데이터를 가져오기 위한 명령어
    String sendRequest = "\\";
    String sendOn = "$";
    String sendOff = "#";

    private TabFragment1 frag1;
    private TabFragment2 frag2;
    private TabFragment3 frag3;
    private TabFragment4 frag4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        frag1 = new TabFragment1();
        frag2 = new TabFragment2();
        frag3 = new TabFragment3();
        frag4 = new TabFragment4();

        // 블루투스 지원 여부 확인
        if(!BleManager.supportBle(this)) {
            Toast.makeText(this, "이 기기는 블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        // 블루투스가 활성화 되어 있지 않을 경우 블루투스 활성화
        if(!BleManager.isBluetoothOn()) {
            BleManager.toggleBluetooth(true);
        }

        // 블루투스 스캔 옵션 설정
        BleManager.ScanOptions scanOptions = BleManager.ScanOptions
                .newInstance()
                .scanPeriod(5000)
                .scanDeviceName(null);

        // 블루투스 연결 옵션 설정
        BleManager.ConnectOptions connectOptions = BleManager.ConnectOptions
                .newInstance()
                .connectTimeout(12000);

        // 블루투스 매니저 초기화
        bleManager = BleManager.getInstance()
                .setScanOptions(scanOptions)
                .setConnectionOptions(connectOptions)
                .setLog(true, TAG)
                .init(this.getApplication());

        initProgressDialog();
        initDeviceListDialog();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.custom));
        }

        binding = ActivityMijiMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        ImageView bluetooth = binding.bluetooth;

        mDeviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanDevices();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BleManager.getInstance().stopScan();
        unregisterReceiver(this.mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BleManager.toggleBluetooth(true);
        registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAll();
        BleManager.getInstance().destroy();
    }

    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }

    private void initDeviceListDialog() {
        listView = new ListView(getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);

                for(BleDevice device : deviceList) {
                    if(item.contains(device.address)) {
                        mBluetoothLeService.connect(device.address);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select bluetooth device");
        builder.setView(listView);
        builder.setPositiveButton("Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanDevices();
                    }
                });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BleDevice device) throws SecurityException {
        if(deviceList.contains(device)) {
            deviceList.remove(device);
            mDeviceArrayAdapter.remove(device.name + "\n" + device.address);
        }
        deviceList.add(device);
        mDeviceArrayAdapter.add(device.name + "\n" + device.address);
        mDeviceArrayAdapter.notifyDataSetChanged();

    }

    private void scanDevices() {
        if(!BleManager.isBluetoothOn()) {
            BleManager.toggleBluetooth(true);
        }

        bleManager.startScan(new BleScanCallback() {
            String message = "";
            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                for(BleDevice d : deviceList) {
                    if(device.address.equals(d.address)) {
                        return;
                    }
                }
                Log.e("miji", device.name + "/" + device.address);

                message += "\n" + device.name + "\n" + device.address;
                mLoadingDialog.setMessage(message);
                addDeviceToArrayAdapter(device);
            }

            @Override
            public void onStart(boolean startScanSuccess, String info) {
                Log.e("miji", "scan start");
                mLoadingDialog.show();
                message = "Start Scan";
                mLoadingDialog.setMessage(message);
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(true);
                if(startScanSuccess) {
                    deviceList.clear();
                    mDeviceArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                Log.e("miji", deviceList.toString());
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                initDeviceListDialog();
                mDeviceListDialog.show();
            }
        });
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(DEVICE_UUID);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public void sendData(String[] strArr) {
        String convertObjectArrayToString = convertObjectArrayToString(strArr, "");
        String str = TAG;
        Log.d(str, "senddata  " + convertObjectArrayToString + "");
        if (this.mGattCharacteristics != null) {
            try {
                BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
                bluetoothLeService.writeCharacteristics((BluetoothGattCharacteristic) this.mGattCharacteristics.get(3).get(0), convertObjectArrayToString + "\r\n");
            } catch (Exception e) {
                String str2 = TAG;
                Log.d(str2, e + "");
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendData2(String str) {
        String str2 = TAG;
        Log.d(str2, "senddata2  " + str + "");
        Bundle bundle = new Bundle(1);
        bundle.putString("test", "$1/1/1/2831/97");
        frag1.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frag1, frag1).commit();
        if (this.mGattCharacteristics != null) {
            try {
                BluetoothLeService bluetoothLeService = this.mBluetoothLeService;
                bluetoothLeService.writeCharacteristics((BluetoothGattCharacteristic) this.mGattCharacteristics.get(3).get(0), str + "\r\n");
            } catch (Exception e) {
                String str3 = TAG;
                Log.d(str3, e + "");
            }
        }
    }

    private static String convertObjectArrayToString(Object[] objArr, String str) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objArr) {
            sb.append(obj.toString());
            sb.append(str);
        }
        return sb.substring(0, sb.length() - 1);
    }

    public void updateCommandState(String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (mConnected) {
                        //mImageBT.setImageResource(C0284R.mipmap.bts_on);
                    } else {
                        //mImageBT.setImageResource(C0284R.mipmap.bts_off);
                    }
                } catch (Exception e) {
                    String access$800 = TAG;
                    Log.d(access$800, "eee:" + e);
                }
                Log.d(TAG, "ggggggggg");
            }
        });
    }

    public void displayGattServices(List<BluetoothGattService> list) {
        if (list != null) {
            String string = getResources().getString(R.string.unknown_service);
            String string2 = getResources().getString(R.string.unknown_characteristic);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            this.mGattCharacteristics = new ArrayList<>();
            for (BluetoothGattService next : list) {
                HashMap hashMap = new HashMap();
                String uuid = next.getUuid().toString();
                String str = TAG;
                Log.d(str, "service uuid : " + uuid);
                hashMap.put("NAME", SampleGattAttributes.lookup(uuid, string));
                hashMap.put("UUID", uuid);
                arrayList.add(hashMap);
                ArrayList arrayList3 = new ArrayList();
                List<BluetoothGattCharacteristic> characteristics = next.getCharacteristics();
                ArrayList arrayList4 = new ArrayList();
                for (BluetoothGattCharacteristic next2 : characteristics) {
                    arrayList4.add(next2);
                    HashMap hashMap2 = new HashMap();
                    String uuid2 = next2.getUuid().toString();
                    String str2 = TAG;
                    Log.d(str2, "gattCharacteristic uuid : " + uuid2);
                    hashMap2.put("NAME", SampleGattAttributes.lookup(uuid2, string2));
                    hashMap2.put("UUID", uuid2);
                    arrayList3.add(hashMap2);
                }
                this.mGattCharacteristics.add(arrayList4);
                arrayList2.add(arrayList3);
            }
            Log.d(TAG, "service read ok ");
        }
    }

    public static String unHex(String str) {
        String str2 = "";
        for (int i = 0; i < str.length(); i += 3) {
            int parseInt = Integer.parseInt(str.substring(i, i + 2), 16);
            str2 = str2 + ((char) parseInt);
        }
        return str2;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str;
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                boolean unused = mConnected = true;
                invalidateOptionsMenu();
                updateCommandState("");
                sendData2(sendRefresh);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                boolean unused2 = mConnected = false;
                invalidateOptionsMenu();
                updateCommandState("");
                sendData2(sendRefresh);

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                Log.d(TAG, "======= Init Setting Data ");
                updateCommandState("Init Data");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try {
                            mBluetoothLeService.setCharacteristicNotification((BluetoothGattCharacteristic) ((ArrayList) mGattCharacteristics.get(3)).get(1), true);
                        } catch (Exception e) {
                            String access$800 = TAG;
                            Log.d(access$800, e + "");
                        }
                    }
                }, 1000);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] byteArrayExtra = intent.getByteArrayExtra("init");
                if (byteArrayExtra[0] == 85 && byteArrayExtra[1] == 51) {
                    Log.d(TAG, "======= Init Setting Data ");
                    updateCommandState("Init Data");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mBluetoothLeService.setCharacteristicNotification((BluetoothGattCharacteristic) ((ArrayList) mGattCharacteristics.get(3)).get(1), true);
                        }
                    }, 1000);
                }
                if (byteArrayExtra[0] == 85 && byteArrayExtra[1] == 3) {
                    Log.d(TAG, "======= SPP READ NOTIFY ");
                    updateCommandState("SPP READ");
                    String str2 = "" + unHex(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    Toast.makeText(getApplicationContext(), "readdata1 : " + str2, Toast.LENGTH_LONG).show();
                    if (str2.charAt(1) == '&') {
                        str = str2.replaceAll(System.getProperty("line.separator"), "");
                        Toast.makeText(getApplicationContext(), "readdata2 : " + str, Toast.LENGTH_LONG).show();
                        String[] split = str.split("/");
                        int i = 0;
                        while (split.length > 0) {
                            try {
                                StringBuilder sb = new StringBuilder();
                                //MainActivity mainActivity = MainActivity.this;
                                sb.append(ddd);
                                sb.append(split[i]);
                                ddd = sb.toString();
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(ddd);
                                sb2.append("\r\n");
                                ddd = sb2.toString();
                                i++;
                            } catch (Exception unused3) {
                            }
                        }
                        //data_view.setText(ddd);
                        frag4.setChart(ddd);
                    } else {
                        str = str2.replaceAll(System.getProperty("line.separator"), "");
                        Toast.makeText(getApplicationContext(), "readdata3 : " + str, Toast.LENGTH_LONG).show();
                    }

                    if(null != str && !"".equals(str) && 0 < str.length()) {
                        // 인입된 데이터가 있을 경우 화면 처리를 위해 Fragment로 전달
                        frag1.changeStatus(str);
                        frag2.onCheckChange(str);
                    }

                    if (str.startsWith("$")) {
                        Toast.makeText(getApplicationContext(), "readdata4 : " + str, Toast.LENGTH_LONG).show();
                        String[] split2 = str.split("/");
                        try {
                            ret$1 = split2[0];
                            ret$2 = split2[1];
                            ret$3 = split2[2];
                            ret$4 = split2[3];
                            ret$5 = split2[4];
                        } catch (Exception e) {
                            Log.d(TAG, "e " + e);
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    ret$1 = ret$1.trim();
                                    String access$800 = TAG;
                                    Log.d(access$800, "1" + ret$1);
                                    if (ret$1.equals("$0")) {
                                        //im01.setImageResource(C0284R.mipmap.f40m1);
                                        //im02.setImageResource(C0284R.mipmap.sm2);
                                    } else if (ret$1.equals("$1")) {
                                        //im01.setImageResource(C0284R.mipmap.f41m2);
                                        //im02.setImageResource(C0284R.mipmap.sm2);
                                    } else if (ret$1.equals("$2")) {
                                        //im01.setImageResource(C0284R.mipmap.f42m3);
                                        //im02.setImageResource(C0284R.mipmap.sm2);
                                    } else {
                                        //im02.setImageResource(C0284R.mipmap.sm0);
                                    }
                                    ret$2 = ret$2.trim();
                                    String access$8002 = TAG;
                                    Log.d(access$8002, "2" + ret$2);
                                    if (ret$2.equals("0")) {
                                        //im03.setImageResource(C0284R.mipmap.sm22);
                                    } else if (ret$2.equals("1")) {
                                        //im03.setImageResource(C0284R.mipmap.sm0);
                                    }
                                    ret$3 = ret$3.trim();
                                    String access$8003 = TAG;
                                    Log.d(access$8003, "3" + ret$3);
                                    if (ret$3.equals("0")) {
                                        //im04.setImageResource(C0284R.mipmap.sm0);
                                        //im05.setImageResource(C0284R.mipmap.sm0);
                                    } else if (ret$3.equals("1")) {
                                        //im04.setImageResource(C0284R.mipmap.sm3);
                                        //im05.setImageResource(C0284R.mipmap.sm0);
                                    } else if (ret$3.equals("2")) {
                                        //im04.setImageResource(C0284R.mipmap.sm0);
                                        //im05.setImageResource(C0284R.mipmap.sm3);
                                    }
                                    if (ret$4.length() == 4) {
                                        //TextView textView = bvolt;
                                        //textView.setText(ret$4.substring(0, 2) + "." + ret$4.substring(2, ret$4.length()));
                                    } else if (ret$4.length() == 3) {
                                        //TextView textView2 = bvolt;
                                        //textView2.setText(ret$4.substring(0, 1) + "." + ret$4.substring(1, ret$4.length()));
                                    } else if (ret$4.length() == 2) {
                                        //TextView textView3 = bvolt;
                                        //textView3.setText("00." + ret$4);
                                    } else if (ret$4.length() == 1) {
                                        //TextView textView4 = bvolt;
                                        //textView4.setText("00.0" + ret$4);
                                    }
                                    //bwatt.setText(ret$5);
                                } catch (Exception unused) {
                                }
                            }
                        });
                        Log.d(TAG, "$ " + ret$1 + "   " + ret$2 + "   " + ret$3 + "   " + ret$4 + "   " + ret$5);
                    }
                    if (str.startsWith("@")) {
                        String[] split3 = str.split("/");
                        try {
                            reta1 = split3[0];
                            reta2 = split3[1];
                            reta3 = split3[2];
                        } catch (Exception e2) {
                            Log.d(TAG, "e " + e2);
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    reta1 = reta1.replaceAll("@", "");
                                    if (reta1.length() == 4) {
                                        //TextView textView = volt;
                                        //textView.setText(reta1.substring(0, 2) + "." + reta1.substring(2, reta1.length()));
                                    } else if (reta1.length() == 3) {
                                        //TextView textView2 = volt;
                                        //textView2.setText(reta1.substring(0, 1) + "." + reta1.substring(1, reta1.length()));
                                    } else if (reta1.length() == 2) {
                                        //TextView textView3 = volt;
                                        //textView3.setText("00." + reta1);
                                    } else if (reta1.length() == 1) {
                                        //TextView textView4 = volt;
                                        //textView4.setText("00.0" + reta1);
                                    }
                                    String access$800 = TAG;
                                    Log.d(access$800, "" + reta2.length());
                                    if (reta2.length() == 4) {
                                        //TextView textView5 = ampere;
                                        //textView5.setText(reta2.substring(0, 2) + "." + reta2.substring(2, reta2.length()));
                                    } else if (reta2.length() == 3) {
                                        //TextView textView6 = ampere;
                                        //textView6.setText(reta2.substring(0, 1) + "." + reta2.substring(1, reta2.length()));
                                    } else if (reta2.length() == 2) {
                                        //TextView textView7 = ampere;
                                        //textView7.setText("00." + reta2);
                                    } else if (reta2.length() == 1) {
                                        //TextView textView8 = ampere;
                                        //textView8.setText("00.0" + reta2);
                                    }
                                    if (reta3.length() == 5) {
                                        //TextView textView9 = watt;
                                        //textView9.setText(reta3.substring(0, 3) + "." + reta3.substring(3, reta3.length()));
                                    } else if (reta3.length() == 4) {
                                        if (reta2.length() == 4) {
                                            //TextView textView10 = watt;
                                            //textView10.setText(reta3.substring(0, 3) + "." + reta3.substring(3, reta3.length()));
                                            return;
                                        }
                                        //TextView textView11 = watt;
                                        //textView11.setText(reta3.substring(0, 2) + "." + reta3.substring(2, reta3.length()));
                                    } else if (reta3.length() == 3) {
                                        if (reta2.length() == 4) {
                                            //TextView textView12 = watt;
                                            //textView12.setText(reta3.substring(0, 2) + "." + reta3.substring(2, reta3.length()));
                                            return;
                                        }
                                        //TextView textView13 = watt;
                                        //textView13.setText(reta3.substring(0, 1) + "." + reta3.substring(1, reta3.length()));
                                    } else if (reta3.length() == 2) {
                                        //TextView textView14 = watt;
                                        //textView14.setText("00." + reta3);
                                    } else if (reta3.length() == 1) {
                                        //TextView textView15 = watt;
                                        //textView15.setText("00.0" + reta3);
                                    }
                                } catch (Exception unused) {
                                }
                            }
                        });
                        Log.d(TAG, " @  " + reta1 + "   " + reta2 + "   " + reta3);
                    }
                    if (str.startsWith("#")) {
                        String[] split4 = str.split("/");
                        try {
                            ret1 = split4[0];
                            ret2 = split4[1];
                            ret3 = split4[2];
                            ret4 = split4[3];
                            ret5 = split4[4];
                        } catch (Exception unused4) {
                        }
                        Log.d(TAG, " #  " + ret1 + "   " + ret2 + "   " + ret3 + "   " + ret4 + "   " + ret5);
                    }
                }
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
}