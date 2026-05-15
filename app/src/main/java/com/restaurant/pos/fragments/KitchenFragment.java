package com.restaurant.pos.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.restaurant.pos.R;
import com.restaurant.pos.activities.KitchenDisplayActivity;
public class KitchenFragment extends Fragment {
    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_kitchen,container,false);
        v.findViewById(R.id.btnOpenKDS).setOnClickListener(btn->{
            startActivity(new Intent(getContext(),KitchenDisplayActivity.class));
        });
        return v;
    }
}
