package com.restaurant.pos.activities;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.restaurant.pos.R;
import com.restaurant.pos.adapters.KitchenOrderAdapter;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.Order;
import java.util.*;
public class KitchenDisplayActivity extends AppCompatActivity implements KitchenOrderAdapter.OnKitchenActionListener {
    private RecyclerView rvNew,rvCooking,rvReady;
    private KitchenOrderAdapter newAdapter,cookingAdapter,readyAdapter;
    private ERPNextService service;
    private Handler refreshHandler=new Handler();
    private static final int REFRESH_INTERVAL=30000;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_kitchen);
        service=new ERPNextService(this);

        rvNew=findViewById(R.id.rvNew);
        rvCooking=findViewById(R.id.rvCooking);
        rvReady=findViewById(R.id.rvReady);

        newAdapter=new KitchenOrderAdapter(this,"new",this);
        cookingAdapter=new KitchenOrderAdapter(this,"cooking",this);
        readyAdapter=new KitchenOrderAdapter(this,"ready",this);

        rvNew.setLayoutManager(new LinearLayoutManager(this));
        rvCooking.setLayoutManager(new LinearLayoutManager(this));
        rvReady.setLayoutManager(new LinearLayoutManager(this));
        rvNew.setAdapter(newAdapter);
        rvCooking.setAdapter(cookingAdapter);
        rvReady.setAdapter(readyAdapter);

        loadOrders();
        startAutoRefresh();
    }

    private void loadOrders(){
        service.getActiveOrders(new com.restaurant.pos.api.ApiCallback<List<Order>>(){
            @Override public void onSuccess(List<Order> orders){
                List<Order> newList=new ArrayList<>(),cookList=new ArrayList<>(),readyList=new ArrayList<>();
                for(Order o:orders){
                    switch(o.getKitchenStatus()){
                        case"cooking":cookList.add(o);break;
                        case"ready":readyList.add(o);break;
                        default:newList.add(o);
                    }
                }
                newAdapter.setOrders(newList);
                cookingAdapter.setOrders(cookList);
                readyAdapter.setOrders(readyList);
            }
            @Override public void onError(String msg){
                Toast.makeText(KitchenDisplayActivity.this,"Error: "+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onStartCooking(Order order){ order.setKitchenStatus("cooking"); loadOrders(); }
    @Override public void onMarkReady(Order order){ order.setKitchenStatus("ready"); loadOrders(); }
    @Override public void onMarkServed(Order order){ order.setKitchenStatus("served"); loadOrders(); }

    private void startAutoRefresh(){
        refreshHandler.postDelayed(new Runnable(){
            @Override public void run(){
                loadOrders();
                refreshHandler.postDelayed(this,REFRESH_INTERVAL);
            }
        },REFRESH_INTERVAL);
    }

    @Override protected void onDestroy(){ super.onDestroy(); refreshHandler.removeCallbacksAndMessages(null); }
}
