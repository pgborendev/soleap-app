package com.restaurant.pos.adapters;
import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.pos.R;
import com.restaurant.pos.models.MenuItem;
import java.util.*;
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    public interface OnMenuItemClickListener { void onMenuItemClicked(MenuItem item); }
    private List<MenuItem> items=new ArrayList<>();
    private final Context ctx;
    private final OnMenuItemClickListener listener;
    public MenuAdapter(Context ctx,OnMenuItemClickListener l){this.ctx=ctx;this.listener=l;}
    public void setItems(List<MenuItem> items){this.items=items;notifyDataSetChanged();}
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v=LayoutInflater.from(ctx).inflate(R.layout.item_menu,parent,false);
        return new ViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder h,int pos){
        MenuItem m=items.get(pos);
        h.tvName.setText(m.getName());
        h.tvPrice.setText(String.format("$%.2f",m.getPrice()));
        h.tvCategory.setText(m.getCategory());
        h.tvBadge.setVisibility(m.getQuantity()>0?View.VISIBLE:View.GONE);
        if(m.getQuantity()>0)h.tvBadge.setText(String.valueOf(m.getQuantity()));
        h.card.setAlpha(m.isAvailable()?1f:0.5f);
        h.card.setOnClickListener(v->listener.onMenuItemClicked(m));
    }
    @Override public int getItemCount(){return items.size();}
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView card;TextView tvName,tvPrice,tvCategory,tvBadge;
        ViewHolder(View v){
            super(v);card=v.findViewById(R.id.card);tvName=v.findViewById(R.id.tvName);
            tvPrice=v.findViewById(R.id.tvPrice);tvCategory=v.findViewById(R.id.tvCategory);
            tvBadge=v.findViewById(R.id.tvBadge);
        }
    }
}
