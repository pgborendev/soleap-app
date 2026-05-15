package com.restaurant.pos.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.restaurant.pos.R;
import com.restaurant.pos.activities.SettingsActivity;
public class SettingsFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_settings,container,false);
        v.findViewById(R.id.btnOpenSettings).setOnClickListener(btn->{
            startActivity(new Intent(getContext(),SettingsActivity.class));
        });
        return v;
    }
}
