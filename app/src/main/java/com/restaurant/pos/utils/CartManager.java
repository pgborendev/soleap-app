package com.restaurant.pos.utils;
import com.restaurant.pos.models.MenuItem;
import java.util.ArrayList;
import java.util.List;
public class CartManager {
    private static CartManager instance;
    private final List<MenuItem> cartItems=new ArrayList<>();
    private String selectedTableId,selectedTableName,customerName="Walk-in Customer",notes;
    public static synchronized CartManager getInstance(){if(instance==null)instance=new CartManager();return instance;}
    public void addItem(MenuItem item){
        for(MenuItem ci:cartItems){if(ci.getId().equals(item.getId())){ci.setQuantity(ci.getQuantity()+1);return;}}
        MenuItem c=clone(item);c.setQuantity(1);cartItems.add(c);
    }
    public void removeItem(String id){cartItems.removeIf(ci->ci.getId().equals(id));}
    public void incrementItem(String id){for(MenuItem ci:cartItems){if(ci.getId().equals(id)){ci.setQuantity(ci.getQuantity()+1);return;}}}
    public void decrementItem(String id){
        for(int i=0;i<cartItems.size();i++){
            if(cartItems.get(i).getId().equals(id)){
                if(cartItems.get(i).getQuantity()<=1)cartItems.remove(i);
                else cartItems.get(i).setQuantity(cartItems.get(i).getQuantity()-1);return;
            }
        }
    }
    public List<MenuItem> getCartItems(){return new ArrayList<>(cartItems);}
    public int getItemCount(){return cartItems.size();}
    public boolean isEmpty(){return cartItems.isEmpty();}
    public double getSubtotal(){double t=0;for(MenuItem ci:cartItems)t+=ci.getSubtotal();return t;}
    public double getTax(double r){return getSubtotal()*r;}
    public double getGrandTotal(double r){return getSubtotal()+getTax(r);}
    public void setTable(String id,String name){selectedTableId=id;selectedTableName=name;}
    public String getSelectedTableId(){return selectedTableId;}
    public String getSelectedTableName(){return selectedTableName;}
    public String getCustomerName(){return customerName;}
    public String getNotes(){return notes;}
    public void setCustomerName(String n){customerName=n;}
    public void setNotes(String n){notes=n;}
    public void clearCart(){cartItems.clear();customerName="Walk-in Customer";notes=null;}
    private MenuItem clone(MenuItem o){MenuItem c=new MenuItem();c.setId(o.getId());c.setName(o.getName());c.setPrice(o.getPrice());c.setCategory(o.getCategory());c.setQuantity(0);return c;}
}
