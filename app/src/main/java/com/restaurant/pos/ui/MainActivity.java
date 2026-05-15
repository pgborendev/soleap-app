package com.restaurant.pos.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.restaurant.pos.R;
import com.restaurant.pos.databinding.ActivityMainBinding;

/**
 * Host activity. Contains a NavHostFragment with bottom navigation.
 * Fragments: Dashboard → Tables → Orders → Kitchen → (Settings launched as Activity)
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up Navigation
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Bottom nav destinations (no back-stack on tab switch)
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.nav_dashboard,
                R.id.nav_tables,
                R.id.nav_orders,
                R.id.nav_kitchen
        ).build();

        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }
}
