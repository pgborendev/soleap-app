package com.restaurant.pos.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.restaurant.pos.R;
import com.restaurant.pos.activities.KitchenDisplayActivity;
import com.restaurant.pos.activities.POSActivity;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.Order;
import com.restaurant.pos.utils.CurrencyUtils;
import com.restaurant.pos.utils.SessionManager;
import java.util.List;
public class DashboardFragment extends Fragment {
    private TextView tvRevenue,tvOrders,tvTables,tvAvgOrder,tvUsername,tvSyncStatus;
    private ERPNextService service;

    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_dashboard,container,false);
        service=new ERPNextService(requireContext());
        tvRevenue=v.findViewById(R.id.tvRevenue);
        tvOrders=v.findViewById(R.id.tvOrders);
        tvTables=v.findViewById(R.id.tvTables);
        tvAvgOrder=v.findViewById(R.id.tvAvgOrder);
        tvUsername=v.findViewById(R.id.tvUsername);
        tvSyncStatus=v.findViewById(R.id.tvSyncStatus);

        tvUsername.setText("Welcome, "+SessionManager.getInstance(requireContext()).getUsername());

        v.findViewById(R.id.btnNewOrder).setOnClickListener(btn->{
            startActivity(new Intent(getContext(),POSActivity.class));
        });
        v.findViewById(R.id.btnKitchen).setOnClickListener(btn->{
            startActivity(new Intent(getContext(),KitchenDisplayActivity.class));
        });

        loadStats();
        return v;
    }

    private void loadStats(){
        service.getActiveOrders(new com.restaurant.pos.api.ApiCallback<List<Order>>(){
            @Override public void onSuccess(List<Order> orders){
                if(getContext()==null)return;
                tvOrders.setText(String.valueOf(orders.size()));
                double total=0;
                for(Order o:orders)total+=o.getGrandTotal();
                tvRevenue.setText(CurrencyUtils.format(total));
                tvAvgOrder.setText(orders.isEmpty()?"$0.00":CurrencyUtils.format(total/orders.size()));
                tvSyncStatus.setText("Synced with ERPNext ✓");
            }
            @Override public void onError(String msg){
                if(getContext()==null)return;
                tvSyncStatus.setText("Sync error - using offline mode");
                tvRevenue.setText("$0.00");tvOrders.setText("0");tvAvgOrder.setText("$0.00");
            }
        });
    }
}
