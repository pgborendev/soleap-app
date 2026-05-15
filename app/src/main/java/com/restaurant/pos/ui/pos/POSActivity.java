package com.restaurant.pos.ui.pos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.restaurant.pos.adapters.CartAdapter;
import com.restaurant.pos.adapters.MenuItemAdapter;
import com.restaurant.pos.databinding.ActivityPosBinding;
import com.restaurant.pos.models.ERPModels;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Point-of-Sale activity.
 * Left panel: menu items grid with category chips.
 * Right panel (or bottom sheet): cart summary.
 * On "Place Order": creates POS Invoice in ERPNext via API.
 * On "Pay": processes payment and submits invoice.
 */
public class POSActivity extends AppCompatActivity {

    public static final String EXTRA_TABLE_NAME     = "table_name";
    public static final String EXTRA_TABLE_DISPLAY  = "table_display";
    public static final String EXTRA_TABLE_CAPACITY = "table_capacity";
    public static final String EXTRA_INVOICE_NAME   = "invoice_name";

    private ActivityPosBinding binding;
    private POSViewModel viewModel;
    private MenuItemAdapter menuAdapter;
    private CartAdapter cartAdapter;

    private String tableName;
    private String tableDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tableName    = getIntent().getStringExtra(EXTRA_TABLE_NAME);
        tableDisplay = getIntent().getStringExtra(EXTRA_TABLE_DISPLAY);
        String existingInvoice = getIntent().getStringExtra(EXTRA_INVOICE_NAME);

        viewModel = new ViewModelProvider(this).get(POSViewModel.class);
        viewModel.init(tableName, existingInvoice);

        setupToolbar();
        setupMenu();
        setupCart();
        setupActions();
        observeViewModel();

        viewModel.loadMenu();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(tableDisplay != null ? tableDisplay : "New Order");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupMenu() {
        menuAdapter = new MenuItemAdapter(new ArrayList<>(), item -> viewModel.addToCart(item));
        binding.rvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMenu.setAdapter(menuAdapter);

        // Search
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterMenu(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Category chips
        binding.chipAll.setOnClickListener(v -> viewModel.setCategory("All"));
        binding.chipStarters.setOnClickListener(v -> viewModel.setCategory("Starters"));
        binding.chipMain.setOnClickListener(v -> viewModel.setCategory("Main Course"));
        binding.chipDesserts.setOnClickListener(v -> viewModel.setCategory("Desserts"));
        binding.chipBeverages.setOnClickListener(v -> viewModel.setCategory("Beverages"));
    }

    private void setupCart() {
        cartAdapter = new CartAdapter(new ArrayList<>(),
                item -> viewModel.increaseQty(item),
                item -> viewModel.decreaseQty(item),
                item -> viewModel.removeFromCart(item));
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(cartAdapter);
    }

    private void setupActions() {
        binding.btnSendToKitchen.setOnClickListener(v -> viewModel.sendToKitchen());
        binding.btnPay.setOnClickListener(v -> showPaymentDialog());
    }

    private void observeViewModel() {
        viewModel.getFilteredMenu().observe(this, items -> {
            menuAdapter.updateData(items);
            binding.menuProgress.setVisibility(View.GONE);
        });

        viewModel.getCartItems().observe(this, cartItems -> {
            cartAdapter.updateData(cartItems);
            boolean hasItems = !cartItems.isEmpty();
            binding.btnSendToKitchen.setEnabled(hasItems);
            binding.btnPay.setEnabled(hasItems);
        });

        viewModel.getSubtotal().observe(this, subtotal ->
                binding.tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", subtotal)));

        viewModel.getTax().observe(this, tax ->
                binding.tvTax.setText(String.format(Locale.getDefault(), "$%.2f", tax)));

        viewModel.getTotal().observe(this, total ->
                binding.tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", total)));

        viewModel.getCartCount().observe(this, count ->
                binding.tvCartCount.setText(count + " items"));

        viewModel.getOrderResult().observe(this, result -> {
            binding.progressOverlay.setVisibility(View.GONE);
            if (result.isLoading()) {
                binding.progressOverlay.setVisibility(View.VISIBLE);
            } else if (result.isSuccess()) {
                Toast.makeText(this, "✓ Order sent to ERPNext!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (result.isError()) {
                Toast.makeText(this, "Error: " + result.error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getPaymentResult().observe(this, result -> {
            if (result.isSuccess()) {
                Toast.makeText(this, "✓ Payment posted to ERPNext!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (result.isError()) {
                Toast.makeText(this, "Payment error: " + result.error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPaymentDialog() {
        String[] methods = {"Cash", "Credit Card", "Debit Card", "QR / E-Wallet", "Bank Transfer"};
        new AlertDialog.Builder(this)
                .setTitle("Select Payment Method")
                .setItems(methods, (dialog, which) -> {
                    String method = methods[which];
                    viewModel.processPayment(method);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (!viewModel.isCartEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Leave Order?")
                    .setMessage("Cart items will be lost. Are you sure?")
                    .setPositiveButton("Leave", (d, w) -> super.onBackPressed())
                    .setNegativeButton("Stay", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
