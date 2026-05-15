package com.restaurant.pos.activities;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.pos.R;
import com.restaurant.pos.api.ERPNextClient;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.ERPConfig;
import com.restaurant.pos.utils.SessionManager;
public class LoginActivity extends AppCompatActivity {
    private EditText etUrl,etApiKey,etApiSecret,etPosProfile;
    private Button btnConnect;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private ERPNextService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUrl=findViewById(R.id.etUrl);
        etApiKey=findViewById(R.id.etApiKey);
        etApiSecret=findViewById(R.id.etApiSecret);
        etPosProfile=findViewById(R.id.etPosProfile);
        btnConnect=findViewById(R.id.btnConnect);
        progressBar=findViewById(R.id.progressBar);
        tvStatus=findViewById(R.id.tvStatus);

        // Load saved config if exists
        ERPConfig saved=SessionManager.getInstance(this).getERPConfig();
        if(saved!=null){
            etUrl.setText(saved.getBaseUrl());
            etApiKey.setText(saved.getApiKey());
            etApiSecret.setText(saved.getApiSecret());
            etPosProfile.setText(saved.getPosProfile());
        }

        btnConnect.setOnClickListener(v->attemptConnect());
    }

    private void attemptConnect(){
        String url=etUrl.getText().toString().trim();
        String key=etApiKey.getText().toString().trim();
        String secret=etApiSecret.getText().toString().trim();
        String profile=etPosProfile.getText().toString().trim();
        if(TextUtils.isEmpty(url)||TextUtils.isEmpty(key)||TextUtils.isEmpty(secret)){
            Toast.makeText(this,"Please fill all required fields",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(profile)) profile="Restaurant POS";
        ERPConfig config=new ERPConfig(url,key,secret,profile);
        SessionManager.getInstance(this).saveERPConfig(config);
        ERPNextClient.resetInstance();
        service=new ERPNextService(this);
        btnConnect.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("Connecting to ERPNext...");
        String finalProfile=profile;
        service.testConnection(url,key,secret,new com.restaurant.pos.api.ApiCallback<Boolean>(){
            @Override public void onSuccess(Boolean result){
                SessionManager.getInstance(LoginActivity.this).setLoggedIn(true);
                progressBar.setVisibility(View.GONE);
                tvStatus.setText("Connected! Loading POS...");
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
            @Override public void onError(String message){
                btnConnect.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                tvStatus.setText("Error: "+message);
                Toast.makeText(LoginActivity.this,"Connection failed: "+message,Toast.LENGTH_LONG).show();
            }
        });
    }
}
