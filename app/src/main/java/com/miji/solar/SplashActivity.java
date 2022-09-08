package com.miji.solar;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermissionUtil;
import com.gun0912.tedpermission.normal.TedPermission;
import com.miji.solar.client.BluetoothSerialClient;

import java.util.List;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();

    private BluetoothSerialClient bluetoothSerialClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 블루투스 사용 가능 여부 확인
        bluetoothSerialClient = BluetoothSerialClient.getInstance();
        if (bluetoothSerialClient == null) {
            Toast.makeText(getApplicationContext(), "블루투스 사용이 불가능한 기기입니다.", Toast.LENGTH_LONG).show();
        }

        // 권한 적용 여부 확인
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

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.custom));
            }

            // splash 화면에서 2초뒤 main 화면으로 넘어가도록 설정
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent main = new Intent(getApplicationContext(), MijiMainActivity.class);
                    startActivity(main);
                    finish();
                }
            }, 2000);
        } else {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toast.makeText(SplashActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    // 권한이 허용된 경우 메인 화면으로 이동
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent main = new Intent(getApplicationContext(), MijiMainActivity.class);
                            startActivity(main);
                            finish();
                        }
                    }, 1000);
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(SplashActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }


            };

            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle("권한 허용 요청")
                    .setRationaleMessage("앱 이용을 위해서는 권한 허용이 필요합니다.")
                    .setDeniedTitle("권한 허용 거부")
                    .setDeniedMessage(
                            "앱 이용에 필요한 권한을 허용하지 않으실 경우 앱 사용이 불가능합니다.\n\n 권한을 허용해주세요.")
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
}