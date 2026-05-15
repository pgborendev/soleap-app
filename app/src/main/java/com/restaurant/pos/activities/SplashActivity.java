package com.restaurant.pos.activities;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.restaurant.pos.R;
import com.restaurant.pos.utils.SessionManager;
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(()->{
            SessionManager sm=SessionManager.getInstance(this);
            Intent intent;
            if(sm.hasConfig()&&sm.isLoggedIn()){
                intent=new Intent(this,MainActivity.class);
            }else{
                intent=new Intent(this,LoginActivity.class);
            }
            startActivity(intent);
            finish();
        },2000);
    }
}
