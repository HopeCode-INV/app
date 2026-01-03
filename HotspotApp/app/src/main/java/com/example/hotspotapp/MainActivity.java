package com.example.hotspotapp;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private WifiManager wifiManager;
    private Button toggleButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        toggleButton = findViewById(R.id.toggle_button);
        statusText = findViewById(R.id.status_text);

        updateUI();

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHotspot();
            }
        });
    }

    private void toggleHotspot() {
        if (isHotspotEnabled()) {
            disableHotspot();
        } else {
            enableHotspot();
        }
        updateUI();
    }

    private void enableHotspot() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }

        try {
            Method getWifiApConfiguration = wifiManager.getClass().getMethod("getWifiApConfiguration");
            Method setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);

            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "MyHotspot";
            config.preSharedKey = "12345678";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            boolean success = (Boolean) setWifiApEnabled.invoke(wifiManager, config, true);
            Toast.makeText(this, success ? "热点已开启" : "开启热点失败", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "开启热点失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableHotspot() {
        try {
            Method setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean success = (Boolean) setWifiApEnabled.invoke(wifiManager, null, false);
            Toast.makeText(this, success ? "热点已关闭" : "关闭热点失败", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "关闭热点失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isHotspotEnabled() {
        try {
            Method isWifiApEnabled = wifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean) isWifiApEnabled.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateUI() {
        boolean isEnabled = isHotspotEnabled();
        statusText.setText(isEnabled ? "热点已开启" : "热点已关闭");
        toggleButton.setText(isEnabled ? "关闭热点" : "开启热点");
    }
}
