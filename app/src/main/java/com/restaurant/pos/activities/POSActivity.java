package com.restaurant.pos.activities;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.restaurant.pos.R;
import com.restaurant.pos.adapters.CartAdapter;
import com.restaurant.pos.adapters.MenuAdapter;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.MenuItem;
import com.restaurant.pos.models.Order;
import com.restaurant.pos.utils.CartManager;
import com.restaurant.pos.utils.CurrencyUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class POSActivity extends AppCompatActivity implements MenuAdapter.OnMenuItemClickListener, CartAdapter.OnCartChangeListener {
    public static final String EXTRA_TABLE_ID="table_id";
    public static final String EXTRA_TABLE_NAME="table_name";

    private RecyclerView rvMenu, rvCart;
    private MenuAdapter menuAdapter;
    private CartAdapter cartAdapter;
    private ChipGroup chipGroup;
    private EditText etSearch;
    private TextView tvTableName,tvSubtotal,tvTax,tvTotal,tvCartEmpty;
    private Button btnSendKitchen,btnPay;
    private ProgressBar progressBar;
    private ERPNextService service;
    private CartManager cart;
    private List<MenuItem> allItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        service=new ERPNextService(this);
        cart=CartManager.getInstance();

        String tableId=getIntent().getStringExtra(EXTRA_TABLE_ID);
        String tableName=getIntent().getStringExtra(EXTRA_TABLE_NAME);
        if(tableId!=null) cart.setTable(tableId,tableName);

        tvTableName=findViewById(R.id.tvTableName);
        tvTableName.setText(tableName!=null?tableName:"New Order");

        rvMenu=findViewById(R.id.rvMenu);
        rvCart=findViewById(R.id.rvCart);
        chipGroup=findViewById(R.id.chipGroup);
        etSearch=findViewById(R.id.etSearch);
        tvSubtotal=findViewById(R.id.tvSubtotal);
        tvTax=findViewById(R.id.tvTax);
        tvTotal=findViewById(R.id.tvTotal);
        tvCartEmpty=findViewById(R.id.tvCartEmpty);
        btnSendKitchen=findViewById(R.id.btnSendKitchen);
        btnPay=findViewById(R.id.btnPay);
        progressBar=findViewById(R.id.progressBar);

        menuAdapter=new MenuAdapter(this,this);
        rvMenu.setLayoutManager(new GridLayoutManager(this,3));
        rvMenu.setAdapter(menuAdapter);

        cartAdapter=new CartAdapter(this,this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);

        etSearch.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){filterMenu(s.toString());}
            public void afterTextChanged(Editable s){}
        });

        btnSendKitchen.setOnClickListener(v->sendToKitchen());
        btnPay.setOnClickListener(v->openPayment());
        findViewById(R.id.btnBack).setOnClickListener(v->finish());

        loadMenu();
        updateCartUI();
    }

    private void loadMenu(){
        progressBar.setVisibility(View.VISIBLE);
        service.getMenuItems(new com.restaurant.pos.api.ApiCallback<List<MenuItem>>(){
            @Override public void onSuccess(List<MenuItem> items){
                progressBar.setVisibility(View.GONE);
                allItems=items;
                menuAdapter.setItems(items);
                setupCategoryChips(items);
            }
            @Override public void onError(String msg){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(POSActivity.this,"Failed to load menu: "+msg,Toast.LENGTH_SHORT).show();
                loadMockMenu();
            }
        });
    }

    private void loadMockMenu(){
        allItems=new ArrayList<>();
        String[][] data={{"1","Grilled Salmon","Main Course","24.50"},{"2","Caesar Salad","Starters","12.00"},
            {"3","Margherita Pizza","Main Course","18.00"},{"4","Beef Burger","Main Course","16.50"},
            {"5","Tiramisu","Desserts","8.50"},{"6","Tom Yum Soup","Starters","10.00"},
            {"7","Mango Smoothie","Beverages","6.00"},{"8","Espresso","Beverages","4.00"},
            {"9","Cheesecake","Desserts","9.00"},{"10","Spring Rolls","Starters","7.50"},
            {"11","Fried Rice","Main Course","13.00"},{"12","Pad Thai","Main Course","14.00"}};
        for(String[] d:data){
            MenuItem m=new MenuItem();m.setId(d[0]);m.setName(d[1]);m.setCategory(d[2]);m.setPrice(Double.parseDouble(d[3]));
            allItems.add(m);
        }
        menuAdapter.setItems(allItems);
        setupCategoryChips(allItems);
    }

    private void setupCategoryChips(List<MenuItem> items){
        Set<String> cats=new HashSet<>();
        for(MenuItem m:items) cats.add(m.getCategory());
        chipGroup.removeAllViews();
        Chip all=new Chip(this);all.setText("All");all.setCheckable(true);all.setChecked(true);
        all.setOnCheckedChangeListener((btn,checked)->{ if(checked)filterMenu(etSearch.getText().toString()); });
        chipGroup.addView(all);
        for(String cat:cats){
            Chip chip=new Chip(this);chip.setText(cat);chip.setCheckable(true);
            chip.setOnCheckedChangeListener((btn,checked)->{ if(checked)filterByCategory(cat,etSearch.getText().toString()); });
            chipGroup.addView(chip);
        }
    }

    private void filterMenu(String query){
        List<MenuItem> filtered=new ArrayList<>();
        for(MenuItem m:allItems){
            if(m.getName().toLowerCase().contains(query.toLowerCase())) filtered.add(m);
        }
        menuAdapter.setItems(filtered);
    }

    private void filterByCategory(String cat,String query){
        List<MenuItem> filtered=new ArrayList<>();
        for(MenuItem m:allItems){
            if(m.getCategory().equals(cat)&&m.getName().toLowerCase().contains(query.toLowerCase())) filtered.add(m);
        }
        menuAdapter.setItems(filtered);
    }

    @Override public void onMenuItemClicked(MenuItem item){
        if(!item.isAvailable()){Toast.makeText(this,"Item not available",Toast.LENGTH_SHORT).show();return;}
        cart.addItem(item);
        cartAdapter.setItems(cart.getCartItems());
        updateCartUI();
    }

    @Override public void onCartItemIncrement(String itemId){
        cart.incrementItem(itemId);
        cartAdapter.setItems(cart.getCartItems());
        updateCartUI();
    }

    @Override public void onCartItemDecrement(String itemId){
        cart.decrementItem(itemId);
        cartAdapter.setItems(cart.getCartItems());
        updateCartUI();
    }

    @Override public void onCartItemRemoved(String itemId){
        cart.removeItem(itemId);
        cartAdapter.setItems(cart.getCartItems());
        updateCartUI();
    }

    private void updateCartUI(){
        boolean empty=cart.isEmpty();
        tvCartEmpty.setVisibility(empty?View.VISIBLE:View.GONE);
        rvCart.setVisibility(empty?View.GONE:View.VISIBLE);
        double sub=cart.getSubtotal(),tax=cart.getTax(0.10),total=cart.getGrandTotal(0.10);
        tvSubtotal.setText(CurrencyUtils.format(sub));
        tvTax.setText(CurrencyUtils.format(tax));
        tvTotal.setText(CurrencyUtils.format(total));
        btnSendKitchen.setEnabled(!empty);
        btnPay.setEnabled(!empty);
    }

    private void sendToKitchen(){
        if(cart.isEmpty())return;
        progressBar.setVisibility(View.VISIBLE);
        Order order=buildOrder();
        service.createPOSInvoice(order,new com.restaurant.pos.api.ApiCallback<Order>(){
            @Override public void onSuccess(Order created){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(POSActivity.this,"Order sent to kitchen! ID: "+created.getId(),Toast.LENGTH_LONG).show();
                cart.clearCart();
                finish();
            }
            @Override public void onError(String msg){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(POSActivity.this,"Failed to send order: "+msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openPayment(){
        if(cart.isEmpty())return;
        android.content.Intent intent=new android.content.Intent(this,PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_TOTAL,cart.getGrandTotal(0.10));
        startActivity(intent);
    }

    private Order buildOrder(){
        Order order=new Order();
        order.setCustomer(cart.getCustomerName());
        order.setTableId(cart.getSelectedTableId());
        List<Order.OrderItem> items=new ArrayList<>();
        for(MenuItem m:cart.getCartItems()) items.add(new Order.OrderItem(m));
        order.setItems(items);
        return order;
    }
}
