package com.example.reachme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.reachme.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.baCk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.blocklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrivacyActivity.this,BlockList.class));
            }
        });

        binding.biometric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {

                SharedPreferences preferences = getSharedPreferences("shrdPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("switch", binding.biometric.isChecked());

                editor.apply();


            }
        });
        loadData();
    }

    private void loadData() {
        Boolean switchVal;

        SharedPreferences preferences = getSharedPreferences("shrdPref", MODE_PRIVATE);

        switchVal = preferences.getBoolean("switch", false);

        binding.biometric.setChecked(switchVal);
    }
}