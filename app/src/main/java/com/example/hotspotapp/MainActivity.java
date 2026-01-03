package com.example.hotspotapp;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private WifiManager wifiManager;
    private Button toggleButton;
    private TextView statusText;
    private TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化WifiManager
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        // 创建UI界面
        createUI();
        
        // 更新UI状态
        updateHotspotStatus();
    }

    private void createUI() {
        // 创建线性布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 40, 20, 20);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // 创建状态文本
        statusText = new TextView(this);
        statusText.setTextSize(20);
        statusText.setPadding(0, 0, 0, 20);
        statusText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(statusText);

        // 创建信息文本
        infoText = new TextView(this);
        infoText.setTextSize(14);
        infoText.setText("SSID: MyHotspot\n密码: 12345678");
        infoText.setPadding(0, 0, 0, 40);
        infoText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(infoText);

        // 创建开关按钮
        toggleButton = new Button(this);
        toggleButton.setTextSize(18);
        toggleButton.setPadding(20, 15, 20, 15);
        toggleButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHotspot();
            }
        });
        
        layout.addView(toggleButton);
        
        // 设置布局
        setContentView(layout);
    }

    private void toggleHotspot() {
        boolean isEnabled = isHotspotEnabled();
        
        if (isEnabled) {
            disableHotspot();
        } else {
            enableHotspot();
        }
        
        updateHotspotStatus();
    }

    private boolean isHotspotEnabled() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void enableHotspot() {
        try {
            // 如果WiFi开启，先关闭
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                Toast.makeText(this, "已关闭WiFi，准备开启热点", Toast.LENGTH_SHORT).show();
            }

            // 配置热点信息
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "MyHotspot";
            config.preSharedKey = "12345678";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            // 调用setWifiApEnabled方法开启热点
            Method setWifiApEnabledMethod = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean result = (Boolean) setWifiApEnabledMethod.invoke(wifiManager, config, true);
            
            if (result) {
                Toast.makeText(this, "热点已成功开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "开启热点失败，请检查权限", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "开启热点失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void disableHotspot() {
        try {
            // 调用setWifiApEnabled方法关闭热点
            Method setWifiApEnabledMethod = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean result = (Boolean) setWifiApEnabledMethod.invoke(wifiManager, null, false);
            
            if (result) {
                Toast.makeText(this, "热点已成功关闭", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "关闭热点失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "关闭热点失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHotspotStatus() {
        boolean isEnabled = isHotspotEnabled();
        
        if (isEnabled) {
            statusText.setText("热点状态: 已开启");
            toggleButton.setText("关闭热点");
        } else {
            statusText.setText("热点状态: 已关闭");
            toggleButton.setText("开启热点");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到界面时更新状态
        updateHotspotStatus();
    }
}