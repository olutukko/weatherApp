package com.example.weatherapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFERENCES_FILE = "my_prefs";
    private static final String TEMP_SWITCH_KEY = "isFahrenheit";
    private static final String SPEED_SWITCH_KEY = "isMilesPerHour";
    private static final String CLOCK_SWITCH_KEY = "is12HourClock";

    @Override
    protected void onCreate(Bundle savedInstanceState) { //takes switch states and uses them to set correct settings
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchMaterial temperatureSwitch = findViewById(R.id.temperatureSwitch);
        SwitchMaterial speedSwitch = findViewById(R.id.speedSwitch);
        SwitchMaterial clockSwitch = findViewById(R.id.clockSwitch);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        temperatureSwitch.setChecked(preferences.getBoolean(TEMP_SWITCH_KEY, false));
        speedSwitch.setChecked(preferences.getBoolean(SPEED_SWITCH_KEY, false));
        clockSwitch.setChecked(preferences.getBoolean(CLOCK_SWITCH_KEY, false));

        temperatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitchState(TEMP_SWITCH_KEY, isChecked));
        speedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitchState(SPEED_SWITCH_KEY, isChecked));
        clockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitchState(CLOCK_SWITCH_KEY, isChecked));

        Button changeActivityButton = findViewById(R.id.backButton);
        changeActivityButton.setOnClickListener(v -> finish());
    }

    private void saveSwitchState(String key, boolean isChecked) { //saves settings for later use
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }
}