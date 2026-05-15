package com.restaurant.pos.adapters;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.pos.R;
import com.restaurant.pos.models.MenuItem;
import java.util.*;
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    public interface OnCartChangeListener {
        void onCartItemIncrement(String itemId);
        void onCartItemDecrement(String itemId);
        void onCartItemRemoved(String itemId);
    }
    private List<MenuItem> items=new ArrayList<>();
    private final Context ctx;
    private final OnCartChangeListener listener;
    public CartAdapter(Context ctx,OnCartChangeListener l){this.ctx=ctx;this.listener=l;}
    public void setItems(List<MenuItem> items){this.items=items;notifyDataSetChanged();}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(ctx).inflate(R.layout.item_cart,parent,false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h,int pos){
        MenuItem m=items.get(pos);
        h.tvName.setText(m.getName());
        h.tvPrice.setText(String.format("$%.2f",m.getPrice()));
        h.tvQty.setText(String.valueOf(m.getQuantity()));
        h.tvSubtotal.setText(String.format("$%.2f",m.getSubtotal()));
        h.btnPlus.setOnClickListener(v->listener.onCartItemIncrement(m.getId()));
        h.btnMinus.setOnClickListener(v->listener.onCartItemDecrement(m.getId()));
        h.btnRemove.setOnClickListener(v->listener.onCartItemRemoved(m.getId()));
    }
    @Override public int getItemCount(){return items.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvPrice,tvQty,tvSubtotal;Button btnPlus,btnMinus,btnRemove;
        ViewHolder(View v){
            super(v);tvName=v.findViewById(R.id.tvName);tvPrice=v.findViewById(R.id.tvPrice);
            tvQty=v.findViewById(R.id.tvQty);tvSubtotal=v.findViewById(R.id.tvSubtotal);
            btnPlus=v.findViewById(R.id.btnPlus);btnMinus=v.findViewById(R.id.btnMinus);
            btnRemove=v.findViewById(R.id.btnRemove);
        }
    }
}
