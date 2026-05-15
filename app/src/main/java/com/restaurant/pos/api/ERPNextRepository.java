package com.restaurant.pos.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.restaurant.pos.models.ERPModels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository layer between ViewModels and the Retrofit API service.
 * Returns LiveData so ViewModels can observe results reactively.
 */
public class ERPNextRepository {

    private static ERPNextRepository sInstance;
    private final ERPNextApiService mApi;

    // Standard field sets for ERPNext list queries
    private static final String MENU_FIELDS =
            "[\"name\",\"item_name\",\"standard_rate\",\"item_group\",\"image\",\"description\",\"disabled\"]";
    private static final String TABLE_FIELDS =
            "[\"name\",\"table_name\",\"status\",\"capacity\",\"floor\",\"current_order\"]";
    private static final String INVOICE_FIELDS =
            "[\"name\",\"customer\",\"grand_total\",\"status\",\"posting_date\",\"posting_time\",\"table\",\"waiter\"]";
    private static final String KOT_FIELDS =
            "[\"name\",\"pos_invoice\",\"table\",\"status\",\"creation\"]";

    private ERPNextRepository() {
        mApi = ERPNextClient.getService();
    }

    public static ERPNextRepository getInstance() {
        if (sInstance == null) {
            synchronized (ERPNextRepository.class) {
                if (sInstance == null) sInstance = new ERPNextRepository();
            }
        }
        return sInstance;
    }

    /** Re-create after config change */
    public static void reset() {
        sInstance = null;
    }

    // ─── Menu ─────────────────────────────────────────────────────────────────

    public LiveData<ApiResult<List<ERPModels.MenuItem>>> getMenuItems() {
        MutableLiveData<ApiResult<List<ERPModels.MenuItem>>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        String filters = "[[\"disabled\",\"=\",0]]";
        mApi.getMenuItems(filters, MENU_FIELDS, 200)
                .enqueue(new Callback<ERPModels.ListResponse<ERPModels.MenuItem>>() {
                    @Override
                    public void onResponse(Call<ERPModels.ListResponse<ERPModels.MenuItem>> call,
                                           Response<ERPModels.ListResponse<ERPModels.MenuItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.ListResponse<ERPModels.MenuItem>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    // ─── Tables ───────────────────────────────────────────────────────────────

    public LiveData<ApiResult<List<ERPModels.RestaurantTable>>> getTables() {
        MutableLiveData<ApiResult<List<ERPModels.RestaurantTable>>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        mApi.getTables(TABLE_FIELDS, 50)
                .enqueue(new Callback<ERPModels.ListResponse<ERPModels.RestaurantTable>>() {
                    @Override
                    public void onResponse(Call<ERPModels.ListResponse<ERPModels.RestaurantTable>> call,
                                           Response<ERPModels.ListResponse<ERPModels.RestaurantTable>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.ListResponse<ERPModels.RestaurantTable>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<ApiResult<ERPModels.RestaurantTable>> updateTableStatus(String tableName, String status) {
        MutableLiveData<ApiResult<ERPModels.RestaurantTable>> liveData = new MutableLiveData<>();
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        mApi.updateTableStatus(tableName, body)
                .enqueue(new Callback<ERPModels.SingleResponse<ERPModels.RestaurantTable>>() {
                    @Override
                    public void onResponse(Call<ERPModels.SingleResponse<ERPModels.RestaurantTable>> call,
                                           Response<ERPModels.SingleResponse<ERPModels.RestaurantTable>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.SingleResponse<ERPModels.RestaurantTable>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    // ─── POS Invoice ──────────────────────────────────────────────────────────

    public LiveData<ApiResult<List<ERPModels.POSInvoice>>> getActiveInvoices() {
        MutableLiveData<ApiResult<List<ERPModels.POSInvoice>>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        String filters = "[[\"status\",\"in\",[\"Draft\",\"Submitted\"]],[\"docstatus\",\"!=\",2]]";
        mApi.getPOSInvoices(filters, INVOICE_FIELDS, 100, "creation desc")
                .enqueue(new Callback<ERPModels.ListResponse<ERPModels.POSInvoice>>() {
                    @Override
                    public void onResponse(Call<ERPModels.ListResponse<ERPModels.POSInvoice>> call,
                                           Response<ERPModels.ListResponse<ERPModels.POSInvoice>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.ListResponse<ERPModels.POSInvoice>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<ApiResult<ERPModels.POSInvoice>> createInvoice(ERPModels.POSInvoice invoice) {
        MutableLiveData<ApiResult<ERPModels.POSInvoice>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        mApi.createPOSInvoice(invoice)
                .enqueue(new Callback<ERPModels.SingleResponse<ERPModels.POSInvoice>>() {
                    @Override
                    public void onResponse(Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> call,
                                           Response<ERPModels.SingleResponse<ERPModels.POSInvoice>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<ApiResult<Object>> processPayment(String invoiceName,
                                                       String modeOfPayment,
                                                       double amount) {
        MutableLiveData<ApiResult<Object>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        ERPModels.PaymentRequest req = new ERPModels.PaymentRequest();
        req.invoice = invoiceName;
        req.modeOfPayment = modeOfPayment;
        req.amount = amount;

        mApi.processPayment(req)
                .enqueue(new Callback<ERPModels.SingleResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ERPModels.SingleResponse<Object>> call,
                                           Response<ERPModels.SingleResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            liveData.postValue(ApiResult.success(null));
                        } else {
                            liveData.postValue(ApiResult.error("Payment failed: HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.SingleResponse<Object>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    // ─── Kitchen ──────────────────────────────────────────────────────────────

    public LiveData<ApiResult<List<ERPModels.KitchenOrderTicket>>> getKOTs() {
        MutableLiveData<ApiResult<List<ERPModels.KitchenOrderTicket>>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        String filters = "[[\"status\",\"in\",[\"New\",\"Preparing\",\"Ready\"]]]";
        mApi.getKOTs(filters, KOT_FIELDS, 50)
                .enqueue(new Callback<ERPModels.ListResponse<ERPModels.KitchenOrderTicket>>() {
                    @Override
                    public void onResponse(Call<ERPModels.ListResponse<ERPModels.KitchenOrderTicket>> call,
                                           Response<ERPModels.ListResponse<ERPModels.KitchenOrderTicket>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.ListResponse<ERPModels.KitchenOrderTicket>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<ApiResult<ERPModels.KitchenOrderTicket>> updateKOTStatus(String kotName, String status) {
        MutableLiveData<ApiResult<ERPModels.KitchenOrderTicket>> liveData = new MutableLiveData<>();
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        mApi.updateKOTStatus(kotName, body)
                .enqueue(new Callback<ERPModels.SingleResponse<ERPModels.KitchenOrderTicket>>() {
                    @Override
                    public void onResponse(Call<ERPModels.SingleResponse<ERPModels.KitchenOrderTicket>> call,
                                           Response<ERPModels.SingleResponse<ERPModels.KitchenOrderTicket>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.SingleResponse<ERPModels.KitchenOrderTicket>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }

    // ─── Connection test ──────────────────────────────────────────────────────

    public LiveData<ApiResult<String>> testConnection() {
        MutableLiveData<ApiResult<String>> liveData = new MutableLiveData<>();
        liveData.postValue(ApiResult.loading());

        mApi.getLoggedUser()
                .enqueue(new Callback<ERPModels.SingleResponse<String>>() {
                    @Override
                    public void onResponse(Call<ERPModels.SingleResponse<String>> call,
                                           Response<ERPModels.SingleResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(ApiResult.success(response.body().data));
                        } else {
                            liveData.postValue(ApiResult.error("Auth failed: HTTP " + response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<ERPModels.SingleResponse<String>> call, Throwable t) {
                        liveData.postValue(ApiResult.error(t.getMessage()));
                    }
                });
        return liveData;
    }
}
