package com.restaurant.pos.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// ─── ERPNext Generic Response Wrapper ────────────────────────────────────────

public class ERPModels {

    public static class ListResponse<T> {
        @SerializedName("data")
        public List<T> data;

        @SerializedName("message")
        public String message;
    }

    public static class SingleResponse<T> {
        @SerializedName("data")
        public T data;

        @SerializedName("message")
        public String message;
    }

    // ─── Menu Item (ERPNext Item doctype) ────────────────────────────────────

    public static class MenuItem {
        @SerializedName("name")
        public String name;               // item code / ID

        @SerializedName("item_name")
        public String itemName;

        @SerializedName("standard_rate")
        public double standardRate;

        @SerializedName("item_group")
        public String itemGroup;

        @SerializedName("image")
        public String image;

        @SerializedName("description")
        public String description;

        @SerializedName("disabled")
        public int disabled;

        // Local UI helpers (not from API)
        public int quantity = 0;

        public boolean isAvailable() {
            return disabled == 0;
        }
    }

    // ─── Restaurant Table ─────────────────────────────────────────────────────

    public static class RestaurantTable {
        @SerializedName("name")
        public String name;               // table ID e.g. "T-01"

        @SerializedName("table_name")
        public String tableName;

        @SerializedName("status")
        public String status;             // "Available", "Occupied", "Reserved"

        @SerializedName("capacity")
        public int capacity;

        @SerializedName("floor")
        public String floor;

        @SerializedName("current_order")
        public String currentOrder;

        public enum Status {
            AVAILABLE("Available"),
            OCCUPIED("Occupied"),
            RESERVED("Reserved");

            public final String value;
            Status(String v) { this.value = v; }
        }
    }

    // ─── POS Invoice ──────────────────────────────────────────────────────────

    public static class POSInvoice {
        @SerializedName("name")
        public String name;               // Invoice ID e.g. "SINV-00001"

        @SerializedName("customer")
        public String customer;

        @SerializedName("pos_profile")
        public String posProfile;

        @SerializedName("table")
        public String table;

        @SerializedName("status")
        public String status;             // Draft, Submitted, Paid, Cancelled

        @SerializedName("grand_total")
        public double grandTotal;

        @SerializedName("net_total")
        public double netTotal;

        @SerializedName("total_taxes_and_charges")
        public double taxAmount;

        @SerializedName("posting_date")
        public String postingDate;

        @SerializedName("posting_time")
        public String postingTime;

        @SerializedName("items")
        public List<POSInvoiceItem> items;

        @SerializedName("payments")
        public List<POSPayment> payments;

        @SerializedName("contact_mobile")
        public String contactMobile;

        @SerializedName("waiter")
        public String waiter;
    }

    // ─── POS Invoice Item ─────────────────────────────────────────────────────

    public static class POSInvoiceItem {
        @SerializedName("item_code")
        public String itemCode;

        @SerializedName("item_name")
        public String itemName;

        @SerializedName("qty")
        public double qty;

        @SerializedName("rate")
        public double rate;

        @SerializedName("amount")
        public double amount;

        @SerializedName("uom")
        public String uom;
    }

    // ─── POS Payment ──────────────────────────────────────────────────────────

    public static class POSPayment {
        @SerializedName("mode_of_payment")
        public String modeOfPayment;

        @SerializedName("amount")
        public double amount;
    }

    // ─── Kitchen Order Ticket (KOT) ───────────────────────────────────────────

    public static class KitchenOrderTicket {
        @SerializedName("name")
        public String name;

        @SerializedName("pos_invoice")
        public String posInvoice;

        @SerializedName("table")
        public String table;

        @SerializedName("status")
        public String status;             // "New", "Preparing", "Ready", "Served"

        @SerializedName("creation")
        public String creation;

        @SerializedName("items")
        public List<POSInvoiceItem> items;
    }

    // ─── Payment Entry Request ────────────────────────────────────────────────

    public static class PaymentRequest {
        @SerializedName("invoice")
        public String invoice;

        @SerializedName("mode_of_payment")
        public String modeOfPayment;

        @SerializedName("amount")
        public double amount;
    }

    // ─── POS Profile ─────────────────────────────────────────────────────────

    public static class POSProfile {
        @SerializedName("name")
        public String name;

        @SerializedName("company")
        public String company;

        @SerializedName("warehouse")
        public String warehouse;

        @SerializedName("currency")
        public String currency;

        @SerializedName("tax_template")
        public String taxTemplate;
    }

    // ─── Daily Summary (for Dashboard) ───────────────────────────────────────

    public static class DailySummary {
        @SerializedName("total_revenue")
        public double totalRevenue;

        @SerializedName("total_orders")
        public int totalOrders;

        @SerializedName("paid_orders")
        public int paidOrders;

        @SerializedName("pending_orders")
        public int pendingOrders;

        @SerializedName("average_order_value")
        public double averageOrderValue;
    }
}
