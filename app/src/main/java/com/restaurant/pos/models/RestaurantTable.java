package com.restaurant.pos.models;
import com.google.gson.annotations.SerializedName;
public class RestaurantTable {
    public enum Status{AVAILABLE,OCCUPIED,RESERVED,CLEANING}
    @SerializedName("name") private String id;
    @SerializedName("table_name") private String tableName;
    @SerializedName("seating_capacity") private int capacity;
    @SerializedName("status") private String statusStr;
    @SerializedName("floor") private String floor;
    @SerializedName("current_order") private String currentOrderId;
    public String getId(){return id;} public String getTableName(){return tableName!=null?tableName:id;}
    public int getCapacity(){return capacity>0?capacity:4;} public String getFloor(){return floor!=null?floor:"Ground Floor";}
    public String getCurrentOrderId(){return currentOrderId;} public String getStatusStr(){return statusStr;}
    public void setStatus(String s){statusStr=s;} public void setCurrentOrderId(String s){currentOrderId=s;}
    public Status getStatus(){
        if(statusStr==null)return Status.AVAILABLE;
        switch(statusStr.toLowerCase()){
            case"occupied":return Status.OCCUPIED; case"reserved":return Status.RESERVED;
            case"cleaning":return Status.CLEANING; default:return Status.AVAILABLE;
        }
    }
}
