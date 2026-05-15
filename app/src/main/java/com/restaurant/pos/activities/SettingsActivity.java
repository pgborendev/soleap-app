package com.restaurant.pos.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.pos.R;
import com.restaurant.pos.api.ERPNextClient;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.ERPConfig;
import com.restaurant.pos.utils.SessionManager;
public class SettingsActivity extends AppCompatActivity {
    private EditText etUrl,etApiKey,etApiSecret,etPosProfile,etCompany;
    private TextView tvConnectionStatus;
    private Button btnSave,btnTest,btnLogout;
    private ProgressBar progressBar;
    private ERPNextService service;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        service=new ERPNextService(this);
        etUrl=findViewById(R.id.etUrl);
        etApiKey=findViewById(R.id.etApiKey);
        etApiSecret=findViewById(R.id.etApiSecret);
        etPosProfile=findViewById(R.id.etPosProfile);
        etCompany=findViewById(R.id.etCompany);
        tvConnectionStatus=findViewById(R.id.tvConnectionStatus);
        btnSave=findViewById(R.id.btnSave);
        btnTest=findViewById(R.id.btnTest);
        btnLogout=findViewById(R.id.btnLogout);
        progressBar=findViewById(R.id.progressBar);

        ERPConfig cfg=SessionManager.getInstance(this).getERPConfig();
        if(cfg!=null){
            etUrl.setText(cfg.getBaseUrl());
            etApiKey.setText(cfg.getApiKey());
            etApiSecret.setText(cfg.getApiSecret());
            etPosProfile.setText(cfg.getPosProfile());
            etCompany.setText(cfg.getCompany());
        }

        btnSave.setOnClickListener(v->saveSettings());
        btnTest.setOnClickListener(v->testConnection());
        btnLogout.setOnClickListener(v->logout());
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
    }

    private void saveSettings(){
        ERPConfig cfg=new ERPConfig(
            etUrl.getText().toString().trim(),
            etApiKey.getText().toString().trim(),
            etApiSecret.getText().toString().trim(),
            etPosProfile.getText().toString().trim()
        );
        cfg.setCompany(etCompany.getText().toString().trim());
        SessionManager.getInstance(this).saveERPConfig(cfg);
        ERPNextClient.resetInstance();
        Toast.makeText(this,"Settings saved",Toast.LENGTH_SHORT).show();
    }

    private void testConnection(){
        progressBar.setVisibility(View.VISIBLE);
        tvConnectionStatus.setText("Testing...");
        btnTest.setEnabled(false);
        String url=etUrl.getText().toString().trim();
        String key=etApiKey.getText().toString().trim();
        String secret=etApiSecret.getText().toString().trim();
        service.testConnection(url,key,secret,new com.restaurant.pos.api.ApiCallback<Boolean>(){
            @Override public void onSuccess(Boolean r){
                progressBar.setVisibility(View.GONE);
                btnTest.setEnabled(true);
                tvConnectionStatus.setText("✓ Connected successfully");
                tvConnectionStatus.setTextColor(getResources().getColor(R.color.green,null));
            }
            @Override public void onError(String msg){
                progressBar.setVisibility(View.GONE);
                btnTest.setEnabled(true);
                tvConnectionStatus.setText("✗ Failed: "+msg);
                tvConnectionStatus.setTextColor(getResources().getColor(R.color.red,null));
            }
        });
    }

    private void logout(){
        SessionManager.getInstance(this).clearSession();
        ERPNextClient.resetInstance();
        startActivity(new Intent(this,LoginActivity.class));
        finishAffinity();
    }
}
