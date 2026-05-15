package com.restaurant.pos.adapters;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.pos.R;
import com.restaurant.pos.models.Order;
import com.restaurant.pos.utils.CurrencyUtils;
import java.util.*;
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<Order> orders=new ArrayList<>();
    private final Context ctx;
    public OrderAdapter(Context ctx){this.ctx=ctx;}
    public void setOrders(List<Order> o){orders=o;notifyDataSetChanged();}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(ctx).inflate(R.layout.item_order,parent,false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h,int pos){
        Order o=orders.get(pos);
        h.tvId.setText(o.getId());
        h.tvCustomer.setText(o.getCustomer()!=null?o.getCustomer():"Walk-in");
        h.tvTotal.setText(CurrencyUtils.format(o.getGrandTotal()));
        h.tvDate.setText(o.getDate()!=null?o.getDate():"");
        String status=o.getKitchenStatus();
        h.tvKitchenStatus.setText(status.toUpperCase());
        int color;
        switch(status){
            case"cooking":color=R.color.amber;break;
            case"ready":color=R.color.green;break;
            case"served":color=R.color.blue;break;
            default:color=R.color.accent;
        }
        h.tvKitchenStatus.setTextColor(ContextCompat.getColor(ctx,color));
    }
    @Override public int getItemCount(){return orders.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvId,tvCustomer,tvTotal,tvDate,tvKitchenStatus;
        ViewHolder(View v){
            super(v);tvId=v.findViewById(R.id.tvId);tvCustomer=v.findViewById(R.id.tvCustomer);
            tvTotal=v.findViewById(R.id.tvTotal);tvDate=v.findViewById(R.id.tvDate);
            tvKitchenStatus=v.findViewById(R.id.tvKitchenStatus);
        }
    }
}
