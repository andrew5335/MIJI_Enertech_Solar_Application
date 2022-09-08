package com.miji.solar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miji.solar.client.BluetoothSerialClient;
import com.miji.solar.ui.main.SectionsPagerAdapter;
import com.miji.solar.databinding.ActivityMijiMainBinding;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Set;

public class MijiMainActivity extends AppCompatActivity {

    private ActivityMijiMainBinding binding;

    private BluetoothSerialClient bluetoothSerialClient;
    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<BluetoothDevice>();
    private ArrayAdapter<String> mDeviceArrayAdapter;

    private EditText mEditTextInput;
    private TextView mTextView;
    private Button mButtonSend;
    private ProgressDialog mLoadingDialog;
    private AlertDialog mDeviceListDialog;
    private Menu mMenu;
    private ListView listView;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        bluetoothSerialClient = BluetoothSerialClient.getInstance();

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
        //FloatingActionButton fab = binding.fab;
        /**
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        **/
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initDeviceListDialog();
                scanDevices();
            }
        });
    }

    @Override
    protected void onPause() {
        bluetoothSerialClient.cancelScan(getApplicationContext());
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableBluetooth();
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

    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  bluetoothSerialClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
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
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
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
        //mDeviceListDialog.show();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
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
            Toast.makeText(getApplicationContext(), "connection error", Toast.LENGTH_LONG).show();
            mLoadingDialog.cancel();
            //addText("Messgae : Connection error - " +  e.toString() + "\n");
            //mMenu.getItem(0).setTitle(R.string.action_connect);
        }

        @Override
        public void onDisconnected() {
            //mMenu.getItem(0).setTitle(R.string.action_connect);
            Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
            mLoadingDialog.cancel();
            //addText("Messgae : Disconnected.\n");
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
                    //addText(bluetoothSerialClient.getConnectedDevice().getName() + " : " +
                    //        new String(mmByteBuffer.array(), 0, mmByteBuffer.position()) + '\n');
                    Toast.makeText(getApplicationContext(), bluetoothSerialClient.getConnectedDevice().getName() + " : " +
                            new String(mmByteBuffer.array(), 0, mmByteBuffer.position()) + '\n', Toast.LENGTH_LONG).show();
                    mmByteBuffer.clear();
                } catch(SecurityException se) {
                    se.printStackTrace();
                }
            }
        }

        @Override
        public void onConnected() {
            try {
                //addText("Messgae : Connected. " + bluetoothSerialClient.getConnectedDevice().getName() + "\n");
                Toast.makeText(getApplicationContext(), "connected : " + bluetoothSerialClient.getConnectedDevice().getName(), Toast.LENGTH_LONG).show();
                mLoadingDialog.cancel();
                //mMenu.getItem(0).setTitle(R.string.action_disconnect);
            } catch(SecurityException se) {
                se.printStackTrace();
            }
        }
    };

    private void addText(String text) {
        mTextView.append(text);
        final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
        if (scrollAmount > 0)
            mTextView.scrollTo(0, scrollAmount);
        else
            mTextView.scrollTo(0, 0);
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
                initDeviceListDialog();
                mDeviceListDialog.show();
            }
        });
    }
}