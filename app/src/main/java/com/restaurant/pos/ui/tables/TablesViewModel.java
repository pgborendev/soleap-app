package com.restaurant.pos.ui.tables;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.restaurant.pos.api.ApiResult;
import com.restaurant.pos.api.ERPNextRepository;
import com.restaurant.pos.models.ERPModels;

import java.util.List;

public class TablesViewModel extends ViewModel {

    private final ERPNextRepository repo = ERPNextRepository.getInstance();
    private final MutableLiveData<ApiResult<List<ERPModels.RestaurantTable>>> mTables = new MutableLiveData<>();

    public LiveData<ApiResult<List<ERPModels.RestaurantTable>>> getTables() { return mTables; }

    public void loadTables() {
        repo.getTables().observeForever(result -> mTables.postValue(result));
    }

    public void updateStatus(String tableName, String status) {
        repo.updateTableStatus(tableName, status).observeForever(result -> {
            // Re-load tables after update
            if (result.isSuccess()) loadTables();
        });
    }
}
