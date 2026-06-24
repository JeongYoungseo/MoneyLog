package kr.ac.kopo.moneylog;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kr.ac.kopo.moneylog.util.SharedPreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        boolean isDark = SharedPreferenceManager.loadDarkMode(this);
        switchDarkMode.setChecked(isDark);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferenceManager.saveDarkMode(SettingsActivity.this, isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_home) {
                startActivity(
                        new Intent(
                                SettingsActivity.this, MainActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.navigation_statistics) {
                startActivity(
                        new Intent(SettingsActivity.this, StatisticsActivity.class));
                return true;
            }
            return true;
        });
    }
}