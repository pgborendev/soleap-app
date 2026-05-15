package com.restaurant.pos.adapters;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.pos.R;
import com.restaurant.pos.models.Order;
import com.restaurant.pos.utils.CurrencyUtils;
import java.util.*;
public class KitchenOrderAdapter extends RecyclerView.Adapter<KitchenOrderAdapter.ViewHolder> {
    public interface OnKitchenActionListener {
        void onStartCooking(Order order);
        void onMarkReady(Order order);
        void onMarkServed(Order order);
    }
    private List<Order> orders=new ArrayList<>();
    private final Context ctx;
    private final String status;
    private final OnKitchenActionListener listener;
    public KitchenOrderAdapter(Context ctx,String status,OnKitchenActionListener l){
        this.ctx=ctx;this.status=status;this.listener=l;
    }
    public void setOrders(List<Order> o){orders=o;notifyDataSetChanged();}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(ctx).inflate(R.layout.item_kitchen_order,parent,false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h,int pos){
        Order o=orders.get(pos);
        h.tvOrderId.setText(o.getId()!=null?o.getId():"KOT-"+pos);
        h.tvTable.setText("Table: "+(o.getTableId()!=null?o.getTableId():"-"));
        StringBuilder sb=new StringBuilder();
        for(Order.OrderItem item:o.getItems()) sb.append("• ").append(item.getItemName()).append(" x").append((int)item.getQty()).append("\n");
        h.tvItems.setText(sb.toString().trim());
        switch(status){
            case"new":
                h.btnAction.setText("Start Cooking");
                h.btnAction.setOnClickListener(v->listener.onStartCooking(o));
                break;
            case"cooking":
                h.btnAction.setText("Mark Ready");
                h.btnAction.setOnClickListener(v->listener.onMarkReady(o));
                break;
            case"ready":
                h.btnAction.setText("Mark Served");
                h.btnAction.setOnClickListener(v->listener.onMarkServed(o));
                break;
        }
    }
    @Override public int getItemCount(){return orders.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvOrderId,tvTable,tvItems;Button btnAction;
        ViewHolder(View v){
            super(v);tvOrderId=v.findViewById(R.id.tvOrderId);tvTable=v.findViewById(R.id.tvTable);
            tvItems=v.findViewById(R.id.tvItems);btnAction=v.findViewById(R.id.btnAction);
        }
    }
}
