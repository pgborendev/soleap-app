package com.restaurant.pos.fragments;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.restaurant.pos.R;
import com.restaurant.pos.adapters.OrderAdapter;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.Order;
import java.util.List;
public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ERPNextService service;
    private TextView tvEmpty;

    @Override public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_orders,container,false);
        service=new ERPNextService(requireContext());
        recyclerView=v.findViewById(R.id.recyclerView);
        swipeRefresh=v.findViewById(R.id.swipeRefresh);
        tvEmpty=v.findViewById(R.id.tvEmpty);
        adapter=new OrderAdapter(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this::loadOrders);
        loadOrders();
        return v;
    }

    private void loadOrders(){
        service.getActiveOrders(new com.restaurant.pos.api.ApiCallback<List<Order>>(){
            @Override public void onSuccess(List<Order> orders){
                if(getContext()==null)return;
                swipeRefresh.setRefreshing(false);
                adapter.setOrders(orders);
                tvEmpty.setVisibility(orders.isEmpty()?View.VISIBLE:View.GONE);
            }
            @Override public void onError(String msg){
                if(getContext()==null)return;
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(),"Error: "+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
