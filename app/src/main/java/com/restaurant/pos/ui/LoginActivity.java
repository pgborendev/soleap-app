package com.restaurant.pos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.restaurant.pos.api.ERPNextClient;
import com.restaurant.pos.api.ERPNextRepository;
import com.restaurant.pos.databinding.ActivityLoginBinding;

/**
 * Login / Setup screen.
 * On first launch users enter ERPNext URL + API credentials.
 * Subsequent launches skip straight to MainActivity if already configured.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ERPNextRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If already configured, go straight to MainActivity
        if (ERPNextClient.isConfigured()) {
            goToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = ERPNextRepository.getInstance();

        binding.btnConnect.setOnClickListener(v -> attemptConnect());
    }

    private void attemptConnect() {
        String url      = binding.etUrl.getText().toString().trim();
        String apiKey   = binding.etApiKey.getText().toString().trim();
        String apiSecret = binding.etApiSecret.getText().toString().trim();
        String profile  = binding.etPosProfile.getText().toString().trim();
        String company  = binding.etCompany.getText().toString().trim();

        if (url.isEmpty() || apiKey.isEmpty() || apiSecret.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (profile.isEmpty()) profile = "Restaurant POS";

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnConnect.setEnabled(false);

        // Save credentials and rebuild client
        ERPNextClient.saveConfig(url, apiKey, apiSecret, profile, company);
        ERPNextRepository.reset();

        // Test connection
        ERPNextRepository.getInstance().testConnection().observe(this, result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnConnect.setEnabled(true);

            if (result.isSuccess()) {
                Toast.makeText(this, "Connected as: " + result.data, Toast.LENGTH_SHORT).show();
                goToMain();
            } else {
                Toast.makeText(this, "Connection failed: " + result.error, Toast.LENGTH_LONG).show();
                // Clear bad credentials
                ERPNextClient.getPrefs().edit().clear().apply();
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
