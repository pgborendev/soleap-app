package com.restaurant.pos.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.restaurant.pos.api.ApiResult;
import com.restaurant.pos.api.ERPNextRepository;
import com.restaurant.pos.models.ERPModels;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private final ERPNextRepository repo = ERPNextRepository.getInstance();

    private final MutableLiveData<ApiResult<List<ERPModels.POSInvoice>>> mInvoices = new MutableLiveData<>();
    private final MutableLiveData<ApiResult<List<ERPModels.RestaurantTable>>> mTables = new MutableLiveData<>();

    public LiveData<ApiResult<List<ERPModels.POSInvoice>>> getInvoices() { return mInvoices; }
    public LiveData<ApiResult<List<ERPModels.RestaurantTable>>> getTables() { return mTables; }

    public void refresh() {
        repo.getActiveInvoices().observeForever(result -> mInvoices.postValue(result));
        repo.getTables().observeForever(result -> mTables.postValue(result));
    }
}
