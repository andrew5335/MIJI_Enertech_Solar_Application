package com.miji.solar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermissionUtil;
import com.gun0912.tedpermission.normal.TedPermission;
import com.miji.solar.client.BluetoothSerialClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BluetoothSerialClient bluetoothSerialClient;
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private EditText mEditTextInput;
    private TextView mTextView;
    private Button mButtonSend;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private Menu mMenu;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Bluetooth client 초기화
        bluetoothSerialClient = BluetoothSerialClient.getInstance();
        if (bluetoothSerialClient == null) {
            Toast.makeText(getApplicationContext(), "블루투스 사용이 불가능한 기기입니다.", Toast.LENGTH_LONG).show();
        }

        boolean isGranted = TedPermissionUtil.isGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_WIFI_STATE
                , Manifest.permission.CHANGE_WIFI_STATE
                , Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.BLUETOOTH_CONNECT
                , Manifest.permission.BLUETOOTH_SCAN
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.BLUETOOTH_ADVERTISE
                , Manifest.permission.BLUETOOTH);

        if(isGranted) {
            overflowMenuInActionBar();
            initProgressDialog();
            initDeviceListDialog();
            initWidget();
        } else {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT)
                            .show();
                }


            };

            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle("권한 허용 요청")
                    .setRationaleMessage("앱 이용을 위해서는 권한 허용이 필요합니다.")
                    .setDeniedTitle("Permission denied")
                    .setDeniedMessage(
                            "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setGotoSettingButtonText("권한 설정")
                    .setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.ACCESS_WIFI_STATE
                            , Manifest.permission.CHANGE_WIFI_STATE
                            , Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.BLUETOOTH_CONNECT
                            , Manifest.permission.BLUETOOTH_SCAN
                            , Manifest.permission.BLUETOOTH_ADMIN
                            , Manifest.permission.BLUETOOTH_ADVERTISE
                            , Manifest.permission.BLUETOOTH)
                    .check();
        }
    }

    private void overflowMenuInActionBar(){
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // 무시한다. 3.x 이 예외가 발생한다.
            // 또, 타블릿 전용으로 만들어진 3.x 버전의 디바이스는 보통 하드웨어 버튼이 존재하지 않는다.
        }
    }

    @Override
    protected void onPause() {
        bluetoothSerialClient.cancelScan(getApplicationContext());
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!bluetoothSerialClient.isEnabled()) {
            // 블루투스 사용을 활성화 한다.
            // 사용자에게 블루투스 사용을 묻는 시스템 창을 출력하게 된다.
            //블루투스가 사용가능한 상태이면 무시한다.
            bluetoothSerialClient.enableBluetooth(MainActivity.this,
                    new BluetoothSerialClient.OnBluetoothEnabledListener() {
                        @Override
                        public void onBluetoothEnabled(boolean success) {
                            // sucess 가 false 일 경우 사용자가 블루투스 사용하기를 희망하지 않음.
                        }
                    });
        }

    }

    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }

    private void initWidget() {
        mTextView = (TextView) findViewById(R.id.textViewTerminal);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mEditTextInput = (EditText) findViewById(R.id.editTextInput);
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStringData(mEditTextInput.getText().toString());
                mEditTextInput.setText("");
            }
        });
    }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_device);
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void addDeviceToArrayAdapter(BluetoothDevice device) throws SecurityException {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();

    }

    private void enableBluetooth() {
        BluetoothSerialClient btSet =  bluetoothSerialClient;
        btSet.enableBluetooth(this, new BluetoothSerialClient.OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                } else {
                    finish();
                }
            }
        });
    }

    private void addText(String text) {
        mTextView.append(text);
        final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
        if (scrollAmount > 0)
            mTextView.scrollTo(0, scrollAmount);
        else
            mTextView.scrollTo(0, 0);
    }


    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  bluetoothSerialClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = bluetoothSerialClient;
        btSet.scanDevices(getApplicationContext(), new BluetoothSerialClient.OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setMessage("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(true);
                mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = bluetoothSerialClient;
                        btSet.cancelScan(getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) throws SecurityException {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setMessage(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }


    private void connect(BluetoothDevice device) {
        mLoadingDialog.setMessage("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  bluetoothSerialClient;
        btSet.connect(getApplicationContext(), device, mBTHandler);
    }

    private BluetoothSerialClient.BluetoothStreamingHandler mBTHandler = new BluetoothSerialClient.BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            addText("Messgae : Connection error - " +  e.toString() + "\n");
            mMenu.getItem(0).setTitle(R.string.action_connect);
        }

        @Override
        public void onDisconnected() {
            mMenu.getItem(0).setTitle(R.string.action_connect);
            mLoadingDialog.cancel();
            addText("Messgae : Disconnected.\n");
        }
        @Override
        public void onData(byte[] buffer, int length) {
            if(length == 0) return;
            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '\0') {
                try {
                    addText(bluetoothSerialClient.getConnectedDevice().getName() + " : " +
                            new String(mmByteBuffer.array(), 0, mmByteBuffer.position()) + '\n');
                    mmByteBuffer.clear();
                } catch(SecurityException se) {
                    se.printStackTrace();
                }
            }
        }

        @Override
        public void onConnected() {
            try {
                addText("Messgae : Connected. " + bluetoothSerialClient.getConnectedDevice().getName() + "\n");
                mLoadingDialog.cancel();
                mMenu.getItem(0).setTitle(R.string.action_disconnect);
            } catch(SecurityException se) {
                se.printStackTrace();
            }
        }
    };

    public void sendStringData(String data) {
        data += '\0';
        byte[] buffer = data.getBytes();
        if(mBTHandler.write(buffer)) {
            addText("Me : " + data + '\n');
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        bluetoothSerialClient.claer();
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean connect = bluetoothSerialClient.isConnection();
        if(item.getItemId() == R.id.action_connect) {
            if (!connect) {
                mDeviceListDialog.show();
            } else {
                mBTHandler.close();
            }
            return true;
        } else {
            showCodeDlg();
            return true;
        }
    }

    private void showCodeDlg() {
        TextView codeView = new TextView(this);
        codeView.setText(Html.fromHtml(readCode()));
        codeView.setMovementMethod(new ScrollingMovementMethod());
        codeView.setBackgroundColor(Color.parseColor("#202020"));
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_DialogWhenLarge)
                .setView(codeView)
                .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private String readCode() {
        try {
            InputStream is = getAssets().open("HC_06_Echo.txt");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();
            String code = new String(buffer);
            buffer = null;
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}