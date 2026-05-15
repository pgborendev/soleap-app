package com.restaurant.pos.api;

import com.restaurant.pos.models.ERPModels;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * ERPNext REST API Interface for URYPOS module.
 *
 * Base URL: https://your-erpnext-instance.com
 * Auth:     token {api_key}:{api_secret}  (set via OkHttp interceptor)
 *
 * ERPNext v15 endpoints used:
 *   GET  /api/resource/{DocType}           → list
 *   GET  /api/resource/{DocType}/{name}    → single
 *   POST /api/resource/{DocType}           → create
 *   PUT  /api/resource/{DocType}/{name}    → update
 *   POST /api/method/{dotted.path}         → whitelisted server method
 */
public interface ERPNextApiService {

    // ─── Menu Items ───────────────────────────────────────────────────────────

    @GET("api/resource/Item")
    Call<ERPModels.ListResponse<ERPModels.MenuItem>> getMenuItems(
            @Query("filters") String filters,
            @Query("fields") String fields,
            @Query("limit_page_length") int limit
    );

    // ─── Restaurant Tables ────────────────────────────────────────────────────

    @GET("api/resource/Restaurant Table")
    Call<ERPModels.ListResponse<ERPModels.RestaurantTable>> getTables(
            @Query("fields") String fields,
            @Query("limit_page_length") int limit
    );

    @PUT("api/resource/Restaurant Table/{name}")
    Call<ERPModels.SingleResponse<ERPModels.RestaurantTable>> updateTableStatus(
            @Path("name") String tableName,
            @Body Map<String, String> body
    );

    // ─── POS Invoice ──────────────────────────────────────────────────────────

    @GET("api/resource/POS Invoice")
    Call<ERPModels.ListResponse<ERPModels.POSInvoice>> getPOSInvoices(
            @Query("filters") String filters,
            @Query("fields") String fields,
            @Query("limit_page_length") int limit,
            @Query("order_by") String orderBy
    );

    @GET("api/resource/POS Invoice/{name}")
    Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> getPOSInvoice(
            @Path("name") String invoiceName
    );

    @POST("api/resource/POS Invoice")
    Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> createPOSInvoice(
            @Body ERPModels.POSInvoice invoice
    );

    @PUT("api/resource/POS Invoice/{name}")
    Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> updatePOSInvoice(
            @Path("name") String invoiceName,
            @Body ERPModels.POSInvoice invoice
    );

    // Submit invoice (set docstatus = 1)
    @POST("api/method/frappe.client.submit")
    Call<ERPModels.SingleResponse<ERPModels.POSInvoice>> submitInvoice(
            @Body Map<String, String> body
    );

    // ─── Payment ──────────────────────────────────────────────────────────────

    @POST("api/method/erpnext.accounts.doctype.pos_invoice.pos_invoice.make_posa_payment_entry")
    Call<ERPModels.SingleResponse<Object>> processPayment(
            @Body ERPModels.PaymentRequest paymentRequest
    );

    // ─── Kitchen Order Ticket ─────────────────────────────────────────────────

    @GET("api/resource/Kitchen Order Ticket")
    Call<ERPModels.ListResponse<ERPModels.KitchenOrderTicket>> getKOTs(
            @Query("filters") String filters,
            @Query("fields") String fields,
            @Query("limit_page_length") int limit
    );

    @PUT("api/resource/Kitchen Order Ticket/{name}")
    Call<ERPModels.SingleResponse<ERPModels.KitchenOrderTicket>> updateKOTStatus(
            @Path("name") String kotName,
            @Body Map<String, String> body
    );

    // ─── POS Profile ──────────────────────────────────────────────────────────

    @GET("api/resource/POS Profile/{name}")
    Call<ERPModels.SingleResponse<ERPModels.POSProfile>> getPOSProfile(
            @Path("name") String profileName
    );

    // ─── URYPOS: Daily Summary ────────────────────────────────────────────────

    @GET("api/method/urypos.api.get_daily_summary")
    Call<ERPModels.SingleResponse<ERPModels.DailySummary>> getDailySummary(
            @Query("date") String date,
            @Query("pos_profile") String posProfile
    );

    // ─── URYPOS: Open POS Session ─────────────────────────────────────────────

    @POST("api/method/urypos.api.open_pos_session")
    Call<ERPModels.SingleResponse<Object>> openPOSSession(
            @Body Map<String, String> body
    );

    // ─── URYPOS: Close POS Session ────────────────────────────────────────────

    @POST("api/method/urypos.api.close_pos_session")
    Call<ERPModels.SingleResponse<Object>> closePOSSession(
            @Body Map<String, Object> body
    );

    // ─── Check Connection / Auth ──────────────────────────────────────────────

    @GET("api/method/frappe.auth.get_logged_user")
    Call<ERPModels.SingleResponse<String>> getLoggedUser();
}
