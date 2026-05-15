package com.restaurant.pos.ui.pos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.restaurant.pos.api.ApiResult;
import com.restaurant.pos.api.ERPNextClient;
import com.restaurant.pos.api.ERPNextRepository;
import com.restaurant.pos.models.ERPModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class POSViewModel extends ViewModel {

    private final ERPNextRepository repo = ERPNextRepository.getInstance();

    private String mTableName;
    private String mExistingInvoice;

    // Full menu from API
    private List<ERPModels.MenuItem> mAllMenu = new ArrayList<>();
    private String mCurrentCategory = "All";
    private String mSearchQuery = "";

    // Cart: itemCode → MenuItem (with quantity)
    private final Map<String, ERPModels.MenuItem> mCartMap = new HashMap<>();

    // LiveData
    private final MutableLiveData<List<ERPModels.MenuItem>> mFilteredMenu  = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ERPModels.MenuItem>> mCartItems      = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double>                   mSubtotal       = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double>                   mTax            = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double>                   mTotal          = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer>                  mCartCount      = new MutableLiveData<>(0);
    private final MutableLiveData<ApiResult<ERPModels.POSInvoice>> mOrderResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResult<Object>>        mPaymentResult  = new MutableLiveData<>();

    private static final double TAX_RATE = 0.10; // 10% VAT

    public void init(String tableName, String existingInvoice) {
        mTableName       = tableName;
        mExistingInvoice = existingInvoice;
    }

    // ─── Menu ─────────────────────────────────────────────────────────────────

    public void loadMenu() {
        repo.getMenuItems().observeForever(result -> {
            if (result.isSuccess() && result.data != null) {
                mAllMenu = result.data;
                applyFilter();
            }
        });
    }

    public void setCategory(String category) {
        mCurrentCategory = category;
        applyFilter();
    }

    public void filterMenu(String query) {
        mSearchQuery = query.toLowerCase();
        applyFilter();
    }

    private void applyFilter() {
        List<ERPModels.MenuItem> filtered = mAllMenu.stream()
                .filter(item -> {
                    boolean catMatch = "All".equals(mCurrentCategory) ||
                            mCurrentCategory.equals(item.itemGroup);
                    boolean searchMatch = mSearchQuery.isEmpty() ||
                            item.itemName.toLowerCase().contains(mSearchQuery);
                    return catMatch && searchMatch && item.isAvailable();
                })
                .collect(Collectors.toList());
        mFilteredMenu.postValue(filtered);
    }

    // ─── Cart ─────────────────────────────────────────────────────────────────

    public void addToCart(ERPModels.MenuItem item) {
        if (mCartMap.containsKey(item.name)) {
            mCartMap.get(item.name).quantity++;
        } else {
            item.quantity = 1;
            mCartMap.put(item.name, item);
        }
        refreshCart();
    }

    public void increaseQty(ERPModels.MenuItem item) {
        if (mCartMap.containsKey(item.name)) {
            mCartMap.get(item.name).quantity++;
            refreshCart();
        }
    }

    public void decreaseQty(ERPModels.MenuItem item) {
        if (mCartMap.containsKey(item.name)) {
            ERPModels.MenuItem c = mCartMap.get(item.name);
            c.quantity--;
            if (c.quantity <= 0) mCartMap.remove(item.name);
            refreshCart();
        }
    }

    public void removeFromCart(ERPModels.MenuItem item) {
        mCartMap.remove(item.name);
        refreshCart();
    }

    private void refreshCart() {
        List<ERPModels.MenuItem> cartList = new ArrayList<>(mCartMap.values());
        mCartItems.postValue(cartList);

        double subtotal = cartList.stream()
                .mapToDouble(i -> i.standardRate * i.quantity)
                .sum();
        double tax   = subtotal * TAX_RATE;
        double total = subtotal + tax;

        mSubtotal.postValue(subtotal);
        mTax.postValue(tax);
        mTotal.postValue(total);
        mCartCount.postValue(cartList.stream().mapToInt(i -> i.quantity).sum());
    }

    public boolean isCartEmpty() {
        return mCartMap.isEmpty();
    }

    // ─── Order Submission ─────────────────────────────────────────────────────

    public void sendToKitchen() {
        ERPModels.POSInvoice invoice = buildInvoice();
        repo.createInvoice(invoice).observeForever(result -> mOrderResult.postValue(result));
    }

    public void processPayment(String modeOfPayment) {
        if (mExistingInvoice != null) {
            // Invoice already exists — just post payment
            double total = mTotal.getValue() != null ? mTotal.getValue() : 0;
            repo.processPayment(mExistingInvoice, modeOfPayment, total)
                    .observeForever(result -> mPaymentResult.postValue(result));
        } else {
            // Create invoice first, then pay
            ERPModels.POSInvoice invoice = buildInvoice();
            repo.createInvoice(invoice).observeForever(result -> {
                if (result.isSuccess() && result.data != null) {
                    double total = mTotal.getValue() != null ? mTotal.getValue() : 0;
                    repo.processPayment(result.data.name, modeOfPayment, total)
                            .observeForever(payResult -> mPaymentResult.postValue(payResult));
                } else {
                    mPaymentResult.postValue(ApiResult.error("Failed to create invoice"));
                }
            });
        }
    }

    private ERPModels.POSInvoice buildInvoice() {
        ERPModels.POSInvoice invoice = new ERPModels.POSInvoice();
        invoice.customer    = "Walk-In Customer";
        invoice.posProfile  = ERPNextClient.getPosProfile();
        invoice.table       = mTableName;

        List<ERPModels.POSInvoiceItem> items = new ArrayList<>();
        for (ERPModels.MenuItem cartItem : mCartMap.values()) {
            ERPModels.POSInvoiceItem item = new ERPModels.POSInvoiceItem();
            item.itemCode  = cartItem.name;
            item.itemName  = cartItem.itemName;
            item.qty       = cartItem.quantity;
            item.rate      = cartItem.standardRate;
            item.amount    = cartItem.standardRate * cartItem.quantity;
            item.uom       = "Nos";
            items.add(item);
        }
        invoice.items = items;
        return invoice;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public LiveData<List<ERPModels.MenuItem>> getFilteredMenu()   { return mFilteredMenu; }
    public LiveData<List<ERPModels.MenuItem>> getCartItems()       { return mCartItems; }
    public LiveData<Double> getSubtotal()                          { return mSubtotal; }
    public LiveData<Double> getTax()                               { return mTax; }
    public LiveData<Double> getTotal()                             { return mTotal; }
    public LiveData<Integer> getCartCount()                        { return mCartCount; }
    public LiveData<ApiResult<ERPModels.POSInvoice>> getOrderResult() { return mOrderResult; }
    public LiveData<ApiResult<Object>> getPaymentResult()          { return mPaymentResult; }
}
