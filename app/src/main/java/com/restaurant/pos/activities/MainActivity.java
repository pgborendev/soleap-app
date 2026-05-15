package com.restaurant.pos.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.restaurant.pos.R;
import com.restaurant.pos.fragments.*;
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav=findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);
        // Load dashboard by default
        if(savedInstanceState==null){
            loadFragment(new DashboardFragment());
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item){
        Fragment f;
        int id=item.getItemId();
        if(id==R.id.nav_dashboard) f=new DashboardFragment();
        else if(id==R.id.nav_tables) f=new TablesFragment();
        else if(id==R.id.nav_orders) f=new OrdersFragment();
        else if(id==R.id.nav_kitchen) f=new KitchenFragment();
        else if(id==R.id.nav_settings) f=new SettingsFragment();
        else return false;
        loadFragment(f);
        return true;
    }

    private void loadFragment(Fragment f){
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer,f)
            .commit();
    }
}
