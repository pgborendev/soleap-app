package com.restaurant.pos.models;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
public class Order {
    public enum Status{DRAFT,SUBMITTED,PAID,CANCELLED}
    @SerializedName("name") private String id;
    @SerializedName("customer") private String customer;
    @SerializedName("posting_date") private String date;
    @SerializedName("posting_time") private String time;
    @SerializedName("grand_total") private double grandTotal;
    @SerializedName("net_total") private double netTotal;
    @SerializedName("total_taxes_and_charges") private double taxAmount;
    @SerializedName("status") private String statusStr;
    @SerializedName("items") private List<OrderItem> items=new ArrayList<>();
    @SerializedName("pos_profile") private String posProfile;
    @SerializedName("table") private String tableId;
    @SerializedName("waiter") private String waiter;
    private String kitchenStatus="new";
    public String getId(){return id;} public String getCustomer(){return customer;}
    public String getDate(){return date;} public String getTime(){return time;}
    public double getGrandTotal(){return grandTotal;} public double getNetTotal(){return netTotal;}
    public double getTaxAmount(){return taxAmount;} public List<OrderItem> getItems(){return items;}
    public String getPosProfile(){return posProfile;} public String getTableId(){return tableId;}
    public String getWaiter(){return waiter;} public String getKitchenStatus(){return kitchenStatus;}
    public void setId(String v){id=v;} public void setCustomer(String v){customer=v;}
    public void setItems(List<OrderItem> v){items=v;} public void setPosProfile(String v){posProfile=v;}
    public void setTableId(String v){tableId=v;} public void setWaiter(String v){waiter=v;}
    public void setGrandTotal(double v){grandTotal=v;} public void setNetTotal(double v){netTotal=v;}
    public void setTaxAmount(double v){taxAmount=v;} public void setKitchenStatus(String v){kitchenStatus=v;}
    public Status getStatus(){
        if(statusStr==null)return Status.DRAFT;
        switch(statusStr){case"Submitted":return Status.SUBMITTED;case"Paid":return Status.PAID;case"Cancelled":return Status.CANCELLED;default:return Status.DRAFT;}
    }
    public static class OrderItem{
        @SerializedName("item_code") private String itemCode;
        @SerializedName("item_name") private String itemName;
        @SerializedName("qty") private double qty;
        @SerializedName("rate") private double rate;
        @SerializedName("amount") private double amount;
        @SerializedName("uom") private String uom="Nos";
        private String notes;
        public OrderItem(MenuItem m){itemCode=m.getItemCode();itemName=m.getName();qty=m.getQuantity();rate=m.getPrice();amount=m.getSubtotal();}
        public String getItemCode(){return itemCode;} public String getItemName(){return itemName;}
        public double getQty(){return qty;} public double getRate(){return rate;}
        public double getAmount(){return amount;} public String getUom(){return uom;}
        public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    }
}
