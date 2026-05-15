package com.restaurant.pos.models;
import com.google.gson.annotations.SerializedName;
public class MenuItem {
    @SerializedName("name") private String id;
    @SerializedName("item_name") private String name;
    @SerializedName("item_code") private String itemCode;
    @SerializedName("standard_rate") private double price;
    @SerializedName("item_group") private String category;
    @SerializedName("image") private String imageUrl;
    @SerializedName("description") private String description;
    @SerializedName("disabled") private int disabled;
    private int quantity=0;
    public String getId(){return id;} public String getName(){return name;}
    public String getItemCode(){return itemCode!=null?itemCode:id;}
    public double getPrice(){return price;} public String getCategory(){return category;}
    public String getImageUrl(){return imageUrl;} public String getDescription(){return description;}
    public boolean isAvailable(){return disabled==0;} public int getQuantity(){return quantity;}
    public void setId(String v){id=v;} public void setName(String v){name=v;}
    public void setPrice(double v){price=v;} public void setCategory(String v){category=v;}
    public void setQuantity(int v){quantity=v;} public double getSubtotal(){return price*quantity;}
}
