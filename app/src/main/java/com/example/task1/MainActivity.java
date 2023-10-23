package com.example.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private Button startDownloadButton;
    private ToggleButton toggleButton;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String BACKGROUND_COLOR_KEY = "bgColor";
    private RelativeLayout mainLayout;  // Add this line
    private BluetoothReceiver bluetoothReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startDownloadButton = findViewById(R.id.startDownloadButton);
        toggleButton = findViewById(R.id.toggleButton);
        mainLayout = findViewById(R.id.mainLayout); // Add this line
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        bluetoothReceiver = new BluetoothReceiver();
        // Register the BluetoothReceiver to listen for Bluetooth state changes
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);
        // Check if there's a saved background color in SharedPreferences and apply it
        int savedColor = sharedPreferences.getInt(BACKGROUND_COLOR_KEY, 0);
        if (savedColor != 0) {
            mainLayout.setBackgroundColor(savedColor);
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    mainLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    // Save the background color in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(BACKGROUND_COLOR_KEY, getResources().getColor(R.color.colorPrimary));
                    editor.apply();
                } else {
                    mainLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
                    // Save the background color in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(BACKGROUND_COLOR_KEY, getResources().getColor(android.R.color.white));
                    editor.apply();
                }
            }
        });

        startDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloadService("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BluetoothReceiver when the activity is destroyed
        unregisterReceiver(bluetoothReceiver);
    }

    private void startDownloadService(final String downloadUrl) {
        Intent serviceIntent = new Intent(this, DownloadService.class);
        serviceIntent.setAction(DownloadService.ACTION_START_DOWNLOAD);
        serviceIntent.putExtra("download_url", downloadUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}
